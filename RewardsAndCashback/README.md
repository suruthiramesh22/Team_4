# RewardsAndCashback — Module 5

Java console implementation for Module 5 (Rewards) from the Digital Wallet and UPI Payment App problem statement.

## Requirements covered

- 1% cashback on successful transfers of Rs. 500 or more.
- Maximum Rs. 20 cashback per transfer.
- Maximum Rs. 100 cashback per user per calendar month.
- No cashback for failed transfers, add-money, or withdrawals (the service is called only for successful transfers).
- Cashback is not credited if it would exceed the supplied KYC wallet limit.
- Cashback reversal record can be created when a transfer is reversed.
- All data is stored in in-memory collections; no database is used.

## Folder structure

```text
RewardsAndCashback/
├── src/
│   ├── Main.java
│   └── CashbackModule/
│       ├── Cashback.java
│       ├── CashbackTransaction.java
│       └── RewardsService.java
└── README.md
```

## Run in VS Code terminal

Install a JDK first. Check:

```bash
java -version
javac -version
```

Open the terminal in the `RewardsAndCashback` project folder and run:

```bash
javac -d out src/CashbackModule/Cashback.java src/CashbackModule/CashbackTransaction.java src/CashbackModule/RewardsService.java src/Main.java
java -cp out Main
```

## Important integration notes

- Call `awardCashback(...)` only after Module 3 confirms a transfer succeeded.
- Pass the user's current wallet balance and KYC wallet limit from the other modules.
- When a transfer is reversed, call `reverseCashback(userId, transferReferenceId)` and coordinate the actual wallet debit with the wallet/transfer module.
- This is a module-level starter implementation. Before merging, agree with the team on shared class names, package names, and method signatures.
- The monthly cashback total is tracked for the current calendar month in this starter version.
