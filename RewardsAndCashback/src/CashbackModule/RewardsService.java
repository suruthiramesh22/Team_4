package CashbackModule;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Module 5: Rewards.
 * Uses in-memory collections as required by the project statement.
 *
 * Assumptions for integration:
 * - call awardCashback only after a transfer succeeds
 * - wallet balances and KYC limits are passed in by the calling module
 * - monthly cashback totals are tracked by user and month
 */
public class RewardsService {
    private static final double MONTHLY_LIMIT = 100.0;
    private static final double PER_TRANSFER_LIMIT = 20.0;

    private final List<CashbackTransaction> transactions = new ArrayList<>();
    private final Map<String, Double> monthlyCashback = new HashMap<>();

    /**
     * Attempts to credit cashback. Returns the credited amount (0 if not eligible).
     * walletBalance and kycWalletLimit should come from the wallet/user module.
     */
    public double awardCashback(
            String userId,
            String transferReferenceId,
            double transferAmount,
            boolean transferSuccessful,
            double walletBalance,
            double kycWalletLimit) {

        if (!transferSuccessful || transferAmount < 500) {
            return 0.0;
        }

        String monthKey = userId + "|" + YearMonth.now();
        double alreadyCredited = monthlyCashback.getOrDefault(monthKey, 0.0);
        double remainingMonthly = Math.max(0.0, MONTHLY_LIMIT - alreadyCredited);

        double calculated = Math.min(transferAmount * 0.01, PER_TRANSFER_LIMIT);
        double cashback = Math.min(calculated, remainingMonthly);

        if (cashback <= 0.0) {
            return 0.0;
        }

        // Do not credit cashback if it would exceed the user's KYC wallet limit.
        if (walletBalance + cashback > kycWalletLimit) {
            return 0.0;
        }

        transactions.add(new CashbackTransaction(
                transferReferenceId, userId, cashback, CashbackTransaction.Type.CREDIT));
        monthlyCashback.put(monthKey, alreadyCredited + cashback);
        return cashback;
    }

    /**
     * Reverses cashback for a reversed transfer.
     * Caller should ensure receiver/sender wallet balance changes are handled safely
     * by the wallet/transfer module. This method records the cashback reversal.
     */
    public double reverseCashback(String userId, String transferReferenceId) {
        for (int i = transactions.size() - 1; i >= 0; i--) {
            CashbackTransaction tx = transactions.get(i);
            if (tx.getUserId().equals(userId)
                    && tx.getReferenceId().equals(transferReferenceId)
                    && tx.getType() == CashbackTransaction.Type.CREDIT
                    && !tx.isReversed()) {
                tx.markReversed();
                transactions.add(new CashbackTransaction(
                        transferReferenceId + "-REV",
                        userId,
                        tx.getAmount(),
                        CashbackTransaction.Type.REVERSAL));
                return tx.getAmount();
            }
        }
        return 0.0;
    }

    public List<CashbackTransaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public double getMonthlyCashback(String userId) {
        String monthKey = userId + "|" + YearMonth.now();
        return monthlyCashback.getOrDefault(monthKey, 0.0);
    }
}
