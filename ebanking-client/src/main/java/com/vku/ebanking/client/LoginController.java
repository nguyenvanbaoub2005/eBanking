package com.vku.ebanking.client;

import com.vku.ebanking.shared.Account;
import com.vku.ebanking.shared.BankService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LoginController {
    @FXML private TextField accountField;
    @FXML private PasswordField pinField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    @FXML private ProgressIndicator progressIndicator;

    private BankService bankService;

    @FXML
    public void initialize() {
        progressIndicator.setVisible(false);
        errorLabel.setVisible(false);

        // Enter key support
        pinField.setOnAction(e -> login());
    }

    @FXML
    private void login() {
        String accountNumber = accountField.getText().trim();
        String pin = pinField.getText().trim();

        if (accountNumber.isEmpty() || pin.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        progressIndicator.setVisible(true);
        loginButton.setDisable(true);
        errorLabel.setVisible(false);

        new Thread(() -> {
            try {
                if (bankService == null) {
                    Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                    bankService = (BankService) registry.lookup("BankService");
                }

                Account account = bankService.login(accountNumber);

                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    loginButton.setDisable(false);

                    if (account != null && account.getPin().equals(pin)) {
                        openMainScreen(account);
                    } else {
                        showError("Sai số tài khoản hoặc mã PIN!");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    loginButton.setDisable(false);
                    showError("Không thể kết nối đến server!\n" + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void openMainScreen(Account account) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Scene scene = new Scene(loader.load());

            MainController controller = loader.getController();
            controller.setAccount(account);
            controller.setBankService(bankService);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            showError("Lỗi mở màn hình chính!");
            e.printStackTrace();
        }
    }

    @FXML
    private void clearFields() {
        accountField.clear();
        pinField.clear();
        errorLabel.setVisible(false);
    }
}