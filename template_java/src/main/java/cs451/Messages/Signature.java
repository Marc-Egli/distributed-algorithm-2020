package cs451.Messages;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representation of the initial emitter of a Message
 */
public class Signature implements Serializable, Comparable<Signature> {

    private final int hostId;
    private final int seq;
    private int[] vectorClock;

    /**
     * Constructs a new immuable Signature
     *
     * @param hostId id of host
     * @param seq    sequence number of the message
     */
    public Signature(int hostId, int seq) {
        this.hostId = hostId;
        this.seq = seq;
    }

    //Getters
    public int getSeq() {
        return seq;
    }

    public int getHostId() {
        return hostId;
    }

    public void setVectorClock(int[] vectorClock){
        this.vectorClock = vectorClock;
    }

    public int[] getVectorClock(){
        return this.vectorClock;
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
    public String toString() {
        String vect = "";
        if(vectorClock != null){
            for(int i = 0; i < vectorClock.length; i++){
                vect += vectorClock[i] + ",";
            }
        }
        return "(H " + hostId + ", Seq " + seq + ", Vector : " + vect+")";
    }

    @Override
    public int compareTo(Signature other) {
        if (this.seq != other.seq) {
            return Integer.compare(this.seq, other.seq);
        } else {
            return Integer.compare(this.hostId, other.hostId);

        }
    }
}
