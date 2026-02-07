package de.bank.oos.controller;

import de.bank.oos.bank.Payment;
import de.bank.oos.bank.Transaction;
import de.bank.oos.bank.Transfer;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AddTransactionController {

    public String getRecipientID() {
        return recipientID.getText();
    }

    public String getDescription() {
        return description.getText();
    }

    public String getDate() {
        return date.getText();
    }

    public String getAmount() {
        return amount.getText();
    }

    @FXML
    private TextField amount;
    @FXML
    private TextField recipientID;
    @FXML
    private TextField description;
    @FXML
    private TextField date;

    public Transaction getTransfer(String account) {
        return new Transfer(getDate(), Double.parseDouble(getAmount()), getDescription(), account, getRecipientID());
    }
    public Transaction getPayment() {
        return new Payment(getDate(), Double.parseDouble(getAmount()), getDescription(), 0,0);
    }
}
