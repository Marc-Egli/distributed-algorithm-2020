package cs451.Broadcast;


import cs451.Host;
import cs451.Link.PerfectLink;
import cs451.Messages.Message;
import cs451.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of best effort broadcast
 */
public class BestEffortBroadcast implements Observer,Broadcast {
    private final List<Host> hosts;
    private final PerfectLink perfectLink;
    private Observer observer;

    /**
     * Creates a best effort broadcast object
     *
     * @param perfectLink perfect link of the host
     * @param hosts       all kown hosts including itself
     * @param observer    z
     */
    public BestEffortBroadcast(PerfectLink perfectLink, List<Host> hosts, Observer observer) {
        this.observer = observer;
        this.perfectLink = perfectLink;
        this.hosts = new ArrayList<>(hosts);
        perfectLink.setObserver(this);

    }

    /**
     * Broadcasts the message to all hosts
     *
     * @param model incomplete message with no destination port and ip
     */
    public void broadcast(Message model) {
        for (Host h : hosts) {
            perfectLink.send(model, h.getPort(), h.getIp());
        }


    }


    /**
     * Delivers message to the observer
     *
     * @param message The Message to deliver
     */
    @Override
    public void deliver(Message message) {
        observer.deliver(message);
    }





}
