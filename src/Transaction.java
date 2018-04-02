import java.io.Serializable;
import java.util.UUID;

public class Transaction implements Serializable {
	public UUID transactionId;
	private int amount;

    public Transaction() {
        this.transactionId = UUID.randomUUID();
    }
}
