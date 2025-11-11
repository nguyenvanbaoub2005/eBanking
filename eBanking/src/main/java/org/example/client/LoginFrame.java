package org.example.client;

import org.example.rmi.BankService;

import javax.swing.*;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Giao diện đăng nhập với thiết kế đẹp mắt
 */
public class LoginFrame extends JFrame {
    
    private JTextField accountField;
    private BankService bankService;
    
    public LoginFrame() {
        setTitle("eBanking System - Đăng nhập");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Kết nối đến RMI Server
        connectToServer();
        
        // Tạo giao diện
        createUI();
    }
    
    private void connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            bankService = (BankService) registry.lookup("BankService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không thể kết nối đến server!\nVui lòng chạy server trước.",
                    "Lỗi kết nối",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void createUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        
        JLabel titleLabel = new JLabel("eBanking System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Hệ thống ngân hàng điện tử");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(200, 230, 255));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(subtitleLabel);
        
        // Center Panel - Login Form
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBackground(new Color(240, 248, 255));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Icon
        JLabel iconLabel = new JLabel("🏦", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(iconLabel, gbc);
        
        // Account Number Label
        JLabel accountLabel = new JLabel("Số tài khoản:");
        accountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        accountLabel.setForeground(new Color(33, 33, 33));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(accountLabel, gbc);
        
        // Account Number Field
        accountField = new JTextField(20);
        accountField.setFont(new Font("Arial", Font.PLAIN, 16));
        accountField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        centerPanel.add(accountField, gbc);
        
        // Info Label
        JLabel infoLabel = new JLabel("<html><center>Tài khoản mẫu: 1001, 1002, 1003, 1004, 1005</center></html>");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoLabel.setForeground(new Color(100, 100, 100));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        centerPanel.add(infoLabel, gbc);
        
        // Login Button
        JButton loginButton = new JButton("Đăng nhập");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(25, 118, 210));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(21, 101, 192));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(25, 118, 210));
            }
        });
        
        loginButton.addActionListener(e -> handleLogin());
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        centerPanel.add(loginButton, gbc);
        
        // Exit Button
        JButton exitButton = new JButton("Thoát");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exitButton.setBackground(new Color(220, 220, 220));
        exitButton.setForeground(new Color(66, 66, 66));
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        exitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exitButton.setBackground(new Color(200, 200, 200));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exitButton.setBackground(new Color(220, 220, 220));
            }
        });
        
        exitButton.addActionListener(e -> System.exit(0));
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        centerPanel.add(exitButton, gbc);
        
        // Enter key listener
        accountField.addActionListener(e -> handleLogin());
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private void handleLogin() {
        String accountNumber = accountField.getText().trim();
        
        if (accountNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập số tài khoản!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            boolean success = bankService.login(accountNumber);
            
            if (success) {
                // Đăng nhập thành công - mở cửa sổ chính
                dispose();
                SwingUtilities.invokeLater(() -> {
                    BankingFrame bankingFrame = new BankingFrame(accountNumber, bankService);
                    bankingFrame.setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(this,
                        "Số tài khoản không tồn tại!\nVui lòng kiểm tra lại.",
                        "Đăng nhập thất bại",
                        JOptionPane.ERROR_MESSAGE);
                accountField.setText("");
                accountField.requestFocus();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi kết nối server: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
