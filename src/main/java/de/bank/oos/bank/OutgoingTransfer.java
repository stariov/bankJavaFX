package de.bank.oos.bank;

public class OutgoingTransfer extends Transfer {
    /**
     * Creates a new {@link OutgoingTransfer} object representing a money transfer between two parties.
     *
     * @param transfer the object which will be copied
     */
    public OutgoingTransfer(Transfer transfer) {
        super(transfer.date, transfer.amount, transfer.description, transfer.getSender(), transfer.getRecipient());
    }

    /**
     * Creates a new {@link OutgoingTransfer} object representing a money transfer between two parties.
     *
     * @param date date of transfer
     * @param amount transaction amount
     * @param description short description of transfer
     * @param sender who sends the money
     * @param recipient who receives the money
     */
    protected OutgoingTransfer(String date, double amount, String description, String sender, String recipient) {
        super(date, amount, description, sender, recipient);
    }

    /**
     * Calculates how much money will be withdrawn.
     * @return negative amount
     */
    @Override
    public double calculate() {
        return -1 * this.amount;
    }
}


