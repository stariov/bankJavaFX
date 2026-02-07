package de.bank.oos.bank.exceptions;

public class TransactionAttributeException extends BankException {
    public TransactionAttributeException(String fehler) {
        super(fehler);
    }
}
