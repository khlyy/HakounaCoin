import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Block {

    private String previousHash;
    private String nonce;
    private String blockHash;

    public List<Transaction> getTransactions() {
        return transactions;
    }

    List<Transaction> transactions;

    public Block(List<Transaction> transactions, String previousHash) {
        this.nonce = UUID.randomUUID().toString();
        this.transactions = transactions;
        this.previousHash = previousHash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }
}
