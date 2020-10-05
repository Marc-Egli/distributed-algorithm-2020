package cs451;


import java.util.ArrayList;
import java.util.List;


public class Receiver extends Thread{

    private FairlossLink fairlossLink;
    private List<Message> receivedACKS = new ArrayList<>();
    private List<Message> receivedBroadcast = new ArrayList<>();
    private Sender sender;
    public Receiver(Sender sender, FairlossLink fairlosslink){
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
                        receivedBroadcast.add(m);
                    }
                    sender.send(m.generateAck());
                    System.out.println("Received Broadcast " + m.getUid());

                    break;

                case ACK:
                    if(!receivedACKS.contains(m)){
                        receivedACKS.add(m);
                        sender.notifyAck(m);
                        System.out.println("Received ACK " + m.getUid());
                    }
                    break;
            }
        }

    }

    public Message getReceivedMessage(){
        return receivedBroadcast.get(0);
    }


}
