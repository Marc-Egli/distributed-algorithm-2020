package cs451;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;



public class Receiver extends Thread{

    FairlossLink fairlossLink;
    private ArrayBlockingQueue<Message> receiveQueue = new ArrayBlockingQueue<>(20);
    private ArrayBlockingQueue<Message> ackQueue = new ArrayBlockingQueue<>(20);
    private List<UUID> receivedACKS = new ArrayList<>();
    private List<UUID> receivedBroadcast = new ArrayList<>();
    public Receiver(FairlossLink fairlosslink){
        this.fairlossLink = fairlosslink;
    }
    @Override
    public void run(){
        while(true){
            Message m = fairlossLink.receive();
            switch (m.getType()) {
                case BROADCAST :
                    try {
                        if (!receivedBroadcast.contains(m.getUid())) {
                            receiveQueue.put(m);
                            receivedBroadcast.add(m.getUid());
                            System.out.println("Received Broadcast " + m.getUid());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    break;

                case ACK:
                    try {
                        if(!receivedACKS.contains(m.getUid())){
                            receivedACKS.add(m.getUid());
                            ackQueue.put(m);
                            System.out.println("Received ACK " + m.getUid());
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

    }


    public synchronized Message getAckMessage(){
        return  ackQueue.poll();
    }


    public synchronized Message getReceivedMessage(){
        return receiveQueue.poll();
    }

}
