import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.UUID;

public class User {

	private UUID userId;
	private String username;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	private ArrayList<UUID> peers;
	private ArrayList<Transaction> transactions;
	private ArrayList<Announcement> announcements;
	private TreeSet<Announcement> announcementSet;
	private int transactionsCount;

	public User(String username) throws NoSuchAlgorithmException {
		this.userId = UUID.randomUUID();
		this.username = username;
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		KeyPair keyPair = kpg.generateKeyPair();
		this.privateKey = keyPair.getPrivate();
		this.publicKey = keyPair.getPublic();
		transactions = new ArrayList<>();
		announcements = new ArrayList<>();
		announcementSet = new TreeSet<>();
		transactionsCount = 0;
	}

	public void makeTransaction() throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
		UUID transactionId = UUID.randomUUID();
		Transaction newTransaction = new Transaction();
		transactions.add(newTransaction);
		transactionsCount++;
		if (transactionsCount == Network.N) {
			// TODO: 4/2/18 solve puzzle to create block
		}
		Announcement newAnnouncement = signTransaction(newTransaction);
		sendAnnouncement(newAnnouncement);
	}

	private void sendAnnouncement(Announcement announcement) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
		Network.propagate(this, announcement);
	}

	private Announcement signTransaction(Transaction transaction) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, this.privateKey);
		byte[] signature = cipher.doFinal(Network.TransactionToByteArray(transaction));
		return new TransactionAnnouncement(transaction, signature);
	}

	public void receiveAnnouncement(Announcement announcement) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
		// TODO: 4/2/18 verify el announcement

		if (!announcementSet.add(announcement))
			return;
		announcements.add(announcement);
		transactionsCount++;
		if (transactionsCount == Network.N) {
			// TODO: 4/2/18 solve puzzle to create block
		}
		sendAnnouncement(announcement);
	}

	public String toString() {
		return username;

	}

	public void setPeers(ArrayList<UUID> peers) {
		this.peers = peers;

	}

	public UUID getUserId() {
		return userId;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public ArrayList<UUID> getPeers() {
		return peers;
	}

	public ArrayList<Announcement> getAnnouncements() {
		return announcements;
	}
}
