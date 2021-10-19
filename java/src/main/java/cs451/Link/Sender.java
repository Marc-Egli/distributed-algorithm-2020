package cs451.Link;

import cs451.Messages.Message;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Sender class runs on a separated thread. This allows to constantly send messages to their destinations.
 */
public class Sender extends Thread {

    private final int CAPACITY = 100;
    private final int ACK_SEND = 1;
    private final FairlossLink fairlossLink;
    //Orders messages by their Signature and then by their UUID
    private final Comparator<Message> UUIdComparator = new Comparator<Message>() {
        @Override
        public int compare(Message message, Message m1) {
            if (message.getSignature().equals(m1.getSignature())) {
                return message.getUid().compareTo(m1.getUid());
            } else {
                return message.getSignature().compareTo(m1.getSignature());
            }
        }
    };
    //Buffers to store the pending ACKS and BROADCASTS
    private final ConcurrentSkipListSet<Message> broadcastBuffer = new ConcurrentSkipListSet<>(UUIdComparator);
    private final ConcurrentSkipListSet<Message> ackBuffer = new ConcurrentSkipListSet<>(UUIdComparator);
    //The messages to send
    private final ConcurrentHashMap<UUID, Message> broadcast = new ConcurrentHashMap<>(CAPACITY);
    private final HashSet<Message> ack = new HashSet<>(CAPACITY);
    private int counter = 0;

    /**
     * Creates a sender with a fairloss link
     *
     * @param fairlossLink a fairloss link
     */
    public Sender(FairlossLink fairlossLink) {
        this.fairlossLink = fairlossLink;
    }


    /**
     * First checks that we send the maximum of messages.
     * Then sends all messages in broadcast and ack
     * The ack messages are only send ACK_SEND times, then the Set is cleared
     * At each send cycle we also try to reduce the cache size in the fairloss link
     */
    @Override
    public void run() {

        while (true) {
            //Refill the messages to send
            fillBroadcast();
            fillAck();

            //Send all messages in broadcast and ack

            for (Map.Entry<UUID, Message> entry : broadcast.entrySet()) {
                fairlossLink.send(entry.getValue());
            }

            for (Message message : ack) {
                fairlossLink.send(message);
            }


            //The ack
            if (counter == ACK_SEND) {
                ack.clear();
                counter = 0;
            }
            counter++;

            fairlossLink.reduceCache();


        }

    }

    /**
     * Refills the broadcast messages to be sent from the corresponding buffer
     */
    private void fillBroadcast() {
        while (broadcast.size() < CAPACITY) {
            Message m = broadcastBuffer.pollFirst();
            if (m != null) {
                broadcast.put(m.getUid(), m);
            } else {
                break;
            }
        }
    }

    /**
     * Refills the ack messages to be sent from the corresponding buffer
     */
    private void fillAck() {
        while (ack.size() < CAPACITY) {
            Message m = ackBuffer.pollFirst();
            if (m != null) {
                ack.add(m);
            } else {
                break;
            }
        }
    }


    /**
     * Puts a message into the corresponding buffer
     * Once there, the message will be eventually put onto the corresponding sending list
     *
     * @param message The message to send
     */
    public void send(Message message) {
        switch (message.getType()) {
            case ACK:
                ackBuffer.add(message);
                break;
            case BROADCAST:
                broadcastBuffer.add(message);
                break;
        }
    }

    /**
     * Notifies the sender that there was an ACK received for one broadcast message
     * It will then remove the concerned message from the broadcast queue
     *
     * @param ack The ACK corresponding to a broadcast message
     */
    public void notifyAck(Message ack) {
        broadcast.remove(ack.getUid());

    }
}
