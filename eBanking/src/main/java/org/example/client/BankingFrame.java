package org.example.client;

import org.example.rmi.BankService;
import org.example.rmi.ClientCallback;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Giao diện chính của ứng dụng Banking với các chức năng đầy đủ
 */
public class BankingFrame extends JFrame {
    
    private String accountNumber;
    private BankService bankService;
    private JLabel balanceLabel;
    private JLabel accountInfoLabel;
    private NumberFormat currencyFormat;
    private ClientCallbackImpl callback;
    
    public BankingFrame(String accountNumber, BankService bankService) {
        this.accountNumber = accountNumber;
        this.bankService = bankService;
        this.currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        
        setTitle("eBanking System - Tài khoản: " + accountNumber);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Đăng ký callback để nhận thông báo
        registerCallback();
        
        createUI();
        refreshBalance();
    }
    
    private void registerCallback() {
        try {
            callback = new ClientCallbackImpl(this);
            bankService.registerCallback(accountNumber, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        
        // Header Panel
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Center Panel - Các nút chức năng
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        
        // Footer Panel
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Left side - Account info
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(25, 118, 210));
        
        try {
            String owner = bankService.getAccountOwner(accountNumber);
            accountInfoLabel = new JLabel("Chào mừng, " + owner);
            accountInfoLabel.setFont(new Font("Arial", Font.BOLD, 20));
            accountInfoLabel.setForeground(Color.WHITE);
            
            JLabel accountLabel = new JLabel("Số tài khoản: " + accountNumber);
            accountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            accountLabel.setForeground(new Color(200, 230, 255));
            
            leftPanel.add(accountInfoLabel);
            leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            leftPanel.add(accountLabel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Right side - Balance
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(25, 118, 210));
        rightPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        JLabel balanceTitle = new JLabel("Số dư hiện tại");
        balanceTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        balanceTitle.setForeground(new Color(200, 230, 255));
        balanceTitle.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        balanceLabel = new JLabel("0 VNĐ");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 24));
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        rightPanel.add(balanceTitle);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(balanceLabel);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBackground(new Color(240, 248, 255));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        // Tạo các nút chức năng
        JButton checkBalanceBtn = createFeatureButton("💰", "Xem số dư", new Color(76, 175, 80));
        JButton depositBtn = createFeatureButton("💵", "Nạp tiền", new Color(33, 150, 243));
        JButton withdrawBtn = createFeatureButton("💸", "Rút tiền", new Color(255, 152, 0));
        JButton transferBtn = createFeatureButton("💳", "Chuyển khoản", new Color(156, 39, 176));
        
        // Thêm action listeners
        checkBalanceBtn.addActionListener(e -> checkBalance());
        depositBtn.addActionListener(e -> deposit());
        withdrawBtn.addActionListener(e -> withdraw());
        transferBtn.addActionListener(e -> transfer());
        
        // Sắp xếp các nút theo grid 2x2
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(checkBalanceBtn, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        centerPanel.add(depositBtn, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(withdrawBtn, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        centerPanel.add(transferBtn, gbc);
        
        return centerPanel;
    }
    
    private JButton createFeatureButton(String icon, String text, Color color) {
        JButton button = new JButton();
        button.setLayout(new BoxLayout(button, BoxLayout.Y_AXIS));
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 2),
                BorderFactory.createEmptyBorder(30, 20, 30, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Arial", Font.BOLD, 18));
        textLabel.setForeground(Color.BLACK);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        button.add(iconLabel);
        button.add(Box.createRigidArea(new Dimension(0, 10)));
        button.add(textLabel);
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        footerPanel.setBackground(new Color(240, 248, 255));
        
        JButton refreshBtn = new JButton("Làm mới");
        styleFooterButton(refreshBtn, new Color(100, 100, 100));
        refreshBtn.addActionListener(e -> refreshBalance());
        
        JButton logoutBtn = new JButton("Đăng xuất");
        styleFooterButton(logoutBtn, new Color(244, 67, 54));
        logoutBtn.addActionListener(e -> logout());
        
        footerPanel.add(refreshBtn);
        footerPanel.add(logoutBtn);
        
        return footerPanel;
    }
    
    private void styleFooterButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
    }
    
    private void refreshBalance() {
        try {
            double balance = bankService.getBalance(accountNumber);
            balanceLabel.setText(currencyFormat.format(balance) + " VNĐ");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi lấy số dư: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void checkBalance() {
        try {
            double balance = bankService.getBalance(accountNumber);
            String owner = bankService.getAccountOwner(accountNumber);
            
            String message = String.format(
                    "<html><body style='width: 250px; padding: 10px;'>" +
                    "<h2 style='color: #1976D2; margin: 0;'>Thông tin tài khoản</h2>" +
                    "<hr>" +
                    "<p><b>Chủ tài khoản:</b> %s</p>" +
                    "<p><b>Số tài khoản:</b> %s</p>" +
                    "<p><b>Số dư:</b> <span style='color: #4CAF50; font-size: 16px;'><b>%s VNĐ</b></span></p>" +
                    "</body></html>",
                    owner, accountNumber, currencyFormat.format(balance)
            );
            
            JOptionPane.showMessageDialog(this, message, "Thông tin tài khoản", JOptionPane.INFORMATION_MESSAGE);
            refreshBalance();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deposit() {
        String input = JOptionPane.showInputDialog(this,
                "Nhập số tiền muốn nạp (VNĐ):",
                "Nạp tiền",
                JOptionPane.QUESTION_MESSAGE);
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                double amount = Double.parseDouble(input.trim());
                
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Số tiền phải lớn hơn 0!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                boolean success = bankService.deposit(accountNumber, amount);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Nạp tiền thành công!\nSố tiền: " + currencyFormat.format(amount) + " VNĐ",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshBalance();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Nạp tiền thất bại!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Số tiền không hợp lệ!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi: " + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void withdraw() {
        String input = JOptionPane.showInputDialog(this,
                "Nhập số tiền muốn rút (VNĐ):",
                "Rút tiền",
                JOptionPane.QUESTION_MESSAGE);
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                double amount = Double.parseDouble(input.trim());
                
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Số tiền phải lớn hơn 0!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                boolean success = bankService.withdraw(accountNumber, amount);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Rút tiền thành công!\nSố tiền: " + currencyFormat.format(amount) + " VNĐ",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshBalance();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Rút tiền thất bại!\nSố dư không đủ hoặc số tiền không hợp lệ.",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Số tiền không hợp lệ!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi: " + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void transfer() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel toAccountLabel = new JLabel("Tài khoản nhận:");
        JTextField toAccountField = new JTextField(15);
        
        JLabel amountLabel = new JLabel("Số tiền (VNĐ):");
        JTextField amountField = new JTextField(15);
        
        panel.add(toAccountLabel);
        panel.add(toAccountField);
        panel.add(amountLabel);
        panel.add(amountField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Chuyển khoản", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String toAccount = toAccountField.getText().trim();
            String amountStr = amountField.getText().trim();
            
            if (toAccount.isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập đầy đủ thông tin!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (toAccount.equals(accountNumber)) {
                JOptionPane.showMessageDialog(this,
                        "Không thể chuyển tiền cho chính mình!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                double amount = Double.parseDouble(amountStr);
                
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Số tiền phải lớn hơn 0!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Xác nhận chuyển khoản
                int confirm = JOptionPane.showConfirmDialog(this,
                        String.format("Xác nhận chuyển %s VNĐ\nđến tài khoản %s?",
                                currencyFormat.format(amount), toAccount),
                        "Xác nhận chuyển khoản",
                        JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = bankService.transfer(accountNumber, toAccount, amount);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(this,
                                "Chuyển khoản thành công!\n" +
                                "Số tiền: " + currencyFormat.format(amount) + " VNĐ\n" +
                                "Đến tài khoản: " + toAccount,
                                "Thành công",
                                JOptionPane.INFORMATION_MESSAGE);
                        refreshBalance();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Chuyển khoản thất bại!\n" +
                                "Tài khoản đích không tồn tại hoặc số dư không đủ.",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Số tiền không hợp lệ!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi: " + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn đăng xuất?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                bankService.unregisterCallback(accountNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }
    
    public void showNotification(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                    message,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshBalance();
        });
    }
    
    /**
     * Implementation của ClientCallback
     */
    private static class ClientCallbackImpl extends UnicastRemoteObject implements ClientCallback {
        private BankingFrame frame;
        
        protected ClientCallbackImpl(BankingFrame frame) throws RemoteException {
            super();
            this.frame = frame;
        }
        
        @Override
        public void notifyClient(String message) throws RemoteException {
            frame.showNotification(message);
        }
    }
}
