package cs451.Broadcast;

import cs451.*;
import cs451.Link.PerfectLink;
import cs451.Messages.Message;
import cs451.Messages.MessageType;
import cs451.Messages.Signature;
import cs451.Observer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Implementation of uniform reliable broadcast
 */
public class UniformReliableBroadcast implements Observer {

    private final BestEffortBroadcast beb;
    private final ConcurrentSkipListSet<Signature> forwarded;
    private final ConcurrentHashMap<Signature, List<Host>> ack;
    private final PerfectLink perfectLink;
    private final List<Host> hosts;
    private final ConcurrentSkipListSet<Signature> delivered;
    private final HashMap<Integer, Host> LOOKUP; //Need to modify this
    private final int MIN_MSG;
    private final Observer observer;


    /**
     * Creates a uniform reliable broadcast object
     * @param perfectLink perfect link of the host
     * @param hosts all known hosts including itself
     * @param observer the observer to deliver the messages
     */
    public UniformReliableBroadcast(PerfectLink perfectLink, List<Host> hosts, Observer observer) {
        beb = new BestEffortBroadcast(perfectLink, hosts, this);

        //TODO change the Sets into maps to have a contains lookup cost O(1) + c
        this.forwarded = new ConcurrentSkipListSet<>();
        this.ack = new ConcurrentHashMap<>();
        this.hosts = hosts;
        this.perfectLink = perfectLink;
        this.delivered = new ConcurrentSkipListSet<>();
        this.observer = observer;
        this.MIN_MSG = (hosts.size() / 2) + 1;
        this.LOOKUP = buildLookupTable();

    }

    /**
     * Invoked by Beb
     * If the message was already delivered we do not consider it, else
     * we add the sender to the list of hosts that sent us this message.
     * We then check if the message can be delivered or not
     * If not already forwarded we broadcast it.
     * @param message The message to deliver
     */
    @Override
    public void deliver(Message message) {

        //If the message got already delivered we do nothing
        if (!delivered.contains(message.getSignature())) {
            Host sender = LOOKUP.get(message.getSrcPort());
            Signature signature = message.getSignature();

            //Remember that we received this message from host h
            if (!ack.getOrDefault(signature, new ArrayList<>()).contains(sender)) {
                ack.putIfAbsent(signature, new ArrayList<>());
                ack.get(signature).add(sender);
                checkDeliverable(message.getSignature());
            }

            //If not already forwarded, we forward it by broadcasting it. Only done once per message Signature
            if (!forwarded.contains(signature)) {
                forwarded.add(signature);
                beb.broadcast(message);
            }

        }
    }

    /**
     * Broadcasts Message message by first remembering that the corresponding signature was forwared.
     * Then best effort broadcasts it
     * @param message the message to broadcast
     */
    public void broadcast(Message message) {
        forwarded.add(message.getSignature());
        beb.broadcast(message);
    }


    /**
     * Builds a lookup table which maps a port to a host.
     * This allows to quickly recover which host has sent what
     * @return
     */
    private HashMap<Integer, Host> buildLookupTable() {
        HashMap lookup = new HashMap<Integer, Host>();
        for (Host h : this.hosts) {
            lookup.put(h.getPort(), h);
        }
        return lookup;
    }

    /**
     * Checks if the Message with Signature signature can be delivered or not
     * Has to be synchronized as multiple threads could deliver the same message in the same time
     * @param signature The signature identifying a unique message with host ID and Sequence Number
     */
    private synchronized void checkDeliverable(Signature signature) {
        if (ack.get(signature).size() >= MIN_MSG) {
            delivered.add(signature);
            ack.remove(signature);
            forwarded.remove(signature);
            observer.deliver(new Message(String.valueOf(signature.getSeq()), MessageType.BROADCAST, signature));
        }
    }


}
