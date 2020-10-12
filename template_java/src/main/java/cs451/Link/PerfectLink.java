package cs451.Link;


import cs451.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static cs451.MessageType.BROADCAST;


public class PerfectLink implements Customer {

    private FairlossLink fairlossLink;
    public Receiver receiver;
    private int linkPort;
    private String linkIp;
    private Sender sender;
    private Customer customer;


    public PerfectLink(int port, String ip) {
        this.linkIp = ip;
        this.linkPort = port;
        this.fairlossLink = new FairlossLink(port, ip);
        this.sender = new Sender(fairlossLink);
        this.receiver = new Receiver(this,sender,fairlossLink);
        receiver.start();
        sender.start();
    }

    public void send(String m, int dstPort, String dstIp) {
        UUID uid = UUID.randomUUID();
        Message message = new Message(linkIp, linkPort, dstIp, dstPort, m, BROADCAST, uid);
        sender.send(message);


    }

    public void deliver(Message message){
        customer.deliver(message);
    }

    public void setObserver(Customer customer) {
        this.customer = customer;
    }


    public void close() {
        receiver.getAcks();
    }

}



