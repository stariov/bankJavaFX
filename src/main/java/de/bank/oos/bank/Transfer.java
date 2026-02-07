package de.bank.oos.bank;

/**
 * <p>Attributes</p>
 * <ul>
 *     <li>String sender</li>
 *     <li>String recipient</li>
 * </ul>
 * <p>Constructors</p>
 * <ul>
 *     <li>{@code String date, double amount, String description}</li>
 *     <li>{@code Transfer other}</li>
 * </ul>
 */
public class Transfer extends Transaction {
    private String sender;
    private String recipient;

    /**
     * Sets the amount of money which will be sent to the recipient.
     *
     * @param amount is the value of the {@link Transfer}
     */
    @Override
    public void setAmount(double amount) {
        if (amount > 0) {
            this.amount = amount; }
        else {
            System.out.println("Transfers can only have positiv amounts");
        }
    }

    /**
     * Set the identifier (who sends the money) of {@link Transfer}.
     *
     * @param sender the identifier of the sender
     */
    public void setSender(String sender) { this.sender = sender; }

    /**
     * Gets the identifier of the sender.
     *
     * @return the identifier of the sender
     */
    public String getSender() { return sender; }

    /**
     * Set the identifier of the recipient.
     *
     * @param recipient the identifier of who will receive the money
     */
    public void setRecipient(String recipient) { this.recipient = recipient; }

    /**
     * Gets the recipient who will receive the money.
     *
     * @return identifier of the recipient
     */
    public String getRecipient() { return recipient; }

    /**
     * The constructor of {@link Transfer}.
     *
     * @param date the date at which the {@link Transfer} will occur
     * @param amount the amount of money which will be transferred
     * @param description a description for the transfer
     */
    public Transfer(String date, double amount, String description) {
        super(date, amount, description);
    }

    /**
     * Creates a new {@link Transfer} object representing a money transfer between two parties.
     *
     * @param date the date of the transfer
     * @param amount the amount of money being transferred
     * @param description a description or note of the transfer
     * @param sender the identifier of the sender
     * @param recipient the identifier of the receiver
     */
    public Transfer(String date, double amount, String description, String sender, String recipient) {
        super(date, amount, description);
        setSender(sender);
        setRecipient(recipient);
    }

    /**
     *  Creates a copy of another {@link Transfer}.
     *
     * @param transfer the object which will be deep copied
     */
    public Transfer(Transfer transfer) {
        super(transfer.date, transfer.amount, transfer.description);
        setSender(transfer.sender);
        setRecipient(transfer.recipient);
    }

    /**
     * Prints the names and values of all attributes of an {@link Transfer} object.
     */
    @Override
    public void printObject() {
        System.out.println("TRANSFER --------------------");
        super.printObject();
        System.out.println( "Sender: "      + this.sender    + "\n" +
                            "Recipient: "   + this.recipient + "\n"
        );
    }

    /**
     * Returns the value of the {@link Transfer}.
     *
     * @return returns the amount
     */
    @Override
    public double calculate() {
        return amount;
    }

    /**
     * Turns the names and values of all attributes of an {@link Transfer} object into a string.
     *
     * @return all information about the {@link Transfer}
     */
    @Override
    public String toString() {
        return super.toString() +   "Sender: "      + this.sender    + "\n" +
                                    "Recipient: "   + this.recipient + "\n";
    }

    /**
     * Compares {@code this's} attribute's values with those of the obj.
     * No pointer comparison.
     *
     * @param obj object which will be compared with {@code this}
     * @return if the object equal one and other
     */
    @Override
    public boolean equals(Object obj) {
        // we have to type check, that's why a 1 line return isn't possible
        if (super.equals(obj) && obj instanceof Transfer transfer) {
            return  this.sender.equals(transfer.sender) &&
                    this.recipient.equals(transfer.recipient);
        }
        return false;
    }

    /**
     * Validates the Transfer
     *
     * @return true if the all fields are set.
     */
    @Override
    public boolean validate() {
        return super.validate() && sender != null && recipient != null && !sender.equals(recipient) && amount > 0;
    }
}