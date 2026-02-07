package de.bank.oos.bank;

public class Payment extends Transaction{
    private double incomingInterest; // interests 0 - 1
    private double outgoingInterest; // 0 - 1

    /**
     * Sets the amount of money which will be transferred.
     *
     * @param amount is the value of the transaction
     */
    @Override
    public void setAmount(double amount) { this.amount = amount; }

    /**
     * Sets the incoming interests of the {@link Payment}.
     *
     * @param p the percentage of incoming interests as decimal
     */
    public void setIncomingInterest(double p) {
        if (p >= 0 && p <= 1) {
            incomingInterest = p;
        }
        else {
            System.out.println("Interest must be between 0 and 1");
        }
    }

    /**
     * Gets the percentage of the incoming interests as a decimal.
     *
     * @return percentage of incoming interests as decimal
     */
    public double getIncomingInterest() { return incomingInterest; }

    /**
     * Sets the outgoing interests of the {@link Payment}.
     *
     * @param p percentage of outgoing interests represented as decimal
     */
    public void setOutgoingInterest(double p) {
        if ((p >= 0) && (p <= 1)) {
            this.outgoingInterest = p;
        }
        else {
            System.out.println("Interest must be between 0 and 1");
        }
    }

    /**
     * Gets the percentage of outgoing interests, represented as decimal.
     *
     * @return percentage of outgoing interests, represented as decimal
     */
    public double getOutgoingInterest() { return outgoingInterest; }

    /**
     * Constructs a {@link Payment} object.
     *
     * @param date the date of when the Payment will occur
     * @param amount the amount of money which will be sent
     * @param description a description of the Payment
     */
    public Payment(String date, double amount, String description) {
        super(date, amount, description);
    }

    /**
     * Constructs a {@link Payment} object.
     *
     * @param date the date of when the Payment will occur
     * @param amount the amount of money which will be sent
     * @param description a description of the Payment
     * @param incomingInterest percentage of the incoming interests as decimal
     * @param outgoingInterest percentage of the outgoing interests as decimal
     */
    public Payment(String date, double amount, String description, double incomingInterest, double outgoingInterest) {
        super(date, amount, description);
        setIncomingInterest(incomingInterest);
        setOutgoingInterest(outgoingInterest);
    }

    /**
     * Constructs a deep copy of a {@link Payment} object.
     *
     * @param payment Payment object which will be copied
     */
    public Payment(Payment payment) {
        super(payment.date, payment.amount, payment.description);
        setIncomingInterest(payment.incomingInterest);
        setOutgoingInterest(payment.outgoingInterest);
    }

    /**
     * Prints the name and value of every attribute of an {@link Payment} object.
     */
    @Override
    public void printObject() {
        System.out.println("PAYMENT --------------------");
        super.printObject();
        System.out.print("IncomingInterest: " + incomingInterest + "\n" +
                         "OutgoingInterest: " + outgoingInterest + "\n"
        );
    }

    /**
     * Calculates the interests.
     *
     * @return returns the amount altered by the interests
     */
    @Override
    public double calculate() {
        if (amount < 0) {
            return amount + amount * outgoingInterest;
        } else {
            return amount - amount * incomingInterest;
        }
    }

    /**
     * Turns the name and value of every attribute of an {@link Payment} object into a String.
     *
     * @return all information about the {@link Payment}
     */
    @Override
    public String toString() {
        return super.toString() +
                "IncomingInterest: " + incomingInterest + "\n" +
                "OutgoingInterest: " + outgoingInterest + "\n";
    }

    /**
     * Compares {@code this's} attribute's values with those of the obj.
     * No pointer comparison.
     *
     * @param obj object which will be compared with {@code this}
     * @return true, if the objects contain the same information
     */
    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) && obj instanceof Payment payment) {
            return  this.incomingInterest == payment.incomingInterest &&
                    this.outgoingInterest == payment.outgoingInterest;
        }
        return false;
    }

    /**
     * Checks if the Payment is correctly set up .
     *
     * @return true, if all attributes are correctly set
     */
    @Override
    public boolean validate() {
        return super.validate() && (incomingInterest <= 1 && incomingInterest >= 0) && (outgoingInterest <= 1 && outgoingInterest >= 0);
    }
}