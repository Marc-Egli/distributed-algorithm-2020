package cs451;

import java.io.Serializable;
import java.util.UUID;

import static cs451.MessageType.ACK;

public class Message implements Serializable {

    private MessageType type;
    private String srcIP,dstIp;
    private int srcPort,dstPort;
    private final Signature signature;
    private String content;
    private UUID uid;


    public Message(String content, MessageType type, Signature signature){
        this.type = type;
        this.content = content;
        this.signature = signature;
    }

    private Message(String srcIP,int srcPort,String dstIp,int dstPort,String content, MessageType type,UUID uid, Signature signature){
        this.srcIP = srcIP;
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

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getSrcIP() {
        return srcIP;
    }

    public void setSrcIP(String srcIP) {
        this.srcIP = srcIP;
    }

    public String getDstIp() {
        return dstIp;
    }

    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
    }

    public int getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }

    public int getDstPort() {
        return dstPort;
    }

    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }

    public Signature getSignature() {
        return signature;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
        Message ack = new Message(dstIp,dstPort,srcIP,srcPort,content,ACK,uid,signature);
        return ack;
    }


    @Override
    public String toString(){
        return "Message " + uid + " content " + getContent() + " from " + getSrcPort() + "\n";
    }




}
