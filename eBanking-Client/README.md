# eBanking Client

Ứng dụng client với giao diện Java Swing cho hệ thống ngân hàng điện tử.

## Cấu trúc

```
src/main/java/org/example/
├── rmi/
│   ├── BankService.java       # Interface RMI (phải giống Server)
│   └── ClientCallback.java    # Interface callback (phải giống Server)
└── client/
    ├── LoginFrame.java        # Giao diện đăng nhập
    └── BankingFrame.java      # Giao diện chính
```

## Tính năng

✅ Đăng nhập với số tài khoản  
✅ Xem thông tin tài khoản và số dư  
✅ Nạp tiền  
✅ Rút tiền  
✅ Chuyển khoản  
✅ Nhận thông báo realtime khi có người chuyển tiền

## Giao diện

### Màn hình đăng nhập

-  Thiết kế hiện đại với màu xanh dương
-  Icon ngân hàng
-  Input số tài khoản
-  Nút đăng nhập và thoát

### Màn hình chính

-  Header: Hiển thị tên, số tài khoản, số dư
-  4 nút chức năng lớn với icon:
   -  💰 Xem số dư
   -  💵 Nạp tiền
   -  💸 Rút tiền
   -  💳 Chuyển khoản
-  Footer: Nút làm mới và đăng xuất

## Tài khoản mẫu

| Số TK | Tên chủ TK   | Số dư          |
| ----- | ------------ | -------------- |
| 1001  | Nguyễn Văn A | 5,000,000 VNĐ  |
| 1002  | Trần Thị B   | 3,000,000 VNĐ  |
| 1003  | Lê Văn C     | 10,000,000 VNĐ |
| 1004  | Phạm Thị D   | 2,000,000 VNĐ  |
| 1005  | Hoàng Văn E  | 7,500,000 VNĐ  |

## Cách chạy

**Lưu ý**: Phải chạy Server trước!

### Biên dịch:

```bash
mvn clean compile
```

### Chạy Client:

```bash
mvn exec:java
```

Hoặc:

```bash
mvn exec:java -Dexec.mainClass="org.example.client.LoginFrame"
```

## Test chức năng Callback

Để test nhận thông báo realtime:

1. Mở 2 cửa sổ client (chạy 2 lần)
2. Đăng nhập vào 2 tài khoản khác nhau (VD: 1001 và 1002)
3. Từ tài khoản 1001, chuyển tiền cho 1002
4. Client của tài khoản 1002 sẽ tự động hiện thông báo

## Kết nối Server

Client mặc định kết nối đến:

-  **Host**: localhost
-  **Port**: 1099
-  **Service**: BankService

Nếu server ở máy khác, sửa trong `LoginFrame.java`:

```java
Registry registry = LocateRegistry.getRegistry("localhost", 1099);
```

Thay `"localhost"` bằng IP của server.
