package cs451;


import java.util.concurrent.LinkedBlockingQueue;

public class Sender extends Thread {

    private LinkedBlockingQueue<Message> toSend = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Message> receivedAcks = new LinkedBlockingQueue<>();
    private FairlossLink fairlossLink;

    public Sender(FairlossLink fairlossLink){
        this.fairlossLink = fairlossLink;
    }
     @Override
    public void run(){

        while(true) {
            while(toSend.peek() == null){}

            Message message = toSend.poll();
            if(message == null){throw new IllegalStateException("Wtf");}

            switch(message.getType()) {

                case BROADCAST:
                    while(true) {
                        fairlossLink.send(message);
                        if(receivedAcks.poll() != null &&
                            receivedAcks.peek().getUid().equals(message.getUid())){
                            break;
                        }
                    }
                    break;

                case ACK:
                    fairlossLink.send(message);
                    break;
            }

        }

     }


    public void send(Message message)  {
        try {
            toSend.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void notifyAck(Message message)  {
        try {
            receivedAcks.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
