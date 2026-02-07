package de.bank.oos.bank;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.bank.oos.bank.exceptions.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PrivateBank implements Bank {

    private String name;
    private double incomingInterest;
    private double outgoingInterest;
    private Map<String, List<Transaction>> accountsToTransactions = new HashMap<>();
    private String directoryName;

    //
    // getter, setter, etc. standard methods
    //

    /**
     * Returns the name of the {@link PrivateBank}.
     * @return name of bank
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the {@link PrivateBank}.
     * @param name name of bank
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the interests percentage which are calculated when withdrawing money.
     * @return interest between 0 and 1
     */
    public double getOutgoingInterest() { return outgoingInterest; }

    /**
     * Sets the interest percentage which are calculated when withdrawing money.
     * @param outgoingInterest between 0 and 1
     */
    public void setOutgoingInterest(double outgoingInterest) {
        if (outgoingInterest <= 1 && outgoingInterest >= 0) {
            this.outgoingInterest = outgoingInterest;
        }
        // Since the value is initialised when creating an object, it will never be NULL.
    }

    /**
     * Returns the interests percentage which are calculated when depositing money.
     * @return interest between 0 and 1
     */
    public double getIncomingInterest() { return incomingInterest; }

    /**
     * Sets the interest percentage which are calculated when depositing money.
     * @param incomingInterest between 0 and 1
     */
    public void setIncomingInterest(double incomingInterest) {
        if (incomingInterest <= 1 && incomingInterest >= 0) {
            this.incomingInterest = incomingInterest;
        }
        // Since the value is initialised when creating an object, it will never be NULL.
    }

    // constructors

    /**
     * Creates {@link PrivateBank} object.
     * @param name name of bank
     * @param incomingInterest interests which are applied when depositing (0 to 1)
     * @param outgoingInterest interests which are applied when withdrawing (0 to 1)
     * @param directoryName path where account data will be stored
     */
    public PrivateBank(@NotNull String name, double incomingInterest, double outgoingInterest, String directoryName) throws IOException {
        setName(name);
        setIncomingInterest(incomingInterest);
        setOutgoingInterest(outgoingInterest);
        setDirectory(directoryName);
        if (directoryName != null) readAccounts();
    }

    /**
     * Copies a {@link PrivateBank} object without copying the {@code accountsToTransactions} map.
     * @param other the PrivateBank object which will be copied
     */
    public PrivateBank(@NotNull PrivateBank other) {
        setName(other.name);
        setIncomingInterest(other.incomingInterest);
        setOutgoingInterest(other.outgoingInterest);
    }

    // object methods

    /**
     * Turns {@link PrivateBank} into a string.
     * @return a string representation of {@code this} {@link PrivateBank}
     */
    @Override
    public String toString() {
       return "Name: " + name + "\n" +
              "IncomingInterest: " + incomingInterest + "\n" +
              "OutgoingInterest: " + outgoingInterest + "\n" +
              "Account-Transactions: " + "\n" + accountsToTransactions.toString();
    }

    /**
     * Does a deep comparison of two {@link PrivateBank} objects.
     *
     * @param other second private bank
     * @return returns true, if the objects contain the same values
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || this.getClass() != other.getClass()) return false;
        PrivateBank privateBank = (PrivateBank) other;
        return
            this.getName().equals(privateBank.getName()) &&
            this.outgoingInterest == privateBank.outgoingInterest &&
            this.incomingInterest == privateBank.incomingInterest &&
            this.accountsToTransactions.equals(privateBank.accountsToTransactions);
    }

    //
    // Implementation of abstract methods from parent Interface
    //

    // Creating Accounts

    /**
     * Adds a new account without any transactions to the bank.
     * @param account the account to be added
     * @throws AccountAlreadyExistsException accountAlreadyExists
     */
    @Override
    public void createAccount(String account) throws AccountAlreadyExistsException, IOException {
        if(accountsToTransactions.containsKey(account)) throw new AccountAlreadyExistsException("Adding account: '" + account + "' failed. This account already exists!");
        accountsToTransactions.put(account, new ArrayList<>());
        writeAccount(account);
    }

    /**
     * Adds an account (with specified transactions) to the bank.
     * Important: duplicate transactions must not be added to the account!
     *
     * @param account name of the account
     * @param transactionList list of the transactions (only Transfer and Payment Objects)
     * @throws AccountAlreadyExistsException    if the account already exists
     * @throws TransactionAlreadyExistException if the transaction already exists
     * @throws TransactionAttributeException    if the validation check for certain attributes fail
     */
    @Override
    public void createAccount(String account, List<Transaction> transactionList) throws
            AccountAlreadyExistsException, TransactionAlreadyExistException, TransactionAttributeException
    {
        // input validation
        if (account == null || transactionList == null) { return; }
        if (accountsToTransactions.containsKey(account)) throw new AccountAlreadyExistsException("Account: '" + account + "' already exists and couldn't be added!");

        // add new account
        accountsToTransactions.put(account, new ArrayList<>());

        for (Transaction transaction : transactionList) {
            // this try will never fail
            try {
                this.addTransaction(account, transaction);
            } catch (AccountDoesNotExistException | IOException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }


    // Handling transactions

    /**
     * Add a transaction to an already existing account.
     *
     * @param account     the account to which the transaction is added
     * @param transaction the transaction which should be added to the specified account
     * @throws TransactionAlreadyExistException if the transaction already exists
     * @throws AccountDoesNotExistException if the account doesn't exist
     * @throws TransactionAttributeException if attributes of the transaction are not set correctly
     */
    @Override
    public void addTransaction(String account, Transaction transaction) throws
            TransactionAlreadyExistException, TransactionAttributeException, IOException, AccountDoesNotExistException {
        // input validation
        if (account == null || transaction == null) { return; }
        if (!accountsToTransactions.containsKey(account)) throw new AccountDoesNotExistException("Account: '" + account + "' doesn't exist!");

        // is the transaction valid and a transfer or payment?
        if (!transaction.validate() || transaction instanceof IncomingTransfer || transaction instanceof OutgoingTransfer)
            throw new TransactionAttributeException("The transaction is not valid. Couldn't add to account!");
        if (transaction instanceof Transfer transfer && (!transfer.getSender().equals(account) || !accountsToTransactions.containsKey(transfer.getRecipient())))
            throw new TransactionAttributeException("The transaction is not valid. Couldn't add to account!");

        List<Transaction> accountTransactions = accountsToTransactions.get(account);
        // It would make more sense if the whole transactionList failed and the accountList remained unchanged.
        if (accountTransactions.contains(transaction)) throw new TransactionAlreadyExistException("The transaction is a duplicate!");
        // only transfers have to be saved in both account lists
        if (transaction instanceof Transfer transfer) {
            accountsToTransactions.get(transfer.getRecipient()).add(new IncomingTransfer(transfer));
            accountTransactions.add(new OutgoingTransfer(transfer));
            writeAccount(transfer.getRecipient());
        }
        else {
            Payment payment = new Payment((Payment) transaction);
            payment.setIncomingInterest(this.incomingInterest);
            payment.setOutgoingInterest(this.outgoingInterest);
            accountTransactions.add(payment); // transaction is a payment
        }
        writeAccount(account);
    }

    @Override
    public void removeTransaction(String account, Transaction transaction) throws
            AccountDoesNotExistException, TransactionDoesNotExistException, IOException {
        // validation of inputs
        if (account == null || transaction == null || !transaction.validate()) return;
        if (transaction instanceof Transfer transfer && !transfer.getSender().equals(account)) throw new TransactionDoesNotExistException("Sender des Transfers entspricht nicht dem Account.");
        if (transaction instanceof Transfer transfer && !accountsToTransactions.containsKey(transfer.getRecipient()) || !accountsToTransactions.containsKey(account))
            throw new AccountDoesNotExistException("Sender or/and recipient account don't exist!");

        // changing Interest before comparing
        Payment payment = null;
        if (transaction instanceof Payment payment1) {
            payment = new Payment(payment1);
            payment.setOutgoingInterest(this.outgoingInterest);
            payment.setIncomingInterest(this.incomingInterest);
        }
        if (!accountsToTransactions.get(account).contains(transaction) && !accountsToTransactions.get(account).contains(payment))
            throw new TransactionDoesNotExistException("The transaction does not exist!\n" + transaction);


        // removing transaction
        List<Transaction> accountTransactions = accountsToTransactions.get(account);
        if (transaction instanceof Transfer transfer) {
            accountTransactions.remove(new OutgoingTransfer(transfer));
            accountsToTransactions.get(transfer.getRecipient()).remove(new IncomingTransfer(transfer));
            writeAccount(transfer.getRecipient());
        }
        else {
            accountTransactions.remove(payment);
        }
        writeAccount(account);
    }

    /**
     * Checks whether the specified transaction for a given account exists.
     *
     * @param account     the account from which the transaction is checked
     * @param transaction the transaction to search/look for
     * @return if the transaction exists for the given account
     */
    @Override
    public boolean containsTransaction(String account, Transaction transaction) {
        if (account == null || transaction == null) return false;
        if (!accountsToTransactions.containsKey(account)) return false;
        // if it's a payment we have to ignore the incoming and outgoing interests
        if (transaction instanceof Payment) {
            Payment payment = new Payment((Payment) transaction);
            payment.setIncomingInterest(incomingInterest);
            payment.setOutgoingInterest(outgoingInterest);
            return accountsToTransactions.get(account).contains(payment);
        }
        // check for Transfer
        return accountsToTransactions.get(account).contains(transaction);
    }

    /**
     * Calculates the balance of one account.
     * @param account the selected account
     * @return balance of account
     */
    @Override
    public double getAccountBalance(String account) {
        if (!accountsToTransactions.containsKey(account)) return 0;
        double balance = 0;
        List<Transaction> transactionList = accountsToTransactions.get(account);
        for (Transaction transaction : transactionList) {
            if (transaction instanceof Transfer transfer)
                balance += transfer.calculate();
        }
        return balance;
    }


    // Getting transaction lists

    /**
     * Returns a list of transactions for an account.
     *
     * @param account the selected account
     * @return the list of all transactions for the specified account
     */
    @Override
    public List<Transaction> getTransactions(String account) {
        if (account == null) return null;
        if (accountsToTransactions.containsKey(account))
            return accountsToTransactions.get(account);
        return null;
    }

    /**
     * Returns a sorted list (-> calculated amounts) of transactions for a specific account. Sorts the list either in ascending or descending order
     * (or empty).
     *
     * @param account the selected account
     * @param asc     selects if the transaction list is sorted in ascending or descending order
     * @return the sorted list of all transactions for the specified account
     */
    @Override
    public List<Transaction> getTransactionsSorted(String account, boolean asc) {
        if (account == null) return null;
        if (accountsToTransactions.containsKey(account)) {
            List<Transaction> list = new ArrayList<>(accountsToTransactions.get(account));
            list.sort(Comparator.comparingDouble(Transaction::calculate));
            if (asc) {
                return list;
            } else {
                return list.reversed();
            }
        }
        return null;
    }

    /**
     * Returns a list of either positive or negative transactions (-> calculated amounts).
     *
     * @param account  the selected account
     * @param positive selects if positive or negative transactions are listed
     * @return the list of all transactions by type
     */
    public List<Transaction> getTransactionsByType(String account, boolean positive) {
        if (account == null) return null;
        if (!accountsToTransactions.containsKey(account)) return null;
        List<Transaction> list = new ArrayList<>();
        if (positive) {
            for (Transaction transaction : accountsToTransactions.get(account)) {
                if (transaction.calculate() >= 0) list.add(transaction);
            }
        }
        else {
            for (Transaction transaction : accountsToTransactions.get(account)) {
                if (transaction.calculate() < 0) list.add(transaction);
            }
        }
        return list;
    }

    // persistence directoryName

    /**
     * Returns where Account information is stored.
     * @return path of database
     */
    public String getDirectory() { return directoryName; }

    /**
     * Sets where account data will be stored.
     * Can only set the directoryName if it has not been assigned already.
     *
     * @param directoryName where the account data will be stored
     */
    public void setDirectory(String directoryName) { if (this.directoryName == null) this.directoryName = directoryName; }

    // persistence of account data

    /**
     * Reads all files from a folder. Each file represents one account, and it's transaction history.
     * @throws IOException if folder could not be opened
     */
    private void readAccounts() throws IOException {
        if (directoryName == null) throw new IOException("Cannot read directory if none is set!");

        File folder = new File(directoryName);
        File[] files = folder.listFiles();

        if (files == null) return;

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(Transaction.class, new TransactionSerial())
                .create();

        Type listType = new TypeToken<List<Transaction>>(){}.getType();
        for (File file : files) {
            if (!file.getName().endsWith(".json")) continue;

            FileReader fReader = new FileReader(file);

            accountsToTransactions.put(file.getName().replace(".json", ""), gson.fromJson(fReader, listType));

            fReader.close();
        }


    }

    /**
     * Creates file from account and it's transaction list.
     * @param account name of file and account which will be stored
     * @throws IOException if file could not be opened
     */
    private void writeAccount(String account) throws IOException {
        if (directoryName == null) throw new IOException("Cannot write into directory if none is set!");

        PrintWriter pWriter = new PrintWriter(new FileOutputStream(directoryName + account + ".json"));
        List<Transaction> list = accountsToTransactions.get(account);

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(Transaction.class, new TransactionSerial())
                .create();

        Type type = new TypeToken<List<Transaction>>(){}.getType();
        String json = gson.toJson(list, type);

        pWriter.print(json);
        pWriter.close();
    }

    /**
     * Returns all Accounts of Bank.
     *
     * @return list of all accounts
     */
    public List<String>getAllAccounts() {
        return this.accountsToTransactions.keySet().stream().toList();
    }

    /**
     * Removes an account.
     *
     * @param account name of account
     */
    public void deleteAccount(String account) throws AccountDoesNotExistException, IOException {
        if(!accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException("Account: " + account + "ist kein Mitglied.");
        }

        List<Transaction> transactionList = accountsToTransactions.get(account);

        for (Transaction transaction : transactionList) {
            try {
                removeTransaction(account, transaction);
            } catch (TransactionDoesNotExistException e) {
                // if this happens you have a problem
                throw new RuntimeException(e);
            }
        }

        Path path = Path.of(directoryName, account + ".json");
        Files.deleteIfExists(path);
        accountsToTransactions.remove(account);
    }
}
