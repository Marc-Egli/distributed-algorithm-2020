package cs451.Link;


import cs451.Link.FairlossLink;
import cs451.Message;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Sender extends Thread {

    private LinkedBlockingQueue<Message> toSend = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Message> receivedAcks = new LinkedBlockingQueue<>();
    private FairlossLink fairlossLink;

    public Sender(FairlossLink fairlossLink){
        this.fairlossLink = fairlossLink;
    }

    /**
     * Sends all messages on the send queue.
     * At each iteration removes the ACK from the send queue.
     * TODO can improve the ACK sending algorithm
     */
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

    /**
     * Puts a message into the sending queue
     * Once there, the message will be sent until the sender receives the corresponding
     * Ack callback, which confirms that the message has been received
     * @param message
     */
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
