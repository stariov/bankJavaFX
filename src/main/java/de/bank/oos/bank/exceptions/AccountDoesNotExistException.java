package de.bank.oos.bank.exceptions;

public class AccountDoesNotExistException extends BankException {
    public AccountDoesNotExistException(String fehler) {
        super(fehler);
    }
}
