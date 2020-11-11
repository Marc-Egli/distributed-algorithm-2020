package cs451.Broadcast;

import cs451.Observer;
import cs451.Host;
import cs451.Link.PerfectLink;
import cs451.Messages.Message;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * First-in First-out broadcast implementation
 */
public class FifoBroadcast implements Observer {

    //Compares only the sequence number of the message
    private final Comparator<Message> orderComparator = (Message m1, Message m2) -> Integer.compare(m1.getSignature().getSeq(), m2.getSignature().getSeq());
    private final Observer observer;
    private final UniformReliableBroadcast uniformReliableBroadcast;
    private final ConcurrentHashMap<Integer, ConcurrentSkipListSet<Message>> pending = new ConcurrentHashMap<>();
    private final AtomicIntegerArray order;

    /**
     * Creates a new Fifo broadcast on top of URB
     * @param perfectLink perfect link of the host
     * @param hosts all known hosts including itself
     * @param observer the observer to deliver the messages
     */
    public FifoBroadcast(PerfectLink perfectLink, List<Host> hosts, Observer observer) {
        this.observer = observer;
        this.uniformReliableBroadcast = new UniformReliableBroadcast(perfectLink, hosts, this);
        this.order = new AtomicIntegerArray(hosts.size());

    }

    /**
     * Broadcasts a Message by invoking URB broadcast
     * @param message the message to broadcast
     */
    public void broadcast(Message message) {
        uniformReliableBroadcast.broadcast(message);
    }


    /**
     * Invoked by URB deliver
     * The goal is to deliver the message in growing sequence number.
     * If the sequence number of the message is the next sequence number we expect we deliver
     * and increment the expected sequence number of the corresponding host.Then recursively
     * check if we can deliver messages in pending.
     * If the sequence number is out of order we put the message into pending
     * @param message
     */
    @Override
    public void deliver(Message message) {
        int hostId = message.getSignature().getHostId();
        int currentMessage = order.get(hostId - 1);
        if (currentMessage == message.getSignature().getSeq() - 1) {

            observer.deliver(message);
            order.getAndIncrement(hostId - 1);
            ConcurrentSkipListSet<Message> awaiting_messages = pending.getOrDefault(hostId, new ConcurrentSkipListSet<>(orderComparator));

            if (!awaiting_messages.isEmpty()) {
                Message next = awaiting_messages.first();
                if (next.getSignature().getSeq() - 1 == order.get(hostId - 1)) {
                    deliver(awaiting_messages.pollFirst());
                }
            }

        } else {
            ConcurrentSkipListSet<Message> tmp = pending.getOrDefault(hostId, new ConcurrentSkipListSet<>(orderComparator));
            tmp.add(message);
            pending.put(hostId, tmp);
        }

    }


}
