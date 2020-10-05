package cs451;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static cs451.MessageType.BROADCAST;


public class PerfectLink {

    private FairlossLink fairlossLink;
    private Receiver receiver;
    private List<String> delivered;
    private int linkPort;
    private String linkIp;
    private Sender sender;


    public PerfectLink(int port, String ip) {
        this.linkIp = ip;
        this.linkPort = port;
        this.fairlossLink = new FairlossLink(port, ip);
        this.delivered = new ArrayList<>();
        this.sender = new Sender(fairlossLink);
        this.receiver = new Receiver(sender,fairlossLink);
        receiver.start();
        sender.start();
    }

    public void send(String m, int dstPort, String dstIp) {
        UUID uid = UUID.randomUUID();
        Message message = new Message(linkIp, linkPort, dstIp, dstPort, m, BROADCAST, uid);
        sender.send(message);


    }


    public Message receive() {
        return receiver.getReceivedMessage();
    }


    public void close() {
        this.fairlossLink.close();
    }

}



