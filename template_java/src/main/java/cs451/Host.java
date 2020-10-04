package cs451;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Host {

    private static final String IP_START_REGEX = "/";
    private int id;
    private String ip;
    private int port = -1;
    private List<Host> hosts;
    private int messages = -1;
    private PerfectLink link;
    public List<String> received = new ArrayList<>();


    public boolean init(List<Host> hosts, int messages) {
        this.hosts = new ArrayList<>(hosts);
        this.hosts.remove(this);
        this.messages = messages;
        this.link = new PerfectLink(port, ip);
        return true;
    }

    public boolean populate(String idString, String ipString, String portString) {
        try {
            id = Integer.parseInt(idString);

            String ipTest = InetAddress.getByName(ipString).toString();
            if (ipTest.startsWith(IP_START_REGEX)) {
                ip = ipTest.substring(1);
            } else {
                ip = InetAddress.getByName(ipTest.split(IP_START_REGEX)[0]).getHostAddress();
            }

            port = Integer.parseInt(portString);
            if (port <= 0) {
                System.err.println("Port in the hosts file must be a positive number!");
                return false;
            }


        } catch (NumberFormatException e) {
            if (port == -1) {
                System.err.println("Id in the hosts file must be a number!");
            } else {
                System.err.println("Port in the hosts file must be a number!");
            }
            return false;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return true;
    }


    public void start()  {
        for (Host h : hosts) {
            link.send(String.valueOf(messages), h.getPort(), h.getIp());
        }
    }

    public void receive() {
        while (true) {
            Message m = link.receive();
            if (m != null) {
                received.add(this.id + " received " + m);
                System.out.println(m);
            }

        }

    }

    public int getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

}
