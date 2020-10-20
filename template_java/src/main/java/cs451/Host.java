package cs451;


import cs451.Broadcast.UniformReliableBroadcast;
import cs451.Link.PerfectLink;



import java.net.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Host implements Customer {

    private static final String IP_START_REGEX = "/";
    private int id;
    private String ip;
    private int port = -1;
    private List<Host> targetHosts;
    private int numMessages = -1;
    private static ConcurrentHashMap<Long,String> outputBuffer;
    //This List has to be thread safe has the main thread will constantly check if the messages have arrived and the
    //messanger thread will constantly add new delivered messages to it
    public List<Message> delivered = Collections.synchronizedList(new ArrayList<>());
    private UniformReliableBroadcast uniformReliableBroadcast;


    public boolean init(List<Host> hosts, int numMessages,ConcurrentHashMap outputBuffer) {
        this.targetHosts = new ArrayList<>(hosts);
        this.targetHosts.remove(this);
        this.numMessages = numMessages;
        this.uniformReliableBroadcast = new UniformReliableBroadcast(this,new PerfectLink(port, ip),this,targetHosts);
        this.outputBuffer = outputBuffer;


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
    public void start()  {
        for(int i = 1 ; i <= numMessages; i ++) {
            Signature sign = new Signature(this.id,i);
            Message message = new Message(String.valueOf(i),MessageType.BROADCAST,sign);
            outputBuffer.put(System.nanoTime(),"b " + this.id);
            uniformReliableBroadcast.broadcast(message);
        }

        while(delivered.size() < numMessages * targetHosts.size()) {}
        uniformReliableBroadcast.close();
    }

    /**
     * This is a callback function invoked by the broadcast that a message can be delivered
     * @param message
     */
    @Override
    public void deliver(Message message) {
        delivered.add(message);
        outputBuffer.put(System.nanoTime(),"d " + message.getSignature().getHostId() + " "+  message.getSignature().getContent());
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






}
