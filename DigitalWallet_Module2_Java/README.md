# Digital Wallet and UPI Payment App — Module 2 (Java)

Simple console-based Java 17 project. It implements **Add Money** and **Withdraw** and includes the minimum user/wallet/bank-account setup needed to run Module 2.

## Implemented
- Add money from a linked bank account (₹10–₹50,000 per transaction).
- Reject add-money if bank balance is insufficient.
- Reject add-money if wallet balance would exceed KYC limit (Basic ₹10,000; Full ₹1,00,000).
- Withdraw from wallet to a linked bank account; wallet cannot go negative.
- Each operation creates a transaction record, including failed operations.
- Bank account balances are tracked individually.
- User status (Active/Blocked), up to 3 linked accounts, unique bank account numbers, and one primary account.
- A bank account with a pending transaction cannot be unlinked (pending-state support is included in the model; Module 2 does not create pending transactions).

## Run
Install Java 17+ and Maven, then from this folder run:

```bash
mvn clean compile exec:java
```

Or compile and run from an IDE by running `com.wallet.Main`.

## Demo data
On startup, the app creates:
- User ID 1: Kowsalya, Basic KYC, wallet ₹0
- Bank account `BANK1001`, balance ₹25,000, primary
- Bank account `BANK1002`, balance ₹5,000

Use menu options to add money, withdraw, view balances, view transaction history, or exit. The PIN is `1234` (demo only).

## Git: create a separate branch and push
Run these commands inside this project folder after creating a GitHub repository (or inside your existing repository if this folder is already part of it):

```bash
git checkout -b feature/module-2-add-withdraw
git add .
git commit -m "Implement Module 2 add money and withdraw"
git push -u origin feature/module-2-add-withdraw
```

If you have not cloned your GitHub repository yet, clone it first and copy these project files into that repository before running the commands. Do not push real PINs, bank details, or other secrets. Demo data in this project is fictional.
