package cs451;


import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Sender extends Thread {

    private LinkedBlockingQueue<Message> toSend = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Message> receivedAcks = new LinkedBlockingQueue<>();
    private FairlossLink fairlossLink;

    public Sender(FairlossLink fairlossLink){
        this.fairlossLink = fairlossLink;
    }
    @Override
    public void run(){

        while(true) {
            while(toSend.isEmpty()){}
            ArrayList<Message> acks = new ArrayList<>();
            for(Message message : toSend){
                switch(message.getType()) {
                    case BROADCAST:
                        fairlossLink.send(message);
                        break;

                    case ACK:
                        fairlossLink.send(message);
                        acks.add(message);
                        break;
                }
            }
            toSend.removeAll(acks);
        }

     }


    public void send(Message message)  {
        try {
            toSend.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Notifies the sender that there was an ACK received for one broadcast message
     * It will then remove the concerned message from the send queue
     *
     * @param ack The ACK corresponding to a broadcast message
     */
    public void notifyAck(Message ack)  {
        Message toRemove = null;
        for(Message message : toSend){
            if(message.getUid().equals(ack.getUid())){
                toRemove = message;
            }
        }
        toSend.remove(toRemove);

    }
}
