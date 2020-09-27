package cs451;

import java.io.IOException;


public class StubbornLink {
    private FairlossLink fairlosslink;

    public StubbornLink(int port, String ip){
        this.fairlosslink = new FairlossLink(port,ip);

    }

    public void send(String m, int port, String ip) throws IOException {
       while(true) {
           fairlosslink.send(m, port, ip);
       }
    }

    public String receive() throws IOException {
        return fairlosslink.receive();
    }
}
