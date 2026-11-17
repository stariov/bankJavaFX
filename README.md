# bankJavaFX

![Java](https://img.shields.io/badge/Java-23%2B-orange) ![JavaFX](https://img.shields.io/badge/JavaFX-25-blue) [![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A small desktop banking app built with **JavaFX**. You can create accounts, book payments and transfers between accounts, and browse each account's transaction history. All data is saved locally as JSON files.

> This is a learning project (package `de.bank.oos`). It is not meant to handle real money.

---

## Features

- **Accounts**: create, select and delete accounts from the overview screen.
- **Transactions**:
  - **Payment**: a deposit (positive amount) or withdrawal (negative amount). The bank's interest rates are applied automatically.
  - **Transfer**: moves money from one account to another. It is booked on both sides, as an `OutgoingTransfer` for the sender and an `IncomingTransfer` for the recipient.
- **History view**: sort transactions by amount (ascending or descending), or show only incoming or only outgoing ones.
- **Persistence**: each account is stored as `bank/<account>.json`. Data is reloaded on startup and saved after every change.
- **Validation**: you get an error dialog for duplicate accounts or transactions, unknown recipients, or invalid values.

## Tech stack

| | |
|---|---|
| Language | Java (compiled with `--release 22`) |
| UI | JavaFX 25 (controls + FXML) |
| JSON | Gson 2.11 with a custom polymorphic (de)serializer |
| Build | Maven |
| Tests | JUnit 5 |

## Getting started

### Prerequisites

- **JDK 23 or newer.** The README used to say Java 17+, but the build targets release 22 and JavaFX 25 requires JDK 23+.
- **Maven 3.8+**

You don't need to install JavaFX separately. Maven downloads it.

### Run the app

```bash
git clone https://github.com/stariov/bankJavaFX.git
cd bankJavaFX
mvn javafx:run
```

### Run the tests

```bash
mvn test
```

## Using the app

1. **Accounts overview**: type a name and click **Add Account**. Select an account to open it, or delete it with **Delete Account**.
2. **Account view**: shows the account ID, the current balance and the transaction list.
   - **Add Transaction** opens a dialog. Enter the amount, date and description, then choose **Payment** or **Transfer** (a transfer also needs a recipient).
   - **Ascending / Descending** sorts the list. **Positiv / Negativ** filters it.
   - Select a transaction and delete it with the delete button.
   - **← Back** returns to the overview.

## Configuration

The bank is created in `Main.java`:

```java
new PrivateBank("ProjectB", 0.1, 0.1, "./bank/");
//              name        in   out  data directory
```

- `incomingInterest` (0–1) is deducted from deposits: a 100 € deposit at 0.1 books **90 €**.
- `outgoingInterest` (0–1) is added to withdrawals: a −100 € payment at 0.1 books **−110 €**.
- Transfers are booked without interest.

## Data format

Every account file contains a list of transactions. Each one is wrapped with its class name so that it can be deserialized back into the right type:

```json
[
  {
    "CLASSNAME": "Payment",
    "INSTANCE": {
      "incomingInterest": 0.1,
      "outgoingInterest": 0.1,
      "date": "01.01.2025",
      "amount": 100.0,
      "description": "Salary"
    }
  }
]
```

The `bank/` directory is in `.gitignore` (only `.gitkeep` is committed), so your local data is never pushed.

## Project structure

```
src/main/java/de/bank/oos/
├── Main.java                     # JavaFX entry point
├── bank/
│   ├── Bank.java                 # Bank interface (accounts + transactions API)
│   ├── PrivateBank.java          # Implementation + JSON file I/O
│   ├── Transaction.java          # Abstract base class (date, amount, description)
│   ├── CalculateBill.java        # calculate() interface
│   ├── Payment.java              # Deposit/withdrawal with interest
│   ├── Transfer.java             # Transfer between accounts
│   ├── IncomingTransfer.java     # Positive side of a transfer
│   ├── OutgoingTransfer.java     # Negative side of a transfer
│   ├── TransactionSerial.java    # Gson (de)serializer
│   └── exceptions/               # BankException + specific subclasses
└── controller/
    ├── AccountsViewController.java
    ├── TransactionViewController.java
    └── AddTransactionController.java
src/main/resources/               # FXML views
src/test/java/                    # JUnit 5 tests (PrivateBank, Payment, Transfer)
```

## Known limitations

- Dates are free-text strings and are not validated or parsed.
- `getAccountBalance` currently sums **only transfers**. Payments are not included in the displayed balance.
- The bank name, interest rates and data path are hard-coded in `Main.java`.
- The delete button in the transaction view is labelled "Delete Account", but it deletes the selected **transaction**.
- `ListViewExperiments.java` is a leftover JavaFX sandbox class and isn't used by the app.

## License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.
