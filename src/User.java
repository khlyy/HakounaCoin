import java.security.*;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.UUID;

public class User {
	private UUID userId;
	private String username;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	private ArrayList<User> peers;
	private ArrayList<Announcement> announcements;
	private TreeSet<Announcement> announcementSet;

	public User(String username, ArrayList<User> peers) throws NoSuchAlgorithmException {
		this.userId = UUID.randomUUID();
		this.username = username;
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
		KeyPair keyPair = kpg.generateKeyPair();
		this.privateKey = keyPair.getPrivate();
		this.publicKey = keyPair.getPublic();
		this.peers = peers;
		announcements = new ArrayList<>();
		announcementSet = new TreeSet<>();
	}

	private void makeTransaction(String transactionId) {

	}

	private void sendAnnouncement(Announcement announcement) { 

	}

	private void signTransaction(Transaction transaction) { // sign transaction and wrap it in the announcement decide sending.
	}

	public void recieveAnnouncement(Announcement announcement) {

	}

}
