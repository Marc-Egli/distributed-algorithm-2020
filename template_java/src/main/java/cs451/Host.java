package cs451;

import cs451.Broadcast.FifoBroadcast;
import cs451.Link.PerfectLink;
import cs451.Messages.Message;
import cs451.Messages.MessageType;
import cs451.Messages.Signature;

import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Host implements Observer {
    private static final String IP_START_REGEX = "/";
    private int id;
    private String ip;
    private int port = -1;
    private int numMessages = -1;
    private static ConcurrentLinkedQueue<String> outputBuffer;
    //This List has to be thread safe has the main thread will constantly check if the messages have arrived and the
    //messanger thread will constantly add new delivered messages to it
    public List<Message> delivered = Collections.synchronizedList(new ArrayList<>());
    private final List<Message> own = Collections.synchronizedList(new ArrayList<>());
    private FifoBroadcast broadcast;


    public boolean init(List<Host> hosts, int numMessages, ConcurrentLinkedQueue outputBuffer) {
        this.numMessages = numMessages;
        //You can use UniformReliableBroadcast or Fifo hear also
        this.broadcast = new FifoBroadcast(new PerfectLink(port, ip), hosts, this);
        this.outputBuffer = outputBuffer;
        System.out.println("Host " + id + "has port " + port);

        return true;
    }

    public boolean populate(String idString, String ipString, String portString) {
        try {
            id = Integer.parseInt(idString);

            String ipTest = InetAddress.getByName(ipString).toString();
            if (ipTest.startsWith(IP_START_REGEX)) {
                ip = ipTest.substring(1);
            } else {
                ip = InetAddress.getByName(ipTest.split(IP_START_REGEX)[0]).getHostAddress();
            }

            port = Integer.parseInt(portString);
            if (port <= 0) {
                System.err.println("Port in the hosts file must be a positive number!");
                return false;
            }


        } catch (NumberFormatException e) {
            if (port == -1) {
                System.err.println("Id in the hosts file must be a number!");
            } else {
                System.err.println("Port in the hosts file must be a number!");
            }
            return false;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        return true;
    }

    /**
     * The hosts starts sending all the message he has to
     */
    public void start() {
        for (int i = 1; i <= numMessages; i++) {
            Signature sign = new Signature(this.id, i);
            Message model = new Message(String.valueOf(i), MessageType.BROADCAST, sign);
            broadcast.broadcast(model);
            outputBuffer.add("b " + i);
        }

        while (own.size() < numMessages) {
        }
        System.out.println("Done");
    }

    /**
     * This is a callback function invoked by the broadcast that a message can be delivered
     *
     * @param message
     */
    @Override
    public void deliver(Message message) {
        delivered.add(message);
        System.err.println("Receiving " + message.getSignature().toString());
        if (message.getSignature().getHostId() == this.id) {
            own.add(message);
        }
        outputBuffer.add("d " + message.getSignature().getHostId() + " " + message.getSignature().getSeq());
    }

    public int getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }


    @Override
    public String toString() {
        return "Host " + id;
    }


}
