import java.util.Map;

public class WithdrawTransaction extends Transaction {
    // sender is the wallet owner; receiver is the linked bank account label.
    public WithdrawTransaction(int id, String walletOwner, String bankAccount, double amount) {
        super(id, walletOwner, bankAccount, amount);
    }

    @Override
    public boolean execute(Map<String, Double> wallets) {
        double balance = wallets.getOrDefault(getSender(), 0.0);
        if (getAmount() <= 0 || balance < getAmount()) {
            setStatus("FAILED");
            return false;
        }
        wallets.put(getSender(), balance - getAmount());
        setStatus("SUCCESS");
        return true;
    }
}
