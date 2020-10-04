package cs451;

public class Sender extends Thread {

    Receiver receiver;
    Message message;
    public Sender(Receiver receiver,Message message){
        this.receiver = receiver;
        this.message = message;
    }
     @Override
    public void run(){

     }
}
