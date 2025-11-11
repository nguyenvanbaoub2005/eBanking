package org.example.server;

import org.example.model.Account;
import org.example.rmi.BankService;
import org.example.rmi.ClientCallback;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class BankServer extends UnicastRemoteObject implements BankService {
    
    private static final String ACCOUNTS_FILE = "accounts.txt";
    private Map<String, Account> accounts;
    private Map<String, ClientCallback> callbacks;
    
    public BankServer() throws RemoteException {
        super();
        accounts = new HashMap<>();
        callbacks = new HashMap<>();
        loadAccounts();
    }
    

    private void loadAccounts() {
        File file = new File(ACCOUNTS_FILE);
        if (!file.exists()) {
            System.out.println("File accounts.txt không tồn tại. Tạo dữ liệu mặc định...");
            createDefaultAccounts();
            saveAccounts();
            return;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String accountNumber = parts[0].trim();
                    String owner = parts[1].trim();
                    double balance = Double.parseDouble(parts[2].trim());
                    accounts.put(accountNumber, new Account(accountNumber, owner, balance));
                }
            }
            System.out.println("Đã tải " + accounts.size() + " tài khoản từ file.");
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file: " + e.getMessage());
            createDefaultAccounts();
        }
    }
    

    private synchronized void saveAccounts() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ACCOUNTS_FILE))) {
            for (Account account : accounts.values()) {
                pw.println(account.toString());
            }
            System.out.println("Đã lưu dữ liệu tài khoản vào file.");
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi file: " + e.getMessage());
        }
    }
    

    private void createDefaultAccounts() {
        accounts.put("1001", new Account("1001", "Nguyễn Văn A", 5000000));
        accounts.put("1002", new Account("1002", "Trần Thị B", 3000000));
        accounts.put("1003", new Account("1003", "Lê Văn C", 10000000));
        accounts.put("1004", new Account("1004", "Phạm Thị D", 2000000));
        accounts.put("1005", new Account("1005", "Hoàng Văn E", 7500000));
    }
    
    @Override
    public synchronized boolean login(String accountNumber) throws RemoteException {
        boolean exists = accounts.containsKey(accountNumber);
        System.out.println("Login attempt: " + accountNumber + " - " + (exists ? "Success" : "Failed"));
        return exists;
    }
    
    @Override
    public synchronized double getBalance(String accountNumber) throws RemoteException {
        Account account = accounts.get(accountNumber);
        if (account != null) {
            System.out.println("Get balance: " + accountNumber + " - " + account.getBalance());
            return account.getBalance();
        }
        return -1;
    }
    
    @Override
    public synchronized String getAccountOwner(String accountNumber) throws RemoteException {
        Account account = accounts.get(accountNumber);
        if (account != null) {
            return account.getOwner();
        }
        return null;
    }
    
    @Override
    public synchronized boolean deposit(String accountNumber, double amount) throws RemoteException {
        if (amount <= 0) {
            return false;
        }
        
        Account account = accounts.get(accountNumber);
        if (account != null) {
            account.deposit(amount);
            saveAccounts();
            System.out.println("Deposit: " + accountNumber + " - +" + amount + " - New balance: " + account.getBalance());
            return true;
        }
        return false;
    }
    
    @Override
    public synchronized boolean withdraw(String accountNumber, double amount) throws RemoteException {
        if (amount <= 0) {
            return false;
        }
        
        Account account = accounts.get(accountNumber);
        if (account != null && account.withdraw(amount)) {
            saveAccounts();
            System.out.println("Withdraw: " + accountNumber + " - -" + amount + " - New balance: " + account.getBalance());
            return true;
        }
        return false;
    }
    
    @Override
    public synchronized boolean transfer(String fromAccount, String toAccount, double amount) throws RemoteException {
        if (amount <= 0 || fromAccount.equals(toAccount)) {
            return false;
        }
        
        Account from = accounts.get(fromAccount);
        Account to = accounts.get(toAccount);
        
        if (from != null && to != null && from.withdraw(amount)) {
            to.deposit(amount);
            saveAccounts();
            System.out.println("Transfer: " + fromAccount + " -> " + toAccount + " - " + amount);
            
            // Gửi thông báo cho client nhận tiền (nếu có đăng ký callback)
            ClientCallback callback = callbacks.get(toAccount);
            if (callback != null) {
                try {
                    String message = String.format("Bạn vừa nhận được %.0f VNĐ từ tài khoản %s", amount, fromAccount);
                    callback.notifyClient(message);
                } catch (RemoteException e) {
                    System.err.println("Không thể gửi thông báo cho client: " + e.getMessage());
                    callbacks.remove(toAccount);
                }
            }
            
            return true;
        }
        return false;
    }
    
    @Override
    public synchronized void registerCallback(String accountNumber, ClientCallback callback) throws RemoteException {
        callbacks.put(accountNumber, callback);
        System.out.println("Callback registered for account: " + accountNumber);
    }
    
    @Override
    public synchronized void unregisterCallback(String accountNumber) throws RemoteException {
        callbacks.remove(accountNumber);
        System.out.println("Callback unregistered for account: " + accountNumber);
    }
}
