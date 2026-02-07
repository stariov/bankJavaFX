package de.bank.oos.bank;

public class IncomingTransfer extends Transfer {
    /**
     * Creates a new {@link IncomingTransfer} object representing a money transfer between two parties.
     *
     * @param transfer the object which will be copied
     */
    public IncomingTransfer(Transfer transfer) {
        super(transfer.date, transfer.amount, transfer.description, transfer.getSender(), transfer.getRecipient());
    }

    /**
     * Creates object of IncomingTransfer.
     *
     * @param date date of transfer
     * @param amount amount of transferred money
     * @param description description of transfer
     * @param sender who sends the money
     * @param recipient who receives the money
     */
    protected IncomingTransfer(String date, double amount, String description, String sender, String recipient) {
        super(date, amount, description, sender, recipient);
    }

    // it is not necessary to override calculate(), since IncomingTransfers are positive just like a normal Transfer
}
