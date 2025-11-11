package org.example.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface callback để server thông báo cho client
 */
public interface ClientCallback extends Remote {
    
    /**
     * Nhận thông báo từ server
     * @param message Nội dung thông báo
     */
    void notifyClient(String message) throws RemoteException;
}
