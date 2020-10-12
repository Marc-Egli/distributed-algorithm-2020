package cs451.Link;

import cs451.Message;

import javax.xml.crypto.Data;
import java.io.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;



public class FairlossLink {

    private DatagramSocket socket;

    /**
     * Low level UDP messaging abstraction
     * @param port The port the socket is listening to
     * @param ip The IP of the host the socket is running on
     */
    public FairlossLink(int port, String ip) {
        try {
            this.socket = new DatagramSocket(port, InetAddress.getByName(ip));
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Sends UDP DatagramPacket to the destination IP contained in Message
     * @param m The message to send
     */
    public synchronized void send(Message m)  {
        byte buf[] = serialize(m);
        try {
            DatagramPacket packet = new DatagramPacket(buf,buf.length,InetAddress.getByName(m.getDstIp()),m.getDstPort());
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void close(){
        this.socket.close();
    }

    /**
     * Receives UDP DatagramPacket
     * @return Message
     */
    public Message receive() {
        byte[] receive = new byte[65535];
        DatagramPacket packet = new DatagramPacket(receive,receive.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return deserialize(packet.getData());
    }

    /**
     * Serializes a Message Object into a byte array
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
     * @param byteMessage The byteArray to deserialize
     * @return message
     */
    public Message deserialize(byte[] byteMessage){
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
