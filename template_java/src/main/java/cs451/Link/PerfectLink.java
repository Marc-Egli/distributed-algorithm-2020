package cs451.Link;


import cs451.*;

import java.util.UUID;

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

    public void send(Message content, int dstPort, String dstIp) {
        UUID uid = UUID.randomUUID();
        Message uniqueMessage = content.addIpLayer(linkPort,linkIp,dstPort,dstIp,uid);

        if(linkPort == dstPort && dstIp.equals(linkIp)){
            new Thread(() -> deliver(uniqueMessage)).start();
        }else {
            sender.send(uniqueMessage);
        }


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



