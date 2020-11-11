package cs451.Link;

import cs451.Messages.Message;

import java.io.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * Representation of a Fairloss Link using an UDP socket
 * Maintains a cache a size CACHE_CAPACITY to optimize the serialization process
 */
public class FairlossLink {
    private final int CACHE_CAPACITY = 300;
    private final Comparator<Message> strictMessageComparator = (Message m1, Message m2) -> {
        if (m1.getUid().equals(m2.getUid())) {
            return m1.getType().compareTo(m2.getType());
        }
        return m1.getSignature().compareTo(m2.getSignature());
    };
    private DatagramSocket socket;
    private final HashMap<Message, byte[]> cache = new HashMap(CACHE_CAPACITY);

    /**
     * Low level UDP messaging abstraction
     *
     * @param port The port the socket is listening to
     * @param ip   The IP of the host the socket is running on
     */
    public FairlossLink(int port, String ip) {
        try {
            this.socket = new DatagramSocket(port, InetAddress.getByName(ip));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The cache contains the mapping from a Message to a byte[].
     * Before we serialize a Message we thus first check if the value was already computed or not.
     * To maintain a constant cache size we reduce it size every time the cache grows to big
     * Note that all Messages are sorted by Signature, therefore when we discard cache entries we
     * remove the lowest SEQ messages as they are more likely to be already Acked
     */
    public void reduceCache() {
        if (cache.size() > CACHE_CAPACITY) {
            List<Message> ks = new ArrayList<>(cache.keySet());
            Collections.sort(ks, strictMessageComparator);
            System.out.println("CLEARED CACHE");

            for (int i = 0; i < 100; i++) {
                cache.remove(ks.get(i));
            }
            System.out.println(cache.size());
        }
    }


    /**
     * Sends UDP DatagramPacket to the destination IP contained in Message
     *
     * @param m The message to send
     */
    public void send(Message m) {
        byte[] buf = cache.computeIfAbsent(m, this::serialize);
        try {
            DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(m.getDstIp()), m.getDstPort());
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * Receives UDP DatagramPacket
     *
     * @return Message
     */
    public Message receive() {
        byte[] receive = new byte[65535];
        DatagramPacket packet = new DatagramPacket(receive, receive.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return deserialize(packet.getData());
    }

    /**
     * Serializes a Message Object into a byte array
     *
     * @param m The message to serialize
     * @return byteMessage
     */
    public byte[] serialize(Message m) {
        byte[] byteMessage = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(m);
            out.flush();
            byteMessage = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
            }
        }
        return byteMessage;
    }

    /**
     * Deserializes a byte array into Message Object
     *
     * @param byteMessage The byteArray to deserialize
     * @return message
     */
    public Message deserialize(byte[] byteMessage) {
        Message message = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(byteMessage);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            message = (Message) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }
        }
        return message;
    }

}
