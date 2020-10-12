package cs451;

import java.io.Serializable;
import java.util.UUID;

import static cs451.MessageType.ACK;

public class Message implements Serializable {



    private MessageType type;
    private String srcIP,dstIp;
    private int srcPort,dstPort;



    private String content;
    private UUID uid;

    public int getSrcPort() {
        return srcPort;
    }

    public Message(String srcIP, int srcPort, String dstIp, int dstPort, String content, MessageType type, UUID uid){
        this.type = type;
        this.content = content;
        this.srcIP = srcIP;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
        this.dstIp = dstIp;
        this.uid = uid;
    }


    public String getSrcIP() {
        return srcIP;
    }

    public String getDstIp() {
        return dstIp;
    }

    public int getDstPort() {
        return dstPort;
    }

    public MessageType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public UUID getUid() {
        return uid;
    }

    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
    }

    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }

    public void setType(MessageType type){
        this.type = type;
    }


    public Message generateAck(){
        if(this.type == ACK){
            throw new IllegalCallerException("Cannot generate an ACK message from an already ACK type");
        }
        Message ack = new Message(dstIp,dstPort,srcIP,srcPort,"",ACK,uid);
        return ack;
    }


    @Override
    public String toString(){
        return "Message " + uid + " content " + getContent() + " from " + getSrcPort() + "\n";
    }

}
