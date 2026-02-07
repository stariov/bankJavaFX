package de.bank.oos.bank.exceptions;

public class TransactionDoesNotExistException extends BankException {
    public TransactionDoesNotExistException(String fehler) {
        super(fehler);
    }
}
