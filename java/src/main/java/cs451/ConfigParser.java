package cs451;

import cs451.Broadcast.BroadcastType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.stream.Stream;

public class ConfigParser {

    private String path;
    private File file;
    public BroadcastType broadcastType;

    public boolean populate(String value) {
        this.file = new File(value);
        path = file.getPath();
        if (file == null) {
            System.out.println("File is null");
        }
        System.out.println(path);

        return true;
    }

    public String getPath() {
        return path;
    }


    public int getNumberOfMessage() {
        int numMessages = 0;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(getPath())));
            String l = reader.readLine();
            numMessages = Integer.parseInt(l);

            if(reader.readLine() == null){
                broadcastType = BroadcastType.Fifo;
                System.out.println("Is Fifo");
            }else{
                broadcastType = BroadcastType.Causal;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return numMessages;
    }


    public HashMap<Integer, HashSet<Integer>> getCausalDependencies(){
        HashMap<Integer, HashSet<Integer>> causalities = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(getPath())));
            String line = null;
            reader.readLine(); //skip first line
            int hostNum = 1;
            while((line = reader.readLine()) != null) {
                System.out.println("Thie line is " + line);
                String[] seperated = line.split(" ");
                HashSet<Integer> dependencies = new HashSet<>();
                //Add self to the dependencies because of FIFO property
                for(int i = 0; i < seperated.length;i++){
                    dependencies.add(Integer.parseInt(seperated[i]));
                }
                causalities.put(hostNum,dependencies);
                hostNum +=1;
            }

            System.out.println(causalities.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(causalities.toString());
        return causalities;

    }




}
