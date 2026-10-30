import java.time.LocalDateTime;

public class Reversal {
    private final int originalTransactionId;
    private String status;
    private final LocalDateTime createdAt;

    public Reversal(int originalTransactionId) {
        this.originalTransactionId = originalTransactionId;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    public int getOriginalTransactionId() { return originalTransactionId; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setStatus(String status) { this.status = status; }
}
