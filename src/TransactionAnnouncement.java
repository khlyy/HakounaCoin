
public class TransactionAnnouncement extends Announcement{

    Transaction transaction;
    public TransactionAnnouncement(Transaction transaction, byte[] signature) {
        super(signature);
        this.transaction = transaction;
    }
}
