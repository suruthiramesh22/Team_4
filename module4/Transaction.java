import java.time.LocalDateTime;

// Common parent class for all transaction types.
public abstract class Transaction {
    private final int id;
    private final String sender;
    private final String receiver;
    private final double amount;
    private String status;
    private final LocalDateTime date;

    public Transaction(int id, String sender, String receiver, double amount) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.status = "PENDING";
        this.date = LocalDateTime.now();
    }

    public abstract boolean execute(java.util.Map<String, Double> wallets);

    public int getId() { return id; }
    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public LocalDateTime getDate() { return date; }
    public void setStatus(String status) { this.status = status; }
}
