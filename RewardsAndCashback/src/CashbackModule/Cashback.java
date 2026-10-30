package CashbackModule;

/**
 * Calculates cashback for eligible successful transfers.
 * Rules from Module 5:
 * - 1% cashback for transfers of Rs. 500 or more
 * - Maximum Rs. 20 cashback per transfer
 */
public class Cashback {
    private final double transferAmount;

    public Cashback(double transferAmount) {
        if (transferAmount < 0) {
            throw new IllegalArgumentException("Transfer amount cannot be negative.");
        }
        this.transferAmount = transferAmount;
    }

    public double calculateCashback() {
        if (transferAmount < 500) {
            return 0.0;
        }
        return Math.min(transferAmount * 0.01, 20.0);
    }
}
