package cs451;

import java.io.Serializable;
import java.util.Objects;

public class Signature implements Serializable {



    private int hostId;
    private int seq;


    public Signature(int hostId,int seq){
        this.hostId = hostId;
        this.seq = seq;
    }


    public int getSeq() {
        return seq;
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
                seq == signature.seq;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostId, seq);
    }

    @Override
    public String toString(){
        return "From host " + hostId + " with Seq " + seq;
    }
}
