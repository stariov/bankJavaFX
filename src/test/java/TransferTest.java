import de.bank.oos.bank.IncomingTransfer;
import de.bank.oos.bank.OutgoingTransfer;
import de.bank.oos.bank.Transfer;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TransferTest {

    Transfer transfer;

    String datum;
    double betrag;
    String beschreibung;
    String sender;
    String recipient;

    @BeforeEach
    public void init() {
        datum = "01.01.2024";
        betrag = 200;
        beschreibung = "beschreibung_transfer";
        sender = "Alice";
        recipient = "Bob";

        transfer = new Transfer(datum, betrag, beschreibung, sender, recipient);
    }

    @Test
    public void constructor() {
        assertEquals(datum, transfer.getDate());
        assertEquals(betrag, transfer.getAmount());
        assertEquals(beschreibung, transfer.getDescription());
        assertEquals(sender, transfer.getSender());
        assertEquals(recipient, transfer.getRecipient());
    }

    @Test
    public void copy_constructor() {
        Transfer copy = new Transfer(transfer);

        assertEquals(datum, copy.getDate());
        assertEquals(betrag, copy.getAmount());
        assertEquals(beschreibung, copy.getDescription());
        assertEquals(sender, copy.getSender());
        assertEquals(recipient, copy.getRecipient());
    }

    @Test
    public void calculate_amount() {
        // Falls Transfer einfach amount zurückgibt:
        assertEquals(betrag, transfer.calculate());
    }

    @Test
    public void calculate_incoming_outgoing() {
        // Constructor does not have to be tested, all values are assigned by super()
        OutgoingTransfer outTransfer = new OutgoingTransfer(transfer);
        IncomingTransfer inTransfer = new IncomingTransfer(transfer);

        assertEquals(-betrag, outTransfer.calculate());
        assertEquals(betrag, inTransfer.calculate());
    }

    @Test
    public void equals_test() {
        Transfer other = new Transfer("01.01.2024", 200, "beschreibung_transfer", "Alice", "Bob");
        assertEquals(transfer, other);
    }

    @Test
    public void toString_test() {
        String expected =
                "Date: "        + datum                  + "\n" +
                "Amount: "      + transfer.calculate()   + "\n" +
                "Description: " + beschreibung           + "\n" +
                "Sender: "      + sender                 + "\n" +
                "Recipient: "   + recipient              + "\n";

        assertEquals(expected, transfer.toString());
    }
}
