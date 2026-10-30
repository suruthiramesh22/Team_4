import java.util.Map;

public class RefundTransaction extends Transaction {
    public RefundTransaction(int id, String receiver, String originalSender, double amount) {
        super(id, receiver, originalSender, amount);
    }

    @Override
    public boolean execute(Map<String, Double> wallets) {
        double payerBalance = wallets.getOrDefault(getSender(), 0.0);
        if (getAmount() <= 0 || payerBalance < getAmount()) {
            setStatus("PENDING");
            return false;
        }
        wallets.put(getSender(), payerBalance - getAmount());
        wallets.put(getReceiver(), wallets.getOrDefault(getReceiver(), 0.0) + getAmount());
        setStatus("SUCCESS");
        return true;
    }
}
