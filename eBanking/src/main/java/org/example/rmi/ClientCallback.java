package org.example.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallback extends Remote {
    //Nhận thông báo từ server
    void notifyClient(String message) throws RemoteException;
}
