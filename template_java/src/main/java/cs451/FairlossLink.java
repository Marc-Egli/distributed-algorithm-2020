package cs451;

import java.io.IOException;
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

    public void send(String m, int port, String ip) throws IOException {
        byte buf[] = m.getBytes();
        DatagramPacket packet = new DatagramPacket(buf,buf.length,InetAddress.getByName(ip),port);
        socket.send(packet);

    }

    public String receive() throws IOException {
        byte[] receive = new byte[65535];
        DatagramPacket packet = new DatagramPacket(receive,receive.length);
        socket.receive(packet);
        return new String(receive);

    }
}
