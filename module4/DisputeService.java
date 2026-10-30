import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DisputeService {
    private final List<Dispute> disputes = new ArrayList<>();

    public boolean raiseDispute(int transactionId, String reason, TransactionService service) {
        Transaction transaction = service.findById(transactionId);
        if (!(transaction instanceof TransferTransaction)
                || !transaction.getStatus().equals("SUCCESS")) {
            System.out.println("Only successful transfers can be disputed.");
            return false;
        }

        long seconds = Duration.between(transaction.getDate(), LocalDateTime.now()).getSeconds();
        if (seconds < 0 || seconds > 24 * 60 * 60) {
            System.out.println("The 24-hour dispute period has expired.");
            return false;
        }

        for (Dispute dispute : disputes) {
            if (dispute.getTransactionId() == transactionId) {
                System.out.println("A dispute already exists for this transfer.");
                return false;
            }
        }

        disputes.add(new Dispute(transactionId, reason));
        System.out.println("Dispute submitted.");
        return true;
    }

    public Dispute findPendingDispute(int transactionId) {
        for (Dispute dispute : disputes) {
            if (dispute.getTransactionId() == transactionId
                    && dispute.getStatus().equals("PENDING")) return dispute;
        }
        return null;
    }

    public List<Dispute> getDisputes() {
        return new ArrayList<>(disputes);
    }
}
