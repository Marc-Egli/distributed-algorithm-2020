package cs451.Link;


import cs451.*;
import cs451.Messages.Message;

import java.util.UUID;

/**
 * Perefect link implementation using a fairlosslink, a sender and a receiver
 */
public class PerfectLink implements Observer {

    private FairlossLink fairlossLink;
    public Receiver receiver;
    private final int linkPort;
    private final String linkIp;
    private final Sender sender;
    private Observer observer;

    /**
     * Creates a Perfect link with IP ip and Port port
     *
     * @param port The port of the perfect link
     * @param ip   The i of the perfect link
     */
    public PerfectLink(int port, String ip) {
        this.linkIp = ip;
        this.linkPort = port;
        this.fairlossLink = new FairlossLink(port, ip);
        this.sender = new Sender(fairlossLink);
        this.receiver = new Receiver(sender, fairlossLink);
        receiver.setObserver(this);
        receiver.start();
        sender.start();
    }

    /**
     * Sends a message. If the message is addressed to ourself we directly deliver,
     * else we forward the message to the sender
     * We also generate an unique ID for the message to be able to differentiate them
     *
     * @param content
     * @param dstPort
     * @param dstIp
     */
    public void send(Message content, int dstPort, String dstIp) {
        UUID uid = UUID.randomUUID();
        Message uniqueMessage = content.addIpLayer(linkPort, linkIp, dstPort, dstIp, uid);

        if (linkPort == dstPort && dstIp.equals(linkIp)) {
            //Deliver to ourself
            deliver(uniqueMessage);
        } else {
            //Forward to the sender
            sender.send(uniqueMessage);
        }


    }

    /**
     * Deliver the message to the customer class
     *
     * @param message
     */
    public void deliver(Message message) {
        observer.deliver(message);
    }

    /**
     * Adds an unique observer to the perfect link
     *
     * @param observer
     */
    public void setObserver(Observer observer) {
        this.observer = observer;
    }

}



