package com.wallet;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        WalletService service = new WalletService();

        User user = new User(1, "Kowsalya", "9876543210", "kowsalya@example.com", "1234", KycStatus.BASIC);
        BankAccount bank1 = new BankAccount("BANK1001", 25_000, true);
        BankAccount bank2 = new BankAccount("BANK1002", 5_000, false);
        service.registerBankAccountGlobally(bank1);
        service.registerBankAccountGlobally(bank2);
        user.linkBankAccount(bank1);
        user.linkBankAccount(bank2);

        System.out.println("=== Digital Wallet App | Module 2 ===");
        System.out.println("Demo user: " + user.getName() + " | PIN: 1234");
        boolean running = true;
        while (running) {
            System.out.println("\n1. View balances");
            System.out.println("2. Add money");
            System.out.println("3. Withdraw money");
            System.out.println("4. View transaction history");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> showBalances(user);
                    case "2" -> {
                        System.out.print("Enter 4-digit PIN: ");
                        if (!user.checkPin(scanner.nextLine().trim())) {
                            System.out.println("Incorrect PIN.");
                            break;
                        }
                        System.out.print("Bank account number: ");
                        String account = scanner.nextLine().trim();
                        System.out.print("Amount to add: Rs. ");
                        double amount = Double.parseDouble(scanner.nextLine().trim());
                        printTransaction(service.addMoney(user, account, amount));
                        showBalances(user);
                    }
                    case "3" -> {
                        System.out.print("Enter 4-digit PIN: ");
                        if (!user.checkPin(scanner.nextLine().trim())) {
                            System.out.println("Incorrect PIN.");
                            break;
                        }
                        System.out.print("Bank account number: ");
                        String account = scanner.nextLine().trim();
                        System.out.print("Amount to withdraw: Rs. ");
                        double amount = Double.parseDouble(scanner.nextLine().trim());
                        printTransaction(service.withdraw(user, account, amount));
                        showBalances(user);
                    }
                    case "4" -> {
                        List<Transaction> history = service.getHistoryLatestFirst(user.getId());
                        if (history.isEmpty()) System.out.println("No transactions yet.");
                        else history.forEach(System.out::println);
                    }
                    case "5" -> running = false;
                    default -> System.out.println("Invalid option. Choose 1–5.");
                }
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            } catch (RuntimeException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
        System.out.println("Thank you!");
        scanner.close();
    }

    private static void showBalances(User user) {
        System.out.printf("%nWallet balance: Rs. %.2f (KYC limit: Rs. %.2f)%n",
                user.getWallet().getBalance(), user.getKycStatus().getWalletLimit());
        for (BankAccount account : user.getBankAccounts()) {
            System.out.printf("Bank %s%s: Rs. %.2f%n", account.getAccountNumber(),
                    account.isPrimary() ? " [PRIMARY]" : "", account.getBalance());
        }
    }

    private static void printTransaction(Transaction tx) {
        System.out.println("\nTransaction result:");
        System.out.println(tx);
    }
}
