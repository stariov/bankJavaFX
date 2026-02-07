package de.bank.oos.bank;

public abstract class Transaction implements CalculateBill {
    String date; // DD.MM.YYYY
    double amount; // the value of the transaction
    String description;

    /**
     * Gets the value of the transaction.
     * @return value of transaction
     */
    public double getAmount() { return amount; }

    /**
     * Set the value of the transaction.
     * @param amount is the value of the transaction
     */
    public abstract void setAmount(double amount);

    /**
     * Get the description of the transaction.
     * @return The description of the transaction
     */
    public String getDescription() { return description; }

    /**
     * Used to set the description of the Transaction.
     * @param description Will be the description of the transaction
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Returns the date when this transaction occurred.
     * @return the date of the transaction
     */
    public String getDate() { return date; }

    /**
     * Sets the date of the transaction.
     *
     * @param date the date at which the transaction will occur
     */
    public void setDate(String date) { this.date = date; }

    /**
     * The constructor of the abstract class {@link Transaction}.
     * Reduces redundant code in constructors of successor classes.
     *
     * @param date the date at which the transaction will occur
     * @param amount how much money will be transferred
     * @param description a description of the transaction
     */
    public Transaction(String date, double amount, String description) {
        setDate(date);
        setAmount(amount);
        setDescription(description);
    }

    /**
     * The copy constructor of the abstract class {@link Transaction}.
     * Reduces redundant code in copy constructors in successor classes.
     *
     * @param transaction the transaction which will be deep copied
     */
    public Transaction(Transaction transaction) {
        setDate(transaction.date);
        setAmount(transaction.amount);
        setDescription(transaction.description);
    }

    /**
     * Prints the names and values of the attributes on the console.
     */
    public void printObject() {
        System.out.println( "Date: "        + date      + "\n" +
                            "Amount: "      + amount    + "\n" +
                            "Description: " + description
        );
    }

    /**
     * Turns the names and values of all attributes into a string.
     *
     * @return all information about the {@link Transaction}
     */
    @Override
    public String toString() {
        return  "Date: "        + date             + "\n" +
                "Amount: "      + this.calculate() + "\n" +
                "Description: " + description      + "\n";
    }

    /**
     * Compares {@code obj's} values of its attributes with those of {@code this}.
     * No Pointer comparison.
     *
     * @param obj object which will be compared with {@code this}
     * @return returns true if the values are the same
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Transaction)) return false; // type check
        Transaction transaction = (Transaction) obj;
        return  this.date.equals(transaction.date) &&
                this.amount == transaction.amount  &&
                this.description.equals(transaction.description);
    }

    public boolean validate() {
        // warns that the method is often used like if(!validate) { ... }
        return date != null && description != null && amount != 0;
    }
}
