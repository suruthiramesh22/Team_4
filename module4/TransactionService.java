import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TransactionService {
    private final List<Transaction> transactions = new ArrayList<>();

    public boolean addTransaction(Transaction transaction, Map<String, Double> wallets) {
        if (transaction instanceof TransferTransaction && isDuplicate((TransferTransaction) transaction)) {
            transaction.setStatus("FAILED");
            System.out.println("Duplicate transfer rejected.");
            transactions.add(transaction);
            return false;
        }

        boolean success = transaction.execute(wallets);
        transactions.add(transaction);
        return success;
    }

    private boolean isDuplicate(TransferTransaction newTransfer) {
        LocalDateTime now = LocalDateTime.now();
        for (Transaction old : transactions) {
            if (old instanceof TransferTransaction
                    && old.getSender().equals(newTransfer.getSender())
                    && old.getReceiver().equals(newTransfer.getReceiver())
                    && Double.compare(old.getAmount(), newTransfer.getAmount()) == 0
                    && old.getStatus().equals("SUCCESS")) {
                long seconds = Duration.between(old.getDate(), now).getSeconds();
                if (seconds >= 0 && seconds < 60) return true;
            }
        }
        return false;
    }

    // Used when a refund has already been executed by ReversalService.
    public void addTransactionRecord(Transaction transaction) {
        transactions.add(transaction);
    }

    public Transaction findById(int id) {
        for (Transaction transaction : transactions) {
            if (transaction.getId() == id) return transaction;
        }
        return null;
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public void showHistory() {
        transactions.stream()
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .forEach(t -> System.out.println(
                        t.getId() + " | " + t.getClass().getSimpleName()
                        + " | " + t.getSender() + " -> " + t.getReceiver()
                        + " | Rs." + t.getAmount() + " | " + t.getStatus()
                        + " | " + t.getDate()));
    }

    public void filterByType(String type) {
        for (Transaction t : transactions) {
            if (t.getClass().getSimpleName().equalsIgnoreCase(type)
                    || (type.equalsIgnoreCase("TRANSFER") && t instanceof TransferTransaction)
                    || (type.equalsIgnoreCase("ADD MONEY") && t instanceof AddMoneyTransaction)
                    || (type.equalsIgnoreCase("WITHDRAW") && t instanceof WithdrawTransaction)
                    || (type.equalsIgnoreCase("CASHBACK") && t instanceof CashbackTransaction)
                    || (type.equalsIgnoreCase("REFUND") && t instanceof RefundTransaction)) {
                System.out.println(t.getId() + " | " + type + " | Rs." + t.getAmount()
                        + " | " + t.getStatus());
            }
        }
    }

    public void filterByStatus(String status) {
        for (Transaction t : transactions) {
            if (t.getStatus().equalsIgnoreCase(status))
                System.out.println(t.getId() + " | " + t.getStatus() + " | Rs." + t.getAmount());
        }
    }

    public void filterByDate(LocalDateTime from, LocalDateTime to) {
        for (Transaction t : transactions) {
            if (!t.getDate().isBefore(from) && !t.getDate().isAfter(to))
                System.out.println(t.getId() + " | " + t.getDate() + " | " + t.getStatus());
        }
    }
}
