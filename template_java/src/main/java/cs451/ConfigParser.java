package cs451;

import java.io.*;

public class ConfigParser {

    private String path;
    private File file;

    public boolean populate(String value) {
        this.file = new File(value);
        path = file.getPath();
        if(file == null) {
            System.out.println("File is null");
        }
        System.out.println(path);

        return true;
    }

    public String getPath() {
        return path;
    }


    public int getFIFOConfig()  {
        int numMessages = 0;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File("../"+getPath())));
            String l = reader.readLine();
            numMessages = Integer.parseInt(l);
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return numMessages;

    }

}
