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


    public PerfectLink(int port, String ip) {
        this.linkIp = ip;
        this.linkPort = port;
        this.fairlossLink = new FairlossLink(port, ip);
        this.delivered = new ArrayList<>();
        this.receiver = new Receiver(fairlossLink);
        receiver.start();
    }

    public void send(String m, int dstPort, String dstIp) {
        UUID uid = UUID.randomUUID();
        Message message = new Message(linkIp, linkPort, dstIp, dstPort, m, BROADCAST, uid);

        while (true) {
            fairlossLink.send(message);


            Message broadcast = receiver.getReceivedMessage();
            if (broadcast != null) {
                fairlossLink.send(broadcast.generateAck());
                System.out.println("Ack sent for " + broadcast.getUid());
            }
            Message ack = receiver.getAckMessage();
            if (ack != null) {
                if (ack.getUid().equals(message.getUid())) {
                    System.out.println("Now breaking from sending");
                    break;
                }
            }

        }


    }


    public Message receive() {
        return receiver.getReceivedMessage();
    }


    public void close() {
        this.fairlossLink.close();
    }

}



