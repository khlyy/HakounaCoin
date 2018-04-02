import java.util.UUID;

public class Transaction {
	private UUID transactionId;
	private int amount;

    public Transaction() {
        this.transactionId = UUID.randomUUID();
    }
}
