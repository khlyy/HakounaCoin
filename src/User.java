import java.util.ArrayList;
import java.util.TreeSet;

public class User {
	private String userId;
	private String userName;
	private long privateKey;
	private long publicKey;
	private ArrayList<User> peers;
	private ArrayList<Announcement> announcements;
	private TreeSet<Announcement> announcementSet;

	private void makeTransaction(String transactionId) {

	}

	private void sendAnnouncement(Announcement announcement) { 

	}

	private void signTransaction(Transaction transaction) { // sign transaction and wrap it in the announcement decide sending.
	}

	public void recieveAnnouncement(Announcement announcement) {

	}

}
