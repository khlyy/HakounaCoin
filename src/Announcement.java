import java.util.UUID;

public class Announcement implements Comparable<Announcement>{
    UUID announcementId;

    public Announcement() {
        this.announcementId = UUID.randomUUID();
    }
    @Override
    public int compareTo(Announcement o) {
        return announcementId.compareTo(o.announcementId);
    }
}
