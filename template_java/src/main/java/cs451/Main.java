package cs451;

import cs451.Broadcast.CausalBroadcast;
import cs451.Broadcast.FifoBroadcast;
import cs451.Link.PerfectLink;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {

    public static ConcurrentLinkedQueue<String> outputBuffer = new ConcurrentLinkedQueue<>();

    private static void handleSignal() {
        //immediately stop network packet processing
        System.out.println("Immediately stopping network packet processing.");
        //write/flush output file if necessary
        System.out.println("Writing output.");
        try {
            File outputFile = new File(outputBuffer.poll());
            System.out.println(outputFile.toString());
            FileOutputStream fos = new FileOutputStream(outputFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            while (outputBuffer.peek() != null) {
                osw.write(outputBuffer.poll());
                osw.write("\n");
            }
            osw.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void initSignalHandlers() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                handleSignal();
            }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        Parser parser = new Parser(args);
        parser.parse();

        initSignalHandlers();

        // example
        long pid = ProcessHandle.current().pid();
        System.out.println("My PID is " + pid + ".");
        System.out.println("Use 'kill -SIGINT " + pid + " ' or 'kill -SIGTERM " + pid + " ' to stop processing packets.");

        System.out.println("My id is " + parser.myId() + ".");
        System.out.println("List of hosts is:");
        for (Host host : parser.hosts()) {
            System.out.println(host.getId() + ", " + host.getIp() + ", " + host.getPort());
        }

        System.out.println("Barrier: " + parser.barrierIp() + ":" + parser.barrierPort());
        System.out.println("Signal: " + parser.signalIp() + ":" + parser.signalPort());
        System.out.println("Output: " + parser.output());
        // if config is defined; always check before parser.config()
        if (parser.hasConfig()) {
            System.out.println("Config: " + parser.config());
        }
        outputBuffer.add(parser.output());

        Host host = parser.getActiveHost();
        Coordinator coordinator = new Coordinator(parser.myId(), parser.barrierIp(), parser.barrierPort(), parser.signalIp(), parser.signalPort());

        System.out.println("Waiting for all processes for finish initialization");

        int numberOfMessages = parser.numMessages();
        PerfectLink perfectLink = new PerfectLink(host.getPort(), host.getIp());
        switch (parser.getBroadcastType())  {

            case Fifo:
                FifoBroadcast fifoBroadcast = new FifoBroadcast(perfectLink,parser.hosts(),host);
                host.init(numberOfMessages,fifoBroadcast,outputBuffer);
                break;
            case Causal:
                CausalBroadcast causalBroadcast = new CausalBroadcast(perfectLink,parser.hosts(),host.getId(),parser.getCausalDependencies(),host);
                host.init(numberOfMessages,causalBroadcast,outputBuffer);
        }



        coordinator.waitOnBarrier();

        System.out.println("Broadcasting messages...");

        host.start();


        coordinator.finishedBroadcasting();
        System.err.println("Signaling end of broadcasting messages");


        while (true) {
            // Sleep for 1 hour
            System.err.println("Now sleeping");
            Thread.sleep(60 * 60 * 1000);
        }
    }
}
