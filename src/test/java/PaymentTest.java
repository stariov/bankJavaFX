import de.bank.oos.bank.Payment;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {

    Payment payment;

    String datum;
    double betrag;
    String beschreibung;
    double incoming;
    double outgoing;


    @BeforeEach
    public void init() {
        datum = "datum";
        betrag = 100;
        beschreibung = "beschreibung_payment";
        incoming = 0.1;
        outgoing = 0.9;

        payment = new Payment(datum, betrag, beschreibung, incoming, outgoing);
    }

    @Test
    public void constructor() {
        assertEquals(datum, payment.getDate());
        assertEquals(betrag, payment.getAmount());
        assertEquals(beschreibung, payment.getDescription());
        assertEquals(incoming, payment.getIncomingInterest());
        assertEquals(outgoing, payment.getOutgoingInterest());
    }

    @Test
    public void copy_constructor() {
        Payment copy = new Payment(payment);
        assertEquals(datum, copy.getDate());
        assertEquals(betrag, copy.getAmount());
        assertEquals(beschreibung, copy.getDescription());
        assertEquals(incoming, copy.getIncomingInterest());
        assertEquals(outgoing, copy.getOutgoingInterest());
    }

    @Test
    public void calculate_amount() {
        assertEquals(betrag - betrag * incoming, payment.calculate());
        Payment outgoingPayment = new Payment("heute", -100, "outgoingTest", 0.1, 0.9);
        assertEquals( -100 + -100 * 0.9 , outgoingPayment.calculate());
    }

    @Test
    public void equals_test() {
        Payment other = new Payment("datum", 100, "beschreibung_payment", 0.1, 0.9);
        assertEquals(payment, other);
    }

    @Test
    public void toString_test() {
        String paymentString =
                "Date: "             + datum                + "\n" +
                "Amount: "           + payment.calculate()  + "\n" +
                "Description: "      + beschreibung         + "\n" +
                "IncomingInterest: " + incoming             + "\n" +
                "OutgoingInterest: " + outgoing             + "\n";
        assertEquals(paymentString, payment.toString());
    }
}
