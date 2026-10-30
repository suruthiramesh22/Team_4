import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReversalService {
    private final List<Reversal> reversals = new ArrayList<>();
    private final TransactionService transactionService;
    private final DisputeService disputeService;
    private int nextRefundId = 1000;

    public ReversalService(TransactionService transactionService, DisputeService disputeService) {
        this.transactionService = transactionService;
        this.disputeService = disputeService;
    }

    // The caller must verify that the current user is an Admin before calling this method.
    public boolean approveDispute(int transactionId, boolean admin,
                                  Map<String, Double> wallets) {
        if (!admin) {
            System.out.println("Only an Admin can approve a reversal.");
            return false;
        }

        Dispute dispute = disputeService.findPendingDispute(transactionId);
        Transaction original = transactionService.findById(transactionId);
        if (dispute == null || !(original instanceof TransferTransaction)
                || !original.getStatus().equals("SUCCESS")) {
            System.out.println("No eligible pending dispute found.");
            return false;
        }

        Reversal reversal = new Reversal(transactionId);
        reversals.add(reversal);

        RefundTransaction refund = new RefundTransaction(
                nextRefundId++, original.getReceiver(), original.getSender(), original.getAmount());

        if (!refund.execute(wallets)) {
            reversal.setStatus("PENDING");
            dispute.setStatus("REVERSAL_PENDING");
            System.out.println("Receiver has insufficient funds. Reversal is pending.");
            return false;
        }

        original.setStatus("REVERSED");
        dispute.setStatus("APPROVED");
        reversal.setStatus("SUCCESS");
        // Keep the refund in the shared transaction history.
        transactionService.addTransactionRecord(refund);
        System.out.println("Reversal completed successfully.");
        return true;
    }

    public void retryPendingReversals(Map<String, Double> wallets) {
        for (Reversal reversal : reversals) {
            if (reversal.getStatus().equals("PENDING")) {
                approveDispute(reversal.getOriginalTransactionId(), true, wallets);
            }
        }
    }

    public List<Reversal> getReversals() {
        return new ArrayList<>(reversals);
    }
}
