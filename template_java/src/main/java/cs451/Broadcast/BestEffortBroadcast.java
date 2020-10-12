package cs451.Broadcast;


import cs451.Customer;
import cs451.Host;
import cs451.Link.PerfectLink;
import cs451.Message;

import java.util.ArrayList;
import java.util.List;


public class BestEffortBroadcast implements Customer {
    private List<Host> hosts;
    private PerfectLink perfectLink;
    private Customer customer;

    public BestEffortBroadcast(Customer customer,PerfectLink perfectLink, List<Host> hosts) {
        this.customer = customer;
        this.perfectLink = perfectLink;
        this.hosts = new ArrayList<>(hosts);
        perfectLink.setObserver(this);

    }




    public void broadcast(String m) {
        for(Host h : hosts){
            perfectLink.send(m,h.getPort(),h.getIp());
        }

    }


    @Override
    public void deliver(Message message) {
        customer.deliver(message);
    }

    public void close(){
        perfectLink.close();
    }

}
