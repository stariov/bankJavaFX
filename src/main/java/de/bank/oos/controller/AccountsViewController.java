package de.bank.oos.controller;

import de.bank.oos.bank.PrivateBank;
import de.bank.oos.bank.exceptions.AccountAlreadyExistsException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;

public class AccountsViewController {
    @FXML public TextField accountIDField;
    @FXML public ListView<String> accountList;

    private PrivateBank bank;
    private Stage stage;

    @FXML
    public void refresh() {
        accountList.getItems().setAll(bank.getAllAccounts());
        System.out.println(bank.getAllAccounts());
    }

    @FXML
    public void addAccount() {
        try {
            if (accountIDField.getText().isEmpty())
                throw new AccountAlreadyExistsException("Invalid Account ID!");
            bank.createAccount(accountIDField.getText());
        } catch (Exception e) {
            errorPopUp(e);
        }
    }

    @FXML
    public void selectAccount(Event event) {
        String selectedAccount = (accountList.getFocusModel().getFocusedItem());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/transactionView.fxml"));
            stage.setTitle(selectedAccount);
            stage.setScene(new Scene(loader.load()));
            ((TransactionViewController) loader.getController()).init(stage, bank, selectedAccount);
        } catch (Exception e) {
            errorPopUp(e);
        }
    }

    @FXML
    public void deleteAccount() {
        String selectedAccount = (accountList.getFocusModel().getFocusedItem());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete this Account?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            try {
                bank.deleteAccount(selectedAccount);
            } catch (Exception e) {
                errorPopUp(e);
            }
        }
    }

    private void errorPopUp(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
        alert.showAndWait();
    }

    public void init(Stage stage, PrivateBank bank) {
        this.stage = stage;
        this.bank = bank;
    }
}
