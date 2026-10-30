import CashbackModule.Cashback;
import CashbackModule.RewardsService;
import CashbackModule.CashbackTransaction;

public class Main {
    public static void main(String[] args) {
        RewardsService rewardsService = new RewardsService();

        System.out.println("=== Module 5: Rewards & Cashback ===");

        double[] transferAmounts = {400, 500, 1000, 2000, 5000};
        for (int i = 0; i < transferAmounts.length; i++) {
            Cashback cashback = new Cashback(transferAmounts[i]);
            System.out.printf("Transfer Rs.%.2f -> calculated cashback Rs.%.2f%n",
                    transferAmounts[i], cashback.calculateCashback());
        }

        System.out.println();
        double credited = rewardsService.awardCashback(
                "USER001",
                "TXN001",
                1000,
                true,
                5000,
                10000);
        System.out.printf("Cashback credited: Rs.%.2f%n", credited);
        System.out.printf("Monthly cashback total: Rs.%.2f%n",
                rewardsService.getMonthlyCashback("USER001"));

        // Example: reversed transfer -> reverse its cashback record.
        double reversed = rewardsService.reverseCashback("USER001", "TXN001");
        System.out.printf("Cashback reversal recorded: Rs.%.2f%n", reversed);

        System.out.println();
        System.out.println("Cashback transactions:");
        for (CashbackTransaction tx : rewardsService.getTransactions()) {
            System.out.printf("%s | %s | %s | Rs.%.2f | %s%n",
                    tx.getReferenceId(), tx.getUserId(), tx.getType(),
                    tx.getAmount(), tx.getCreatedAt());
        }
    }
}
