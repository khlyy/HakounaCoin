import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.util.*;

public class User {

    private UUID userId;
    private String username;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private ArrayList<UUID> peers;
    private List<Transaction> transactions;
    private ArrayList<Announcement> announcements;
    private TreeSet<Announcement> announcementSet;
    private List<Block> blockChain;
    private List<List<Block>> cache;
    private int transactionsCount;

    public User(String username) throws NoSuchAlgorithmException, IOException {
        this.userId = UUID.randomUUID();
        this.username = username;
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = kpg.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
        transactions = new ArrayList<>();
        announcements = new ArrayList<>();
        blockChain = new ArrayList<>();
        Block b0 = new Block("0", transactions, null);
        blockHash(b0);
        blockChain.add(b0);
        cache = new ArrayList<>();
        announcementSet = new TreeSet<>();
        transactionsCount = 0;
    }


    public static byte[] blockToByteArray(Block block) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(block);
        out.flush();
        return bos.toByteArray();
    }

    public String blockHash(Block block) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] byteHash = digest.digest(blockToByteArray(block));
        String stringHash = Base64.getEncoder().encodeToString(byteHash);
        block.setBlockHash(stringHash);
        return stringHash;
    }

    public Block blockMining() throws IOException, NoSuchAlgorithmException {
        for (int i = 0; i < 1e8; i++) {
            Block block = new Block(transactions, blockChain.get(blockChain.size() - 1).getBlockHash());
            String hash = blockHash(block);
            if (hash.substring(0, 2).equals("00")) {
                System.err.println("User " + username + " Solved the puzzle and created the block");
                System.err.println(hash);
                return block;
            }
        }
        return null;
    }

    public void removeIntersection(Block block) {
        List<Transaction> blockTransaction = block.getTransactions();
        List<Transaction> intersection = new ArrayList(blockTransaction);
        intersection.retainAll(transactions);
        for (Transaction transaction : intersection)
            transactions.remove(transaction);
    }

    public boolean insertInBlockChain(List<Block> blocks, Block block) {
        if (blocks.get(blocks.size() - 1).getBlockHash().equals(block.getPreviousHash())) {
            blocks.add(block);
            if (blocks == blockChain)
                removeIntersection(block);
            return true;
        }
        System.err.println("Failed to add b1_ to the block chain");
        for (int i = blocks.size() - 2; i > Math.max(-1, blocks.size() - 4); i--)
            if (blocks.get(i).getBlockHash().equals(block.getPreviousHash())) {
                List newChain = new ArrayList(blocks.subList(0, i + 1));
                newChain.add(block);
                cache.add(newChain);
                System.err.println("New chain added to the cache");
                return true;
            }

        return false;
    }

    public void receiveBlock(Block block) {
        boolean inserted = insertInBlockChain(blockChain, block);
        if (inserted) {
            System.err.println("new block was added to " + username + "'s block chain");
            return;
        }
        List deleteCachedChains = new ArrayList();
        for (List cachedChain : cache) {
            if (cachedChain.size() + 3 <= blockChain.size()) {
                deleteCachedChains.add(cachedChain);
                continue;
            }
            inserted = insertInBlockChain(cachedChain, block);
            if (inserted) {
                if (cachedChain.size() > blockChain.size()) {
                    List tmp = new ArrayList(cachedChain);
                    cachedChain = blockChain;
                    blockChain = tmp;
                }

                break;
            }

        }

        for(int i = 0; i < deleteCachedChains.size(); i++)
            cache.remove(deleteCachedChains.get(i));
    }

    public void makeBlock() throws IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException {
        Block block = blockMining();
        receiveBlock(block);
        Announcement blockAnnouncement = new BlockAnnouncement(block);
        announcementSet.add(blockAnnouncement);
        transactions.clear();
        transactionsCount = 0;
        sendAnnouncement(blockAnnouncement);
    }

    public void makeTransaction() throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Transaction newTransaction = new Transaction();
        transactions.add(newTransaction);
        transactionsCount++;
        Announcement newAnnouncement = signTransaction(newTransaction);
        announcementSet.add(newAnnouncement);
        sendAnnouncement(newAnnouncement);
        if (transactionsCount == Network.N) {
            System.err.println("Reached N Transactions");
            makeBlock();
        }
    }


    private void sendAnnouncement(Announcement announcement) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Network.propagate(this, announcement);
    }

    private Announcement signTransaction(Transaction transaction) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, this.privateKey);
        byte[] signature = cipher.doFinal(Network.TransactionToByteArray(transaction));
        return new TransactionAnnouncement(transaction, signature);
    }

    public void receiveAnnouncement(Announcement announcement) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        // TODO: 4/2/18 verify el announcement

        if (!announcementSet.add(announcement))
            return;

        if (announcement instanceof BlockAnnouncement) {
            System.err.println("User " + username + " Received block");
            receiveBlock(((BlockAnnouncement) announcement).getBlock());
        }
        else {
            transactionsCount++;
            if (transactionsCount == Network.N)
                makeBlock();
        }

        announcements.add(announcement);
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

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
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

    public List<Block> getBlockChain() {
        return blockChain;
    }
}
