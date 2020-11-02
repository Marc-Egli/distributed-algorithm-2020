package cs451.Broadcast;

import cs451.*;
import cs451.Link.PerfectLink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public class UniformReliableBroadcast implements Customer {

    private BestEffortBroadcast beb;
    private ConcurrentHashMap<Signature,List<Host>> forward;
    private ConcurrentHashMap<Signature,List<Host>> ack;
    private PerfectLink perfectLink;
    private final List<Host> hosts;
    private ConcurrentLinkedQueue<Signature> delivered;
    private final HashMap<Integer,Host> LOOKUP; //Need to modify this
    private int MIN_MSG;
    private final Customer customer;
    private final Host currentHost;



    public UniformReliableBroadcast(Host currentHost,PerfectLink perfectLink, List<Host> hosts,Customer customer){
        beb = new BestEffortBroadcast(perfectLink,hosts,this);
        this.forward = new ConcurrentHashMap<>();
        this.ack = new ConcurrentHashMap<>();
        this.hosts = hosts;
        this.perfectLink = perfectLink;
        this.delivered = new ConcurrentLinkedQueue<>();
        this.customer = customer;
        this.MIN_MSG = hosts.size();
        this.LOOKUP = buildLookupTable();
        this.currentHost = currentHost;

    }

    @Override
    public void deliver(Message message) {
        //TODO get host from ip and port instead of only port
        if(!delivered.contains(message.getSignature())) {
            Host sender = LOOKUP.get(message.getSrcPort());
            //Remember that we received this message
            if (!ack.getOrDefault(message.getSignature(), new ArrayList<>()).contains(sender)) {
                ack.putIfAbsent(message.getSignature(), new ArrayList<>());
                ack.get(message.getSignature()).add(sender);
            }

            //If not already forwarded, we forward it by broadcasting it
            if (!forward.getOrDefault(message.getSignature(), new ArrayList<>()).contains(sender)) {
                forward.putIfAbsent(message.getSignature(), new ArrayList<>());
                forward.get(message.getSignature()).add(sender);
                System.out.println("Adding to " + message.getSignature()+ " the host " + sender.getId());
                beb.broadcast(message);
                canDeliver();

            }
        }

    }

    public void broadcast(Message message) {
        forward.put(message.getSignature(),new ArrayList<>());
        forward.get(message.getSignature()).add(currentHost);
        System.out.println("Adding to " + message.getSignature()+ " the host " + currentHost.getId());
        System.out.println(forward.toString());
        beb.broadcast(message);
    }

    public void close(){
        perfectLink.close();
    }


    private HashMap<Integer,Host> buildLookupTable(){
        HashMap lookup = new HashMap<Integer,Host>();
        for(Host h : this.hosts){
            lookup.put(h.getPort(),h);
        }

        return lookup;
    }


    private synchronized void  canDeliver(){
        List<Signature> toRemove = new ArrayList<>();
        for(Map.Entry<Signature,List<Host>> pending : ack.entrySet()) {
            if(pending.getValue().size() >= MIN_MSG){
                delivered.add(pending.getKey());
                System.out.println("Delivered Message " + pending.getKey().getSeq() + " from " + pending.getKey().getHostId() + " because of numHots = " + pending.getValue().size());
                System.out.println(ack.toString());
                customer.deliver(new Message(String.valueOf(pending.getKey().getSeq()), MessageType.BROADCAST,pending.getKey()));
                toRemove.add(pending.getKey());
            }
        }
        for(Signature sign : toRemove){
            ack.remove(sign);
        }
        System.out.println(ack.toString());

    }




}
