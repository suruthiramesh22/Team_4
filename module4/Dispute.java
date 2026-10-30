import java.time.LocalDateTime;

public class Dispute {
    private final int transactionId;
    private final String reason;
    private final LocalDateTime createdAt;
    private String status;

    public Dispute(int transactionId, String reason) {
        this.transactionId = transactionId;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    public int getTransactionId() { return transactionId; }
    public String getReason() { return reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
