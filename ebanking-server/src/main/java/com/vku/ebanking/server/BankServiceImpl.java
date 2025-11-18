package com.vku.ebanking.server;

import com.vku.ebanking.shared.Account;
import com.vku.ebanking.shared.BankService;
import com.vku.ebanking.shared.ClientCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankServiceImpl extends UnicastRemoteObject implements BankService {
    private List<Account> accounts;
    private XMLAccountHandler xmlHandler;
    private Map<String, ClientCallback> callbacks;
    private ServerController controller;

    public BankServiceImpl(ServerController controller) throws RemoteException {
        super();
        this.controller = controller;
        this.xmlHandler = new XMLAccountHandler();
        this.accounts = xmlHandler.loadAccounts();
        this.callbacks = new HashMap<>();
    }

    @Override
    public synchronized Account login(String accountNumber) throws RemoteException {
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                controller.log("Đăng nhập: " + accountNumber + " - " + account.getAccountName());
                return account;
            }
        }
        controller.log("Đăng nhập thất bại: " + accountNumber);
        return null;
    }

    @Override
    public synchronized boolean deposit(String accountNumber, double amount) throws RemoteException {
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                account.setBalance(account.getBalance() + amount);
                xmlHandler.saveAccounts(accounts);
                String message = String.format("Nạp tiền: +%.0f₫", amount);
                controller.log(accountNumber + " - " + message);
                notifyClient(accountNumber, message);
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean withdraw(String accountNumber, double amount) throws RemoteException {
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                if (account.getBalance() >= amount) {
                    account.setBalance(account.getBalance() - amount);
                    xmlHandler.saveAccounts(accounts);
                    String message = String.format("Rút tiền: -%.0f₫", amount);
                    controller.log(accountNumber + " - " + message);
                    notifyClient(accountNumber, message);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean transfer(String fromAccount, String toAccount, double amount)
            throws RemoteException {
        Account from = null;
        Account to = null;

        for (Account account : accounts) {
            if (account.getAccountNumber().equals(fromAccount)) {
                from = account;
            }
            if (account.getAccountNumber().equals(toAccount)) {
                to = account;
            }
        }

        if (from != null && to != null && from.getBalance() >= amount) {
            from.setBalance(from.getBalance() - amount);
            to.setBalance(to.getBalance() + amount);
            xmlHandler.saveAccounts(accounts);

            String msgFrom = String.format("Chuyển tiền đến %s: -%.0f₫", toAccount, amount);
            String msgTo = String.format("Nhận tiền từ %s: +%.0f₫", fromAccount, amount);

            controller.log(fromAccount + " -> " + toAccount + ": " + amount + "₫");
            notifyClient(fromAccount, msgFrom);
            notifyClient(toAccount, msgTo);
            return true;
        }
        return false;
    }

    @Override
    public void registerCallback(String accountNumber, ClientCallback callback)
            throws RemoteException {
        callbacks.put(accountNumber, callback);
        controller.log("Callback đã đăng ký: " + accountNumber);
    }

    @Override
    public void unregisterCallback(String accountNumber) throws RemoteException {
        callbacks.remove(accountNumber);
        controller.log("Callback đã hủy: " + accountNumber);
    }

    private void notifyClient(String accountNumber, String message) {
        ClientCallback callback = callbacks.get(accountNumber);
        if (callback != null) {
            try {
                callback.notifyTransaction(message);
            } catch (RemoteException e) {
                callbacks.remove(accountNumber);
                controller.log("Callback lỗi cho: " + accountNumber);
            }
        }
    }

    public List<Account> getAllAccounts() {
        return accounts;
    }
}