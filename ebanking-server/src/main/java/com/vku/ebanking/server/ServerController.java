package com.vku.ebanking.server;

import com.vku.ebanking.shared.Account;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerController {
    @FXML private Label statusLabel;
    @FXML private TextArea logArea;
    @FXML private Button startButton;
    @FXML private Button stopButton;
    @FXML private TableView<Account> accountTable;
    @FXML private TableColumn<Account, String> colAccountNumber;
    @FXML private TableColumn<Account, String> colAccountName;
    @FXML private TableColumn<Account, Double> colBalance;

    private BankServiceImpl bankService;
    private Registry registry;
    private boolean isRunning = false;

    @FXML
    public void initialize() {
        colAccountNumber.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        colAccountName.setCellValueFactory(new PropertyValueFactory<>("accountName"));
        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));

        stopButton.setDisable(true);
        log("Server sẵn sàng khởi động...");
    }

    @FXML
    private void startServer() {
        try {
            bankService = new BankServiceImpl(this);
            registry = LocateRegistry.createRegistry(1099);
            registry.rebind("BankService", bankService);

            isRunning = true;
            startButton.setDisable(true);
            stopButton.setDisable(false);
            statusLabel.setText("🟢 Server đang chạy");
            statusLabel.setStyle("-fx-text-fill: #10b981;");
            log("✓ Server đã khởi động thành công trên cổng 1099");

            loadAccounts();
        } catch (Exception e) {
            log("✗ Lỗi khởi động server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void stopServer() {
        try {
            if (registry != null) {
                registry.unbind("BankService");
            }
            isRunning = false;
            startButton.setDisable(false);
            stopButton.setDisable(true);
            statusLabel.setText("🔴 Server đã dừng");
            statusLabel.setStyle("-fx-text-fill: #ef4444;");
            log("✓ Server đã dừng");
        } catch (Exception e) {
            log("✗ Lỗi dừng server: " + e.getMessage());
        }
    }

    @FXML
    private void refreshAccounts() {
        loadAccounts();
        log("Danh sách tài khoản đã được làm mới");
    }

    private void loadAccounts() {
        if (bankService != null) {
            ObservableList<Account> accounts =
                    FXCollections.observableArrayList(bankService.getAllAccounts());
            accountTable.setItems(accounts);
        }
    }

    public void log(String message) {
        Platform.runLater(() -> {
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            logArea.appendText("[" + timestamp + "] " + message + "\n");
        });
    }
}