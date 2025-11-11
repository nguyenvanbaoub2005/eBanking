# Hệ thống eBanking với RMI và Java Swing

Hệ thống ngân hàng điện tử đơn giản sử dụng công nghệ Java RMI và giao diện Java Swing.

## Tính năng

-  ✅ Đăng nhập với số tài khoản
-  ✅ Xem thông tin tài khoản và số dư
-  ✅ Nạp tiền vào tài khoản
-  ✅ Rút tiền từ tài khoản
-  ✅ Chuyển khoản giữa các tài khoản
-  ✅ Nhận thông báo khi có người chuyển tiền (callback)
-  ✅ Lưu trữ dữ liệu trong file text

## Cấu trúc dự án

```
eBanking/
├── src/main/java/org/example/
│   ├── rmi/
│   │   ├── BankService.java         # Interface RMI cho dịch vụ ngân hàng
│   │   └── ClientCallback.java      # Interface callback cho thông báo
│   ├── model/
│   │   └── Account.java             # Model tài khoản
│   ├── server/
│   │   ├── BankServer.java          # Implementation RMI Server
│   │   └── ServerMain.java          # Main class khởi chạy server
│   └── client/
│       ├── LoginFrame.java          # Giao diện đăng nhập
│       └── BankingFrame.java        # Giao diện chính
├── accounts.txt                     # File lưu trữ dữ liệu tài khoản
└── pom.xml
```

## Tài khoản mẫu

| Số tài khoản | Tên chủ tài khoản | Số dư ban đầu  |
| ------------ | ----------------- | -------------- |
| 1001         | Nguyễn Văn A      | 5,000,000 VNĐ  |
| 1002         | Trần Thị B        | 3,000,000 VNĐ  |
| 1003         | Lê Văn C          | 10,000,000 VNĐ |
| 1004         | Phạm Thị D        | 2,000,000 VNĐ  |
| 1005         | Hoàng Văn E       | 7,500,000 VNĐ  |

## Hướng dẫn chạy ứng dụng

### Bước 1: Khởi chạy RMI Server

Mở terminal và chạy lệnh:

```bash
mvn compile exec:java -Dexec.mainClass="org.example.server.ServerMain"
```

Hoặc chạy trực tiếp class `ServerMain` trong IDE.

Server sẽ khởi động trên port 1099 và sẵn sàng nhận kết nối.

### Bước 2: Khởi chạy Client

Mở terminal mới (hoặc chạy trong IDE) và chạy:

```bash
mvn compile exec:java -Dexec.mainClass="org.example.client.LoginFrame"
```

Hoặc chạy trực tiếp class `LoginFrame` trong IDE.

### Bước 3: Sử dụng ứng dụng

1. **Đăng nhập**: Nhập một trong các số tài khoản mẫu (1001-1005)
2. **Xem số dư**: Click nút "Xem số dư" để xem chi tiết tài khoản
3. **Nạp tiền**: Click "Nạp tiền" và nhập số tiền muốn nạp
4. **Rút tiền**: Click "Rút tiền" và nhập số tiền muốn rút
5. **Chuyển khoản**: Click "Chuyển khoản", nhập số tài khoản đích và số tiền

## Test chức năng Callback

Để test chức năng nhận thông báo khi có người chuyển tiền:

1. Mở 2 cửa sổ client (chạy LoginFrame 2 lần)
2. Đăng nhập vào 2 tài khoản khác nhau (ví dụ: 1001 và 1002)
3. Từ tài khoản 1001, chuyển tiền cho 1002
4. Cửa sổ client của tài khoản 1002 sẽ tự động hiện thông báo nhận tiền

## Công nghệ sử dụng

-  **Java RMI**: Giao tiếp client-server
-  **Java Swing**: Giao diện người dùng
-  **File I/O**: Lưu trữ dữ liệu
-  **Maven**: Quản lý dự án

## Đặc điểm giao diện

-  🎨 Thiết kế hiện đại với màu sắc bắt mắt
-  🖱️ Hover effects trên các nút
-  💡 Icon trực quan cho từng chức năng
-  📱 Layout responsive và dễ sử dụng
-  ✨ Thông báo realtime khi nhận tiền

## Lưu ý

-  Đảm bảo server đang chạy trước khi khởi động client
-  Dữ liệu được lưu vào file `accounts.txt` sau mỗi giao dịch
-  Có thể chạy nhiều client cùng lúc để test chức năng callback
-  File `accounts.txt` sẽ được tạo tự động nếu chưa tồn tại
