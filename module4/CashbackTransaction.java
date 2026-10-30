import java.util.Map;

public class CashbackTransaction extends Transaction {
    public CashbackTransaction(int id, String walletOwner, double amount) {
        super(id, "SYSTEM", walletOwner, amount);
    }

    @Override
    public boolean execute(Map<String, Double> wallets) {
        if (getAmount() <= 0) {
            setStatus("FAILED");
            return false;
        }
        wallets.put(getReceiver(), wallets.getOrDefault(getReceiver(), 0.0) + getAmount());
        setStatus("SUCCESS");
        return true;
    }
}
