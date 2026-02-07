package de.bank.oos.bank.exceptions;

public class AccountAlreadyExistsException extends BankException {
    public AccountAlreadyExistsException(String fehler) {
        super(fehler);
    }
}
