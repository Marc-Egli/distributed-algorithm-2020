package cs451.Broadcast;

import cs451.Customer;
import cs451.Host;
import cs451.Link.PerfectLink;
import cs451.Message;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerArray;


public class FifoBroadcast implements Customer {

    private final Comparator<Message> orderComparator = (Message m1,Message m2) -> Integer.compare(m1.getSignature().getSeq(),m2.getSignature().getSeq());
    private final List<Host> otherHosts;
    private final Host currentHost;
    private final PerfectLink perfectLink;
    private final UniformReliableBroadcast uniformReliableBroadcast;
    private ConcurrentHashMap<Integer,List<Message>> pending = new ConcurrentHashMap<>();
    private AtomicIntegerArray order;

    public FifoBroadcast(Host currentHost, PerfectLink perfectLink,List<Host> otherHosts){
        this.currentHost= currentHost;
        this.perfectLink = perfectLink;
        this.otherHosts = otherHosts;
        this.uniformReliableBroadcast = new UniformReliableBroadcast(currentHost,perfectLink,otherHosts,this);
        this.order = new AtomicIntegerArray(1 + otherHosts.size());

    }


    public void broadcast(Message message){
        uniformReliableBroadcast.broadcast(message);
    }
    @Override
    public void deliver(Message message) {
        int hostId = message.getSignature().getHostId();
        int currentMessage = order.get(hostId-1);
        if(currentMessage == message.getSignature().getSeq() -1 ) {
            order.getAndIncrement(hostId-1);
            currentHost.deliver(message);
            List<Message> awaiting_messages = pending.get(hostId);
            if(!awaiting_messages.isEmpty()){
                awaiting_messages.remove(0);
                deliver(awaiting_messages.get(0));
            }
        }else{
            List<Message> tmp = pending.getOrDefault(hostId,new ArrayList<>());
            tmp.add(message);
            tmp.sort(orderComparator);
            pending.put(hostId,tmp);
        }

    }

    public void close(){
        uniformReliableBroadcast.close();
    }

}
