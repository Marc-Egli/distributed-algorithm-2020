package cs451;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PerfectLink {

    private StubbornLink stubbornLink;
    private List<String> delivered;


    public PerfectLink(int port, String ip){
        this.stubbornLink = new StubbornLink(port,ip);
        this.delivered = new ArrayList<>();
    }

    public void send(String m, int port, String ip) throws IOException {
            stubbornLink.send(m,port,ip);
    }

    public String receive() throws IOException {
        String m = stubbornLink.receive();
        if (!delivered.contains(m)){
            delivered.add(m);
            return m;
        }
        return null;
    }
}
