package cs451.Messages;

import java.io.Serializable;
import java.util.UUID;

import static cs451.Messages.MessageType.ACK;

public class Message implements Serializable {

    private final MessageType type;
    private String srcIp, dstIp;
    private int srcPort, dstPort;
    private final Signature signature;
    private final String content;
    private UUID uid;


    /**
     * Constructs a partial message with not Network information
     * @param content the content of the Message
     * @param type type of the Message
     * @param signature signature of the Message
     */
    public Message(String content, MessageType type, Signature signature) {
        this.type = type;
        this.content = content;
        this.signature = signature;
    }

    /**
     * Constructs a full message
     * @param srcIp
     * @param srcPort
     * @param dstIp
     * @param dstPort
     * @param content
     * @param type
     * @param uid
     * @param signature
     */
    private Message(String srcIp, int srcPort, String dstIp, int dstPort, String content, MessageType type, UUID uid, Signature signature) {
        this.srcIp = srcIp;
        this.srcPort = srcPort;
        this.dstIp = dstIp;
        this.dstPort = dstPort;
        this.type = type;
        this.content = content;
        this.uid = uid;
        this.signature = signature;
    }


    //Getter and Setters

    public MessageType getType() {
        return type;
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


    public UUID getUid() {
        return uid;
    }


    /**
     * Generates an ACK responds from a Broadcast Message
     * @return
     */
    public Message generateAck() {
        if (this.type == ACK) {
            throw new IllegalCallerException("Cannot generate an ACK message from an already ACK type");
        }
        return new Message(dstIp, dstPort, srcIp, srcPort, content, ACK, uid, signature);
    }


    /**
     * Creates a new message from the current message by adding all destination and source information
     * @param srcPort source port of the sender
     * @param srcIp source ip of the sender
     * @param dstPort destination port
     * @param dstIp destination ip
     * @param uid unique identifier
     * @return Message containing all necessary information to be send over thew fairloss link
     */
    public Message addIpLayer(int srcPort, String srcIp, int dstPort, String dstIp, UUID uid) {
        return new Message(srcIp, srcPort, dstIp, dstPort, content, type, uid, signature);

    }

    /**
     * String representation of a Message
     * @return
     */
    @Override
    public String toString() {
        return type + " " + signature.toString();
    }


}
