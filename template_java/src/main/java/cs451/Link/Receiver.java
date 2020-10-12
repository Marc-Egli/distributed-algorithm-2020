package cs451.Link;


import cs451.Customer;
import cs451.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Receiver extends Thread{

    private FairlossLink fairlossLink;
    private Customer customer;
    private List<Message> receivedACKS = new ArrayList<>();
    private List<UUID> receivedBroadcast = new ArrayList<>();
    private Sender sender;

    public Receiver(Customer customer,Sender sender, FairlossLink fairlosslink){
        this.customer =customer;
        this.sender = sender;
        this.fairlossLink = fairlosslink;
    }
    @Override
    public void run(){
        while(true){
            Message m = fairlossLink.receive();
            switch (m.getType()) {
                case BROADCAST :
                    if (!receivedBroadcast.contains(m.getUid())) {
                        receivedBroadcast.add(m.getUid());
                        new Thread(() -> customer.deliver(m)).start();
                    }
                    sender.send(m.generateAck());

                    break;

                case ACK:
                    //Not correct if
                    if(!receivedACKS.contains(m)){
                        System.out.println("Received ACK for " + m.getUid());
                        receivedACKS.add(m);
                        sender.notifyAck(m);
                    }
                    break;
            }
        }

    }

    public void getAcks(){
        System.out.println(receivedBroadcast);
    }

}
