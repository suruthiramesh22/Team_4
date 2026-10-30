# Digital Wallet & UPI App — Module 1

This is a standalone Java console application for **Module 1: User, KYC and Accounts**.
It uses in-memory `HashMap` collections. No database or external libraries are required.

## Requirements
- JDK 17 or later
- Visual Studio Code
- VS Code Extension Pack for Java (recommended)

## Open and run in VS Code
1. Extract `Module1-DigitalWallet.zip`.
2. In VS Code, choose **File → Open Folder** and open the extracted `Module1-DigitalWallet` folder.
3. Open `src/main/java/com/digitalwallet/Main.java`.
4. Click **Run** above `main`, or use the integrated terminal:

   ```powershell
   javac -d out (Get-ChildItem -Recurse src/main/java -Filter *.java | ForEach-Object FullName)
   java -cp out com.digitalwallet.Main
   ```

   On macOS/Linux:

   ```bash
   mkdir -p out
   javac -d out $(find src/main/java -name "*.java")
   java -cp out com.digitalwallet.Main
   ```

## Features included
- Customer registration with unique 10-digit phone number
- Required name and email validation
- Exactly four numeric PIN digits
- Automatic Basic KYC, Active status and zero-balance wallet
- Basic wallet holding limit: ₹10,000
- Full wallet holding limit: ₹1,00,000
- Upgrade/downgrade KYC with downgrade balance validation
- Block and activate customer
- Link up to three bank accounts per customer
- Globally unique bank account number
- First linked bank account automatically becomes primary
- Change primary bank account
- Unlink bank account only when it has no pending transactions
- Automatically select a new primary account when the current primary is unlinked
- View customer, wallet and linked bank account details

## Package structure

```text
src/main/java/com/digitalwallet/
├── Main.java
├── model/
│   ├── User.java
│   ├── Customer.java
│   ├── Admin.java
│   ├── Account.java
│   ├── Wallet.java
│   ├── BankAccount.java
│   ├── KYCStatus.java
│   └── UserStatus.java
├── repository/
│   ├── UserRepository.java
│   └── BankAccountRepository.java
└── service/
    ├── UserService.java
    └── BankAccountService.java
```

## OOP concepts
- **Encapsulation:** model fields are private and accessed through methods.
- **Inheritance:** `Customer` and `Admin` extend `User`; `Wallet` and `BankAccount` extend `Account`.
- **Abstraction:** `User` and `Account` are abstract classes.
- **Polymorphism:** `getRole()` and `getAccountType()` are overridden by child classes.

## Notes for team integration
- Keep these shared model/repository classes as the common source for other modules.
- Other modules should use the existing `Customer.getWallet()`, `Customer.getBankAccounts()`,
  `Customer.getWalletLimit()`, and repository lookup methods rather than creating duplicate models.
- For money arithmetic, this project uses `BigDecimal` rather than `double`.
- PIN storage here is simplified for a classroom in-memory project. A production application
  must use secure salted password hashing and never store a plain PIN.
- `pendingTransactions` is a counter. Module 2/4 should increment it when a bank operation
  becomes pending and decrement it when that operation is completed or cancelled.
- Module 3 should enforce blocked-user checks, daily transfer limits and monthly Basic-KYC
  transfer limits in its transaction service.
