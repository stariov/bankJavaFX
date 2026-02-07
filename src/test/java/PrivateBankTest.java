import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.bank.oos.bank.*;
import de.bank.oos.bank.exceptions.AccountAlreadyExistsException;
import de.bank.oos.bank.exceptions.TransactionAlreadyExistException;
import de.bank.oos.bank.exceptions.TransactionAttributeException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class PrivateBankTest {

    // bank
    PrivateBank pBank;
    final String name = "bigTonka";
    static final String directory = "testData/";
    final double incoming = 0.1;
    final double outgoing = 0.5;

    // transactions
    Transaction transactionT;
    final String date = "00.22.3-10";
    final double betrag = 100;
    final String beschreibung = "beschreibung_transfer";
    final String sender = "you";
    final String recipient = "i";

    Transaction transactionP;

    // tools

    final static File folder = new File(directory);

    final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeAdapter(Transaction.class, new TransactionSerial())
            .create();

    final Type listType = new TypeToken<List<Transaction>>(){}.getType();

    // before routine

    @BeforeAll
    static void setup() throws IOException {
        if (!folder.mkdir())
            throw new IOException("Could not create folder: " + folder.getName() + "!");
    }

    @BeforeEach
    void init() {
        pBank = assertDoesNotThrow(() -> new PrivateBank(name, incoming, outgoing, directory));
        transactionT = new Transfer(date, betrag, beschreibung, sender, recipient);
        transactionP = new Payment(date + "P", 2 * betrag, beschreibung + "P", 0.5, 0.6);
    }

    // tests

    @Test
    void constructor_setsFieldsCorrectly() {
        assertEquals(name, pBank.getName());
        assertEquals(incoming, pBank.getIncomingInterest());
        assertEquals(outgoing, pBank.getOutgoingInterest());
        assertEquals(directory, pBank.getDirectory());
    }

    @ParameterizedTest
    @ValueSource(strings = {"einKunde", "", " "})
    void createAccount(String account) {
        assertDoesNotThrow(() -> pBank.createAccount(account));
        assertDoesNotThrow(() -> pBank.getTransactions(account));
    }

    @Test
    void createAccountWithList() throws IOException, TransactionAlreadyExistException, AccountAlreadyExistsException, TransactionAttributeException {
        // creating list of transactions
        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transactionP);
        transactionList.add(transactionT);

        // creating accounts
        assertDoesNotThrow(() -> pBank.createAccount("i"));
        pBank.createAccount("you", transactionList);

        // reading transaction, deserializing them
        FileReader fReader = assertDoesNotThrow(() -> new FileReader(directory + "you.json"));
        List<Transaction> deserializedList = gson.fromJson(fReader, listType);

        // creating list of altered transactions
        transactionList = new ArrayList<>();
        OutgoingTransfer tra1 = new OutgoingTransfer((Transfer) transactionT);
        Payment pay0 = new Payment((Payment) transactionP);
        pay0.setIncomingInterest(pBank.getIncomingInterest());
        pay0.setOutgoingInterest(pBank.getOutgoingInterest());

        transactionList.add(pay0);
        transactionList.add(tra1);

        // comparing
        assertEquals(transactionList, deserializedList);

        // closing
        fReader.close();
    }

    @Test
    void addTransaction() throws AccountAlreadyExistsException, IOException {
        // accounts hinzufügen
        pBank.createAccount(sender);
        pBank.createAccount(recipient);

        // try
        assertDoesNotThrow(() -> pBank.addTransaction(sender, transactionT));

        // test
        FileReader reader = new FileReader(directory + sender + ".json");
        List<Transaction> deserializedList = gson.fromJson(reader, listType);

        OutgoingTransfer outTransfer = new OutgoingTransfer((Transfer) transactionT);

        assertEquals(outTransfer, deserializedList.getFirst());

        // closing reader otherwise it is not possible to delete file
        reader.close();
    }

    @Test
    void removeTransaction() throws TransactionAlreadyExistException, AccountAlreadyExistsException, TransactionAttributeException, IOException {
        // creating list of transactions
        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transactionP);
        transactionList.add(transactionT);

        // creating accounts
        pBank.createAccount("i");
        pBank.createAccount("you", transactionList);

        // try
        assertDoesNotThrow(() -> pBank.removeTransaction("you", transactionP));

        // test
        transactionList.remove(transactionP);
        assertEquals(transactionList, pBank.getTransactions("you"));
    }

    @Test
    void containsTransaction() throws AccountAlreadyExistsException, IOException, TransactionAlreadyExistException, TransactionAttributeException {
        // creating list of transactions
        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transactionP);
        transactionList.add(transactionT);

        // creating accounts
        pBank.createAccount("i");
        pBank.createAccount("you", transactionList);

        // try
        Payment payment = new Payment((Payment) transactionP);
        payment.setOutgoingInterest(pBank.getOutgoingInterest());
        payment.setIncomingInterest(pBank.getIncomingInterest());

        assertTrue(pBank.containsTransaction(sender, new OutgoingTransfer((Transfer) transactionT)));
        assertTrue(pBank.containsTransaction(sender, payment));
        assertTrue(pBank.containsTransaction(sender, transactionP));
    }

    @Test
    void accountBalance() throws AccountAlreadyExistsException, IOException, TransactionAlreadyExistException, TransactionAttributeException {
        // creating list of transactions
        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transactionP);
        transactionList.add(transactionT);

        // creating accounts
        pBank.createAccount("i");
        pBank.createAccount("you", transactionList);

        // test and try
        assertEquals(-100, pBank.getAccountBalance(sender));
        assertEquals(100, pBank.getAccountBalance(recipient));
    }

    @Test
    void getTransactions() throws AccountAlreadyExistsException, IOException, TransactionAlreadyExistException, TransactionAttributeException {
        // creating list of transactions
        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transactionP);
        transactionList.add(transactionT);

        // creating accounts
        pBank.createAccount("i");
        pBank.createAccount("you", transactionList);

        // creating list of altered transactions
        transactionList = new ArrayList<>();
        OutgoingTransfer tra1 = new OutgoingTransfer((Transfer) transactionT);
        Payment pay0 = new Payment((Payment) transactionP);
        pay0.setIncomingInterest(pBank.getIncomingInterest());
        pay0.setOutgoingInterest(pBank.getOutgoingInterest());

        transactionList.add(pay0);
        transactionList.add(tra1);

        // comparing
        assertEquals(transactionList, pBank.getTransactions(sender));
    }

    @Test
    void getTransactionSorted() throws AccountAlreadyExistsException, IOException, TransactionAlreadyExistException, TransactionAttributeException {
        // creating list of transactions
        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transactionP);
        transactionList.add(transactionT);

        // creating accounts
        pBank.createAccount("i");
        pBank.createAccount("you", transactionList);

        // creating list of altered transactions
        transactionList = new ArrayList<>();
        OutgoingTransfer tra1 = new OutgoingTransfer((Transfer) transactionT);
        Payment pay0 = new Payment((Payment) transactionP);
        pay0.setIncomingInterest(pBank.getIncomingInterest());
        pay0.setOutgoingInterest(pBank.getOutgoingInterest());

        transactionList.add(pay0);
        transactionList.add(tra1);

        transactionList.sort(Comparator.comparingDouble(Transaction::calculate));

        // comparing
        assertEquals(transactionList, pBank.getTransactionsSorted(sender, true));
        assertEquals(transactionList.reversed(), pBank.getTransactionsSorted(sender, false));
    }

    @Test
    void getTransactionsByType() throws TransactionAlreadyExistException, AccountAlreadyExistsException, TransactionAttributeException, IOException {
        // creating list of transactions
        List<Transaction> transactionsYou = new ArrayList<>();
        transactionsYou.add(transactionT);
        transactionsYou.add(transactionP);

        List<Transaction> transactionsMe = new ArrayList<>();
        transactionsMe.add(new Transfer(date, 100, beschreibung, "tom", sender));
        transactionsMe.add(new Transfer(date, 200, beschreibung, "tom", sender));

        // creating accounts
        pBank.createAccount(recipient);
        pBank.createAccount(sender, transactionsYou);
        pBank.createAccount("tom", transactionsMe);

        // test
        // poslist
        List<Transaction> poslist = new ArrayList<>();
        Payment payment = new Payment((Payment) transactionP);
        payment.setOutgoingInterest(outgoing);
        payment.setIncomingInterest(incoming);
        poslist.add(payment);
        poslist.add(new Transfer(date, 100, beschreibung, "tom", sender));
        poslist.add(new Transfer(date, 200, beschreibung, "tom", sender));
        // neglist
        List<Transaction> neglist = new ArrayList<>();
        neglist.add(transactionT);

        // try and test
        assertEquals(poslist ,pBank.getTransactionsByType(sender, true));
        assertEquals(neglist, pBank.getTransactionsByType(sender, false));
    }

    // after routine

    @AfterEach
    void reset() throws IOException {
        File[] files = folder.listFiles();

        if (files != null)
            for (File file : files)
                if (!file.delete())
                    throw new IOException("Could not delete file: " + file.getName() + "!");
    }

    @AfterAll
    static void cleanup() throws IOException {
        if (!folder.delete())
            throw new IOException("Could not delete folder: " + folder.getName() + "!");
    }

}