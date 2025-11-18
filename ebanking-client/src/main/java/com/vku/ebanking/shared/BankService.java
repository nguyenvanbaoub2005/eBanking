package com.vku.ebanking.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BankService extends Remote {
    Account login(String accountNumber) throws RemoteException;
    boolean deposit(String accountNumber, double amount) throws RemoteException;
    boolean withdraw(String accountNumber, double amount) throws RemoteException;
    boolean transfer(String fromAccount, String toAccount, double amount) throws RemoteException;
    void registerCallback(String accountNumber, ClientCallback callback) throws RemoteException;
    void unregisterCallback(String accountNumber) throws RemoteException;
}
