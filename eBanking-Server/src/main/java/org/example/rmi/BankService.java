package org.example.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface RMI cho dịch vụ ngân hàng
 */
public interface BankService extends Remote {
    
    /**
     * Đăng nhập với số tài khoản
     * @param accountNumber Số tài khoản
     * @return true nếu tài khoản tồn tại, false nếu không
     */
    boolean login(String accountNumber) throws RemoteException;
    
    /**
     * Lấy số dư tài khoản
     * @param accountNumber Số tài khoản
     * @return Số dư hiện tại
     */
    double getBalance(String accountNumber) throws RemoteException;
    
    /**
     * Lấy tên chủ tài khoản
     * @param accountNumber Số tài khoản
     * @return Tên chủ tài khoản
     */
    String getAccountOwner(String accountNumber) throws RemoteException;
    
    /**
     * Nạp tiền vào tài khoản
     * @param accountNumber Số tài khoản
     * @param amount Số tiền nạp
     * @return true nếu thành công, false nếu thất bại
     */
    boolean deposit(String accountNumber, double amount) throws RemoteException;
    
    /**
     * Rút tiền từ tài khoản
     * @param accountNumber Số tài khoản
     * @param amount Số tiền rút
     * @return true nếu thành công, false nếu thất bại (không đủ tiền)
     */
    boolean withdraw(String accountNumber, double amount) throws RemoteException;
    
    /**
     * Chuyển tiền giữa các tài khoản
     * @param fromAccount Tài khoản nguồn
     * @param toAccount Tài khoản đích
     * @param amount Số tiền chuyển
     * @return true nếu thành công, false nếu thất bại
     */
    boolean transfer(String fromAccount, String toAccount, double amount) throws RemoteException;
    
    /**
     * Đăng ký callback để nhận thông báo
     * @param accountNumber Số tài khoản
     * @param callback Interface callback
     */
    void registerCallback(String accountNumber, ClientCallback callback) throws RemoteException;
    
    /**
     * Hủy đăng ký callback
     * @param accountNumber Số tài khoản
     */
    void unregisterCallback(String accountNumber) throws RemoteException;
}
