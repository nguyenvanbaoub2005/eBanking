# eBanking Server

Server RMI cho hệ thống ngân hàng điện tử.

## Cấu trúc

```
src/main/java/org/example/
├── rmi/
│   ├── BankService.java       # Interface RMI
│   └── ClientCallback.java    # Interface callback
├── model/
│   └── Account.java           # Model tài khoản
└── server/
    ├── BankServer.java        # Implementation server
    └── ServerMain.java        # Main class
```

## Chức năng

-  Quản lý tài khoản ngân hàng
-  Xử lý đăng nhập
-  Xử lý giao dịch: nạp tiền, rút tiền, chuyển khoản
-  Lưu trữ dữ liệu vào file `accounts.txt`
-  Gửi thông báo realtime cho client qua callback

## Cách chạy

### Biên dịch:

```bash
mvn clean compile
```

### Chạy Server:

```bash
mvn exec:java
```

Hoặc:

```bash
mvn exec:java -Dexec.mainClass="org.example.server.ServerMain"
```

Server sẽ khởi động trên **port 1099** và sẵn sàng nhận kết nối từ client.

## File dữ liệu

File `accounts.txt` chứa thông tin tài khoản theo format:

```
accountNumber,owner,balance
```

Ví dụ:

```
1001,Nguyễn Văn A,5000000
1002,Trần Thị B,3000000
```

File sẽ được tự động tạo với dữ liệu mặc định nếu chưa tồn tại.

## Lưu ý

-  Server phải chạy trước khi khởi động client
-  Đảm bảo port 1099 không bị chiếm dụng
-  Mọi thay đổi giao dịch đều được lưu vào file ngay lập tức
