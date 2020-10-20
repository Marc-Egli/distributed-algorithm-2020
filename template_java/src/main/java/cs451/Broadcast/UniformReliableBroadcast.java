package cs451.Broadcast;

import cs451.*;
import cs451.Link.PerfectLink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class UniformReliableBroadcast implements Customer {

    private BestEffortBroadcast beb;
    private ConcurrentHashMap<Signature,List<Host>> forward;
    private ConcurrentHashMap<Signature,List<Host>> ack;
    private PerfectLink perfectLink;
    private final List<Host> otherHosts;
    private List<Signature> delivered;
    private final Host currentHost;
    private final ConcurrentHashMap<Integer,Host> LOOKUP; //Need to modify this
    private int MIN_MSG;
    private final Customer customer;



    public UniformReliableBroadcast(Customer customer, PerfectLink perfectLink, Host currentHost, List<Host> otherHosts){
        beb = new BestEffortBroadcast(this,perfectLink,otherHosts);
        this.forward = new ConcurrentHashMap<>();
        this.ack = new ConcurrentHashMap<>();
        this.LOOKUP = buildLookupTable();
        this.perfectLink = perfectLink;
        this.otherHosts = otherHosts;
        this.currentHost = currentHost;
        this.delivered = new ArrayList<Signature>();
        this.customer = customer;
        this.MIN_MSG = (1 + otherHosts.size() )/ 2;

    }

    @Override
    public void deliver(Message message) {
        //TODO get host from ip and port
        if(!delivered.contains(message.getSignature())) {
            Host sender = LOOKUP.get(message.getSrcPort());
            //Remember that we received this message
            if (!ack.containsKey(message.getSignature())) {
                ack.put(message.getSignature(), new ArrayList<>());
            }
            ack.get(message.getSignature()).add(sender);

            //If not already forwarded, we forward it by broadcasting it
            if (!forward.getOrDefault(message.getSignature(), new ArrayList<>()).contains(sender)) {
                forward.putIfAbsent(message.getSignature(), new ArrayList<>());
                forward.get(message.getSignature()).add(sender);
                beb.broadcast(message);
                canDeliver();

            }
        }

    }

    public void broadcast(Message message) {
        forward.put(message.getSignature(),new ArrayList<>());
        forward.get(message.getSignature()).add(currentHost);
        //We deliver instantly if we broadcast
        ack.put(message.getSignature(),new ArrayList<>());
        ack.get(message.getSignature()).add(currentHost);
        beb.broadcast(message);
    }

    public void close(){
        perfectLink.close();
    }


    private ConcurrentHashMap<Integer,Host> buildLookupTable(){
        ConcurrentHashMap lookup = new ConcurrentHashMap<Integer,Host>();
        for(Host h : otherHosts){
            lookup.put(h.getPort(),h);
        }

        return lookup;
    }


    private void canDeliver(){
        List<Signature> toRemove = new ArrayList<>();
        for(Map.Entry<Signature,List<Host>> pending : ack.entrySet()) {
            if(pending.getValue().size() > MIN_MSG){
                delivered.add(pending.getKey());
                customer.deliver(new Message(String.valueOf(pending.getKey().getContent()), MessageType.BROADCAST,pending.getKey()));
                toRemove.add(pending.getKey());
            }
        }
        ack.remove(toRemove);
    }




}
