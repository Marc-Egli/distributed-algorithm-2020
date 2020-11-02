package cs451.Broadcast;


import cs451.Customer;
import cs451.Host;
import cs451.Link.PerfectLink;
import cs451.Message;

import java.util.ArrayList;
import java.util.List;


public class BestEffortBroadcast implements Customer {
    private final List<Host> hosts;
    private PerfectLink perfectLink;
    private final Customer customer;

    public BestEffortBroadcast(PerfectLink perfectLink, List<Host> hosts,Customer customer) {
        this.customer = customer;
        this.perfectLink = perfectLink;
        this.hosts = new ArrayList<>(hosts);
        perfectLink.setObserver(this);

    }

    public void broadcast(Message model) {
        for(Host h : hosts){
            perfectLink.send(model,h.getPort(),h.getIp());
        }


    }


    @Override
    public void deliver(Message message) {
        customer.deliver(message);
    }



}
