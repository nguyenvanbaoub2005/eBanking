package com.vku.ebanking.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallback extends Remote {
    void notifyTransaction(String message) throws RemoteException;
}