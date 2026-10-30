package com.digitalwallet;

import com.digitalwallet.model.Customer;
import com.digitalwallet.model.KYCStatus;
import com.digitalwallet.model.UserStatus;
import com.digitalwallet.repository.BankAccountRepository;
import com.digitalwallet.repository.UserRepository;
import com.digitalwallet.service.BankAccountService;
import com.digitalwallet.service.UserService;

import java.math.BigDecimal;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();
        BankAccountRepository bankRepository = new BankAccountRepository();

        UserService userService = new UserService(userRepository);
        BankAccountService bankService =
                new BankAccountService(userRepository, bankRepository);

        // Optional sample user so you can test immediately.
        Customer sample = userService.register(
                "U100", "Sample User", "9000000001",
                "sample@example.com", "1234");
        bankService.linkBankAccount(
                sample.getPhone(), "BA100", "State Bank of India",
                "123456789012", new BigDecimal("25000.00"));

        System.out.println("Sample data created:");
        System.out.println("Phone: 9000000001 | PIN: 1234");
        System.out.println("You can also register your own customers.");

        int choice;
        do {
            printMenu();
            choice = readInt("Enter choice: ");

            try {
                switch (choice) {
                    case 1 -> registerUser(userService);
                    case 2 -> userService.displayUser(readText("Enter phone number: "));
                    case 3 -> updateKyc(userService);
                    case 4 -> updateStatus(userService);
                    case 5 -> linkBankAccount(bankService);
                    case 6 -> unlinkBankAccount(bankService);
                    case 7 -> changePrimary(bankService);
                    case 8 -> bankService.displayAccounts(readText("Enter phone number: "));
                    case 9 -> markPending(bankService);
                    case 10 -> completePending(bankService);
                    case 0 -> System.out.println("Exiting Module 1. Goodbye!");
                    default -> System.out.println("Invalid menu option.");
                }
            } catch (RuntimeException exception) {
                System.out.println("Operation failed: " + exception.getMessage());
            }
        } while (choice != 0);

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n========== DIGITAL WALLET | MODULE 1 ==========");
        System.out.println("1. Register customer");
        System.out.println("2. View customer and wallet details");
        System.out.println("3. Update KYC status");
        System.out.println("4. Block / Activate customer");
        System.out.println("5. Link bank account");
        System.out.println("6. Unlink bank account");
        System.out.println("7. Change primary bank account");
        System.out.println("8. View linked bank accounts");
        System.out.println("9. [Testing] Mark bank account transaction as pending");
        System.out.println("10. [Testing] Complete a pending bank transaction");
        System.out.println("0. Exit");
    }

    private static void registerUser(UserService service) {
        String id = readText("User ID: ");
        String name = readText("Name: ");
        String phone = readText("10-digit phone: ");
        String email = readText("Email: ");
        String pin = readText("4-digit PIN: ");

        Customer customer = service.register(id, name, phone, email, pin);
        System.out.println("Registration successful.");
        System.out.println("Customer: " + customer.getName());
        System.out.println("Wallet created: " + customer.getWallet().getAccountId());
        System.out.println("Opening wallet balance: Rs. " + customer.getWallet().getBalance());
        System.out.println("Default KYC: " + customer.getKycStatus());
        System.out.println("Default status: " + customer.getUserStatus());
    }

    private static void updateKyc(UserService service) {
        String phone = readText("Customer phone: ");
        System.out.println("1. BASIC");
        System.out.println("2. FULL");
        int option = readInt("Choose KYC: ");
        KYCStatus status = switch (option) {
            case 1 -> KYCStatus.BASIC;
            case 2 -> KYCStatus.FULL;
            default -> throw new IllegalArgumentException("Choose 1 or 2.");
        };
        service.updateKyc(phone, status);
        System.out.println("KYC updated to " + status + ".");
    }

    private static void updateStatus(UserService service) {
        String phone = readText("Customer phone: ");
        System.out.println("1. ACTIVE");
        System.out.println("2. BLOCKED");
        int option = readInt("Choose status: ");
        UserStatus status = switch (option) {
            case 1 -> UserStatus.ACTIVE;
            case 2 -> UserStatus.BLOCKED;
            default -> throw new IllegalArgumentException("Choose 1 or 2.");
        };
        service.updateStatus(phone, status);
        System.out.println("Customer status updated to " + status + ".");
    }

    private static void linkBankAccount(BankAccountService service) {
        String phone = readText("Customer phone: ");
        String id = readText("Bank account ID: ");
        String bank = readText("Bank name: ");
        String number = readText("Bank account number (6-20 digits): ");
        BigDecimal balance = readMoney("Opening bank balance: ");
        service.linkBankAccount(phone, id, bank, number, balance);
        System.out.println("Bank account linked successfully.");
    }

    private static void unlinkBankAccount(BankAccountService service) {
        String phone = readText("Customer phone: ");
        String number = readText("Bank account number: ");
        service.unlinkBankAccount(phone, number);
        System.out.println("Bank account unlinked successfully.");
    }

    private static void changePrimary(BankAccountService service) {
        String phone = readText("Customer phone: ");
        String number = readText("Bank account number: ");
        service.changePrimary(phone, number);
        System.out.println("Primary bank account updated.");
    }

    private static void markPending(BankAccountService service) {
        String number = readText("Bank account number: ");
        service.markPending(number);
        System.out.println("Pending transaction count increased.");
    }

    private static void completePending(BankAccountService service) {
        String number = readText("Bank account number: ");
        service.completePending(number);
        System.out.println("Pending transaction count decreased.");
    }

    private static String readText(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(String prompt) {
        System.out.print(prompt);
        return Integer.parseInt(scanner.nextLine().trim());
    }

    private static BigDecimal readMoney(String prompt) {
        System.out.print(prompt);
        return new BigDecimal(scanner.nextLine().trim());
    }
}
