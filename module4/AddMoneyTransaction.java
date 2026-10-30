import java.util.Map;

public class AddMoneyTransaction extends Transaction {
    // sender is the linked bank account label; receiver is the wallet owner.
    public AddMoneyTransaction(int id, String bankAccount, String walletOwner, double amount) {
        super(id, bankAccount, walletOwner, amount);
    }

    @Override
    public boolean execute(Map<String, Double> wallets) {
        if (getAmount() < 10 || getAmount() > 50000) {
            setStatus("FAILED");
            return false;
        }
        wallets.put(getReceiver(), wallets.getOrDefault(getReceiver(), 0.0) + getAmount());
        setStatus("SUCCESS");
        return true;
    }
}
