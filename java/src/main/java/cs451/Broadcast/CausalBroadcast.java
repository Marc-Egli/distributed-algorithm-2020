package cs451.Broadcast;

import cs451.Host;
import cs451.Link.PerfectLink;
import cs451.Messages.Message;
import cs451.Observer;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class CausalBroadcast implements Observer,Broadcast{

    private final Observer observer;
    private final UniformReliableBroadcast uniformReliableBroadcast;
    private final HashMap<Integer, HashSet<Integer>> causalities;
    //private int vectorClock[];
    private final int HOST_ID;
    private final List<Host> HOSTS;
    private final HashMap<Integer, Host> LOOKUP_TABLE;
    private final AtomicIntegerArray vectorClock;
    private ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Message>> pending = new ConcurrentHashMap<>();

    public CausalBroadcast(PerfectLink perfectLink, List<Host> hosts,int hostId, HashMap<Integer, HashSet<Integer>> causalities, Observer observer) {
        this.causalities = causalities;
        this.observer = observer;
        this.HOST_ID = hostId;
        this.HOSTS = hosts;
        this.LOOKUP_TABLE = buildLookupTable();
        this.uniformReliableBroadcast = new UniformReliableBroadcast(perfectLink,hosts,this);
        //this.vectorClock = new int[hosts.size()];
        this.vectorClock = new AtomicIntegerArray(hosts.size());

    }

    @Override
    public void broadcast(Message message) {
        message.getSignature().setVectorClock(getCausalVectorClock());
        uniformReliableBroadcast.broadcast(message);
        observer.deliver(message);
        this.vectorClock.getAndIncrement(HOST_ID-1);


    }

    public int[] getCausalVectorClock(){
        int causalVectorClock[] = new int[this.HOSTS.size()];
        HashSet<Integer> hostCausalities = causalities.get(HOST_ID);
        for (Integer i : hostCausalities){
            causalVectorClock[i-1] = vectorClock.get(i-1);
        }
        return causalVectorClock;
    }

    @Override
    public void deliver(Message message) {

        Host sender = LOOKUP_TABLE.get(message.getSignature().getHostId());
        if(sender.getId() != HOST_ID){

            if(canCausalyDeliver(message)) {
                observer.deliver(message);
                this.vectorClock.getAndIncrement(message.getSignature().getHostId()-1);
                deliverPending();

            } else {
                ConcurrentLinkedQueue<Message> awaiting = pending.getOrDefault(sender.getId(),new ConcurrentLinkedQueue<>());
                awaiting.add(message);
                pending.put(sender.getId(),awaiting);
            }



            System.out.println(pending.toString());
            System.out.println("");
            for(int i = 0; i < vectorClock.length(); i++){
                System.out.print(vectorClock.get(i) + " ");
            }
            System.out.println("");
            deliverPending();

        }

    }


    private boolean canCausalyDeliver(Message message){
        int sender = message.getSignature().getHostId();
        int[] mVectorClock = message.getSignature().getVectorClock();
        boolean canDeliver = true;
        for(Host host : this.HOSTS) {
            if(!(this.vectorClock.get(host.getId() -1) >= mVectorClock[host.getId()-1])){
                canDeliver = false;
                break;
            }
        }
        return canDeliver;
    }

    private HashMap<Integer, Host> buildLookupTable() {
        HashMap lookup = new HashMap<Map.Entry<String, Integer>, Host>();
        for (Host h : this.HOSTS) {
            lookup.put(h.getId(), h);
        }
        return lookup;
    }

    public void deliverPending(){
        Message toRemove = null;
        for (Map.Entry<Integer, ConcurrentLinkedQueue<Message>> entry : pending.entrySet()) {
            for (Message message : entry.getValue()) {
                if (canCausalyDeliver(message)) {
                    toRemove = message;
                    observer.deliver(message);
                    vectorClock.getAndIncrement(entry.getKey() - 1);
                    break;
                }

            }

            if(toRemove != null){
                break;
            }
        }


        if(toRemove != null){
            pending.get(toRemove.getSignature().getHostId()).remove(toRemove);
            deliverPending();
        }

    }

}
