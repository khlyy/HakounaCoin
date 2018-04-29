import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.UUID;

public class Network {
	private static ArrayList<User> users;
	private static TreeMap<UUID, Integer> usersMap;
	static final int N = 10;
	static final double propagatingThreshold = 0.8;
	static final double peeringThreshold = 0.3;
	private static final int numberOfUsers = 10;

	public static void propagate(User user, Announcement announcement) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException {
		for (UUID peerId: user.getPeers()) {
			if(Math.random() < propagatingThreshold){
				User nextUser = users.get(usersMap.get(peerId));
				System.out.println(user + " --> " + nextUser);
				nextUser.receiveAnnouncement(announcement);
			}
		}
	}

	private static void addUser(String username, int idx) throws NoSuchAlgorithmException { // create user in the network
		User newUser = new User(username);
		users.add(newUser);
		usersMap.put(newUser.getUserId(), idx);

	}

	private static void setPeers(int idx) {
		ArrayList<UUID> peers = new ArrayList<>();
		for (int i = 0; i < numberOfUsers; i++) 
			if (Math.random() < peeringThreshold && i != idx)
				peers.add(users.get(i).getUserId());
		
		users.get(idx).setPeers(peers);
	}

	private static void printPeers(int idx) {
		System.out.print(users.get(idx) + " --> ");
		for (UUID peerId: users.get(idx).getPeers()) {
			User nextUser = users.get(usersMap.get(peerId));
			System.out.print(nextUser + "   ");
		}
		System.out.println();
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException, ClassNotFoundException {

		users = new ArrayList<>();
		usersMap = new TreeMap<>();

		for (int i = 0; i < numberOfUsers; i++)
			addUser("User " + (i + 1), i);

		for (int i = 0; i < numberOfUsers; i++) {
			setPeers(i);
			printPeers(i);
		}

		System.out.println("------------------------------------------------------------------------------------------------");

		users.get(0).makeTransaction();
		System.out.println("------------------------------------------------------------------------------------------------");
		for (User user: users)
			System.out.println(user + ": " + user.getAnnouncements());

	}

	public static byte[] TransactionToByteArray(Transaction trans) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bos);
		out.writeObject(trans);
		out.flush();
		return bos.toByteArray();
	}

	public static Transaction byteArrayToTransaction(byte[] byteArray) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
		ObjectInputStream in = new ObjectInputStream(bis);
		return (Transaction) in.readObject();
	}
}
