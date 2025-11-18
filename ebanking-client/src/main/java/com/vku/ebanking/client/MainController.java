package com.vku.ebanking.client;

import com.vku.ebanking.shared.Account;
import com.vku.ebanking.shared.BankService;
import com.vku.ebanking.shared.ClientCallback;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.NumberFormat;
import java.util.Locale;

public class MainController {
    @FXML private Label accountNameLabel;
    @FXML private Label accountNumberLabel;
    @FXML private Label balanceLabel;
    @FXML private TextField amountField;
    @FXML private TextField recipientField;
    @FXML private TextArea notificationArea;
    @FXML private Button depositButton;
    @FXML private Button withdrawButton;
    @FXML private Button transferButton;

    private Account account;
    private BankService bankService;
    private ClientCallbackImpl callback;
    private NumberFormat currencyFormat;

    @FXML
    public void initialize() {
        currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        notificationArea.setEditable(false);
    }

    public void setAccount(Account account) {
        this.account = account;
        updateUI();
    }

    public void setBankService(BankService bankService) {
        this.bankService = bankService;
        registerCallback();
    }

    private void updateUI() {
        accountNameLabel.setText(account.getAccountName());
        accountNumberLabel.setText("STK: " + account.getAccountNumber());
        updateBalance(account.getBalance());
    }

    private void updateBalance(double balance) {
        balanceLabel.setText(currencyFormat.format(balance) + " ₫");
        animateBalance();
    }

    private void animateBalance() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), balanceLabel);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.1);
        st.setToY(1.1);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    private void registerCallback() {
        try {
            callback = new ClientCallbackImpl();
            bankService.registerCallback(account.getAccountNumber(), callback);
        } catch (RemoteException e) {
            showNotification("❌ Lỗi đăng ký callback");
        }
    }

    @FXML
    private void deposit() {
        processTransaction(() -> {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                showNotification("❌ Số tiền không hợp lệ!");
                return;
            }
            if (bankService.deposit(account.getAccountNumber(), amount)) {
                account.setBalance(account.getBalance() + amount);
                updateBalance(account.getBalance());
                showNotification("✅ Nạp tiền thành công: +" +
                        currencyFormat.format(amount) + "₫");
                amountField.clear();
            }
        });
    }

    @FXML
    private void withdraw() {
        processTransaction(() -> {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                showNotification("❌ Số tiền không hợp lệ!");
                return;
            }
            if (amount > account.getBalance()) {
                showNotification("❌ Số dư không đủ!");
                return;
            }
            if (bankService.withdraw(account.getAccountNumber(), amount)) {
                account.setBalance(account.getBalance() - amount);
                updateBalance(account.getBalance());
                showNotification("✅ Rút tiền thành công: -" +
                        currencyFormat.format(amount) + "₫");
                amountField.clear();
            }
        });
    }

    @FXML
    private void transfer() {
        processTransaction(() -> {
            String recipient = recipientField.getText().trim();
            double amount = Double.parseDouble(amountField.getText());

            if (recipient.isEmpty()) {
                showNotification("❌ Vui lòng nhập số tài khoản người nhận!");
                return;
            }
            if (amount <= 0) {
                showNotification("❌ Số tiền không hợp lệ!");
                return;
            }
            if (amount > account.getBalance()) {
                showNotification("❌ Số dư không đủ!");
                return;
            }
            if (recipient.equals(account.getAccountNumber())) {
                showNotification("❌ Không thể chuyển cho chính mình!");
                return;
            }

            if (bankService.transfer(account.getAccountNumber(), recipient, amount)) {
                account.setBalance(account.getBalance() - amount);
                updateBalance(account.getBalance());
                showNotification("✅ Chuyển tiền thành công đến " + recipient +
                        ": -" + currencyFormat.format(amount) + "₫");
                amountField.clear();
                recipientField.clear();
            } else {
                showNotification("❌ Chuyển tiền thất bại!");
            }
        });
    }

    private void processTransaction(TransactionTask task) {
        try {
            task.execute();
        } catch (NumberFormatException e) {
            showNotification("❌ Số tiền không hợp lệ!");
        } catch (Exception e) {
            showNotification("❌ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showNotification(String message) {
        Platform.runLater(() -> {
            notificationArea.appendText(message + "\n");
            notificationArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    @FXML
    private void logout() {
        try {
            if (callback != null) {
                bankService.unregisterCallback(account.getAccountNumber());
                UnicastRemoteObject.unexportObject(callback, true);
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) balanceLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            showNotification("❌ Lỗi đăng xuất!");
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    interface TransactionTask {
        void execute() throws Exception;
    }

    class ClientCallbackImpl extends UnicastRemoteObject implements ClientCallback {
        protected ClientCallbackImpl() throws RemoteException {
            super();
        }

        @Override
        public void notifyTransaction(String message) throws RemoteException {
            showNotification("🔔 " + message);
        }
    }
}