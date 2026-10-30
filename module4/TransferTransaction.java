import java.util.Map;

public class TransferTransaction extends Transaction {
    public TransferTransaction(int id, String sender, String receiver, double amount) {
        super(id, sender, receiver, amount);
    }

    @Override
    public boolean execute(Map<String, Double> wallets) {
        double senderBalance = wallets.getOrDefault(getSender(), 0.0);
        if (getAmount() <= 0 || senderBalance < getAmount()
                || getSender().equals(getReceiver())) {
            setStatus("FAILED");
            return false;
        }

        // Validate first, then update both wallets.
        double receiverBalance = wallets.getOrDefault(getReceiver(), 0.0);
        wallets.put(getSender(), senderBalance - getAmount());
        wallets.put(getReceiver(), receiverBalance + getAmount());
        setStatus("SUCCESS");
        return true;
    }
}
