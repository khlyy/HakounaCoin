
public class TransactionAnnouncement extends Announcement{
    byte[] signature;
    Transaction transaction;

    public TransactionAnnouncement(Transaction transaction, byte[] signature) {
        this.signature = signature;
        this.transaction = transaction;
    }
}
