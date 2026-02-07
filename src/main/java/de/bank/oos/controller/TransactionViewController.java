package de.bank.oos.controller;

import de.bank.oos.bank.PrivateBank;
import de.bank.oos.bank.Transaction;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import javax.xml.transform.Result;
import java.io.IOException;
import java.util.Optional;

public class TransactionViewController {
    @FXML public Label IDLabel;
    @FXML public Label balanceLabel;
    @FXML public ListView<Transaction> transactionList;

    private PrivateBank bank;
    private String account;
    private Stage stage;

    @FXML
    public void backToOverview() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/accountsView.fxml"));
        stage.setScene(new Scene(loader.load()));

        ((AccountsViewController) loader.getController()).init(stage, bank);

        stage.setTitle(bank.getName());
    }

    @FXML
    public void createTransaction() {
        try {
            Dialog<Result> dialog = new Dialog<>();
            dialog.setTitle("Add Transaction");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/newTransactionView.fxml"));
            dialog.getDialogPane().setContent(loader.load());
            AddTransactionController controller = loader.getController();

            ButtonType transferButton = new ButtonType("Transfer", ButtonBar.ButtonData.RIGHT);
            ButtonType paymentButton = new ButtonType("Payment", ButtonBar.ButtonData.RIGHT);
            dialog.getDialogPane().getButtonTypes().setAll(transferButton, paymentButton);
            Optional<Result> result = dialog.showAndWait();

            if (result.toString().equals("Optional[ButtonType [text=Transfer, buttonData=RIGHT]]")) {
                bank.addTransaction(account, controller.getTransfer(account));
            } else if (result.toString().equals("Optional[ButtonType [text=Payment, buttonData=RIGHT]]")) {
                bank.addTransaction(account, controller.getPayment());
            }

        } catch (Exception e) {
            errorPopUp(e);
        }
    }

    @FXML
    public void ascending() { transactionList.getItems().setAll(bank.getTransactionsSorted(account, true)); }

    @FXML
    public void descending() { transactionList.getItems().setAll(bank.getTransactionsSorted(account, false)); }

    @FXML
    public void positiv() { transactionList.getItems().setAll(bank.getTransactionsByType(account, true)); }

    @FXML
    public void negativ() { transactionList.getItems().setAll(bank.getTransactionsByType(account, false)); }

    @FXML
    public void deleteTransaction() {
        Transaction transaction = (transactionList.getFocusModel().getFocusedItem());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete this Transaction?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            try {
                bank.removeTransaction(account, transaction);
            } catch (Exception e) {
                errorPopUp(e);
            }
        }
    }

    public void init(Stage stage, PrivateBank bank, String account) {
        this.stage = stage;
        this.account = account;
        this.bank = bank;
        IDLabel.setText(account);
        balanceLabel.setText(String.valueOf(bank.getAccountBalance(account)));
    }

    private void errorPopUp(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
        alert.showAndWait();
    }

    public void refresh() {
        transactionList.getItems().clear();
        transactionList.getItems().addAll(bank.getTransactions(account));
        transactionList.refresh();
        balanceLabel.setText(String.valueOf(bank.getAccountBalance(account)));
        System.out.println(bank.getTransactions(account));
    }
}
