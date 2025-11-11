package org.example.server;

import org.example.rmi.BankService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Main class để khởi chạy RMI Server
 */
public class ServerMain {
    
    private static final int PORT = 1099;
    private static final String SERVICE_NAME = "BankService";
    
    public static void main(String[] args) {
        try {
            // Tạo RMI registry
            Registry registry = LocateRegistry.createRegistry(PORT);
            
            // Tạo và đăng ký service
            BankService bankService = new BankServer();
            registry.rebind(SERVICE_NAME, bankService);
            
            System.out.println("===========================================");
            System.out.println("    RMI BANK SERVER STARTED");
            System.out.println("===========================================");
            System.out.println("Port: " + PORT);
            System.out.println("Service Name: " + SERVICE_NAME);
            System.out.println("Server is ready to accept connections...");
            System.out.println("===========================================");
            
        } catch (Exception e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
