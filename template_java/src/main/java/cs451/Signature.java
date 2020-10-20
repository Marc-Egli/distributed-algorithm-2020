package cs451;

import java.util.Objects;

public class Signature {



    private int hostId;
    private int content;


    public Signature(int hostId,int content){
        this.hostId = hostId;
        this.content = content;
    }


    public int getContent() {
        return content;
    }
    public int getHostId() {
        return hostId;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Signature signature = (Signature) o;
        return hostId == signature.hostId &&
                content == signature.content;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostId, content);
    }
}
