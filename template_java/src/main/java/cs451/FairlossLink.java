package cs451;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;



public class FairlossLink {

    private DatagramSocket socket;

    public FairlossLink(int port, String ip) {
        try {
            this.socket = new DatagramSocket(port, InetAddress.getByName(ip));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void send(Message m)  {
        byte buf[] = serialize(m);
        try {
            DatagramPacket packet = new DatagramPacket(buf,buf.length,InetAddress.getByName(m.getDstIp()),m.getDstPort());
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void close(){
        this.socket.close();
    }

    public Message receive() {
        byte[] receive = new byte[65535];
        DatagramPacket packet = new DatagramPacket(receive,receive.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return deserialize(packet.getData());
    }

    public byte[] serialize(Message m) {
        byte[] byteMessage = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(m);
            out.flush();
            byteMessage = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
            }
        }
        return byteMessage;
    }

    public Message deserialize(byte[] byteMessage){
        Message message = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(byteMessage);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            message = (Message) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }
        }
        return message;
    }




}
