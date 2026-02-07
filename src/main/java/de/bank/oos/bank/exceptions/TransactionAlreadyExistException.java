package de.bank.oos.bank.exceptions;

public class TransactionAlreadyExistException extends BankException {
    public TransactionAlreadyExistException(String fehler) {
        super(fehler);
    }
}
