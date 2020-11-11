package cs451.Link;

import cs451.Observer;
import cs451.Messages.Message;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Receiver class runs on a separated thread. Therefore it can continuously work on receiving messages.
 * This class also handles all the Ack generation i.e for each BROADCAST received it will generate a
 * corresponding ACK message to notify the sender that the BROADCAST was received
 */
public class Receiver extends Thread {

    private final FairlossLink fairlossLink;
    private Observer observer;
    private final HashSet<UUID> receivedACKS = new HashSet<>();
    private final HashSet<UUID> receivedBroadcast = new HashSet<>();
    private final Sender sender;
    private final ThreadPoolExecutor executor;

    /**
     * Constructs a receiver
     * @param sender sender of the perfect link
     * @param fairlosslink underlying fairloss link
     */
    public Receiver(Sender sender, FairlossLink fairlosslink) {
        this.sender = sender;
        this.fairlossLink = fairlosslink;
        this.executor = new ThreadPoolExecutor(2, 8, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));
    }


    /**
     * Receives a new message from the fairloss link.
     * In case of a BROADCAST message generate an ACK and deliver the message to the
     * perfect link
     * In case of an ACK message notify the sender to stop sending the corresponding message
     * Also makes sure to only receive a message once by keeping track of all previously received messages
     */
    @Override
    public void run() {
        while (true) {
            Message m = fairlossLink.receive();
            switch (m.getType()) {
                case BROADCAST:
                    if (!receivedBroadcast.contains(m.getUid())) {
                        receivedBroadcast.add(m.getUid());
                        executor.execute(() -> observer.deliver(m));
                    }
                    sender.send(m.generateAck());
                    break;

                case ACK:
                    if (!receivedACKS.contains(m.getUid())) {
                        //System.out.println("Received ACK for " + m.getUid());
                        receivedACKS.add(m.getUid());
                        sender.notifyAck(m);
                    }
                    break;
            }
        }

    }


    /**
     * Adds an unique observer to the receiver
     * @param observer
     */
    public void setObserver(Observer observer) {
        this.observer = observer;
    }

}
