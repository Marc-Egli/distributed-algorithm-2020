package cs451;

import java.io.Serializable;
import java.util.UUID;

import static cs451.MessageType.ACK;

public class Message implements Serializable {

    private MessageType type;
    private String srcIp,dstIp;
    private int srcPort,dstPort;
    private final Signature signature;
    private String content;
    private UUID uid;


    public Message(String content, MessageType type, Signature signature){
        this.type = type;
        this.content = content;
        this.signature = signature;
    }

    private Message(String srcIp,int srcPort,String dstIp,int dstPort,String content, MessageType type,UUID uid, Signature signature){
        this.srcIp = srcIp;
        this.srcPort = srcPort;
        this.dstIp = dstIp;
        this.dstPort = dstPort;
        this.type = type;
        this.content = content;
        this.uid = uid;
        this.signature = signature;
    }

    public MessageType getType() {
        return type;
    }


    public String getSrcIp() {
        return srcIp;
    }


    public String getDstIp() {
        return dstIp;
    }



    public int getSrcPort() {
        return srcPort;
    }


    public int getDstPort() {
        return dstPort;
    }


    public Signature getSignature() {
        return signature;
    }

    public String getContent() {
        return content;
    }


    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }



    public Message generateAck(){
        if(this.type == ACK){
            throw new IllegalCallerException("Cannot generate an ACK message from an already ACK type");
        }
        Message ack = new Message(dstIp,dstPort,srcIp,srcPort,content,ACK,uid,signature);
        return ack;
    }


    public Message addIpLayer(int srcPort, String srcIp, int dstPort, String dstIp, UUID uid){
        return new Message(srcIp,srcPort, dstIp, dstPort, content,  type, uid,  signature);

    }


    @Override
    public String toString(){
        return "Message " + uid + " content " + getContent() + " from " + getSrcPort() + "\n";
    }




}
