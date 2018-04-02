import java.util.UUID;

public class Announcement implements Comparable<Announcement>{
    UUID announcementId;
    byte[] signature;

    public Announcement(byte[] signature) {
        this.announcementId = UUID.randomUUID();
        this.signature = signature;
    }
    @Override
    public int compareTo(Announcement o) {
        return announcementId.compareTo(o.announcementId);
    }
}
