package com.vku.ebanking.server;

import com.vku.ebanking.shared.Account;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XMLAccountHandler {
    private static final String XML_FILE = "accounts.xml";
    private DocumentBuilder builder;
    private Transformer transformer;

    public XMLAccountHandler() {
        try {
            // Khởi tạo DocumentBuilder để parse XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();

            // Khởi tạo Transformer để ghi XML với format đẹp
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            System.out.println("✅ XMLAccountHandler đã khởi tạo");
        } catch (Exception e) {
            System.err.println("❌ Lỗi khởi tạo XMLAccountHandler: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Đọc danh sách tài khoản từ file XML
     * Nếu file không tồn tại, tự động tạo file mặc định
     */
    public List<Account> loadAccounts() {
        List<Account> accounts = new ArrayList<>();
        try {
            File xmlFile = new File(XML_FILE);

            // Kiểm tra file có tồn tại không
            if (!xmlFile.exists()) {
                System.out.println("⚠️ File XML không tồn tại, đang tạo file mặc định...");
                createDefaultXML();
                xmlFile = new File(XML_FILE);
            }

            // Parse XML file
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // Lấy tất cả các node <account>
            NodeList nodeList = doc.getElementsByTagName("account");
            System.out.println("📖 Đọc " + nodeList.getLength() + " tài khoản từ XML");

            // Duyệt qua từng account và tạo object
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);

                String accountNumber = getElementText(element, "accountNumber");
                String accountName = getElementText(element, "accountName");
                double balance = Double.parseDouble(getElementText(element, "balance"));
                String pin = getElementText(element, "pin");

                accounts.add(new Account(accountNumber, accountName, balance, pin));
            }

            System.out.println("✅ Load thành công " + accounts.size() + " tài khoản");
        } catch (Exception e) {
            System.err.println("❌ Lỗi load accounts từ XML: " + e.getMessage());
            e.printStackTrace();
        }
        return accounts;
    }

    /**
     * Lưu danh sách tài khoản vào file XML
     * Được gọi ngay khi có thay đổi (nạp tiền, rút tiền, chuyển khoản)
     */
    public synchronized void saveAccounts(List<Account> accounts) {
        try {
            System.out.println("💾 Đang lưu " + accounts.size() + " tài khoản vào XML...");

            // Tạo Document mới
            Document doc = builder.newDocument();

            // Tạo root element <accounts>
            Element rootElement = doc.createElement("accounts");
            doc.appendChild(rootElement);

            // Tạo từng <account> element
            for (Account account : accounts) {
                Element accountElement = doc.createElement("account");

                // Thêm các child elements
                appendChild(doc, accountElement, "accountNumber", account.getAccountNumber());
                appendChild(doc, accountElement, "accountName", account.getAccountName());
                appendChild(doc, accountElement, "balance", String.valueOf(account.getBalance()));
                appendChild(doc, accountElement, "pin", account.getPin());

                rootElement.appendChild(accountElement);
            }

            // Ghi ra file
            DOMSource source = new DOMSource(doc);
            File file = new File(XML_FILE);
            StreamResult result = new StreamResult(file);

            transformer.transform(source, result);

            System.out.println("✅ Lưu thành công vào file: " + file.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("❌ Lỗi lưu accounts vào XML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tạo file XML mặc định với 4 tài khoản demo
     */
    private void createDefaultXML() {
        System.out.println("🔧 Đang tạo file XML mặc định...");

        List<Account> defaultAccounts = new ArrayList<>();
        defaultAccounts.add(new Account("01234", "Nguyễn Văn A", 5000, "1234"));
        defaultAccounts.add(new Account("12345", "Trần Thị B", 10000, "5678"));
        defaultAccounts.add(new Account("98765", "Lê Văn C", 15000, "9999"));
        defaultAccounts.add(new Account("11111", "Phạm Thị D", 20000, "0000"));

        saveAccounts(defaultAccounts);
        System.out.println("✅ Đã tạo file mặc định với 4 tài khoản");
    }

    /**
     * Helper method: Lấy text content của một element con
     */
    private String getElementText(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }

    /**
     * Helper method: Thêm một child element với text content
     */
    private void appendChild(Document doc, Element parent, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        element.setTextContent(textContent);
        parent.appendChild(element);
    }

    /**
     * Lấy thông tin một tài khoản cụ thể từ XML
     * (Dùng cho debug hoặc kiểm tra nhanh)
     */
    public Account getAccount(String accountNumber) {
        List<Account> accounts = loadAccounts();
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Cập nhật một tài khoản cụ thể
     * (Alternative method nếu không muốn load toàn bộ list)
     */
    public synchronized boolean updateAccount(Account updatedAccount) {
        try {
            List<Account> accounts = loadAccounts();
            boolean found = false;

            for (int i = 0; i < accounts.size(); i++) {
                if (accounts.get(i).getAccountNumber().equals(updatedAccount.getAccountNumber())) {
                    accounts.set(i, updatedAccount);
                    found = true;
                    break;
                }
            }

            if (found) {
                saveAccounts(accounts);
                System.out.println("✅ Cập nhật tài khoản: " + updatedAccount.getAccountNumber());
                return true;
            } else {
                System.err.println("❌ Không tìm thấy tài khoản: " + updatedAccount.getAccountNumber());
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi cập nhật tài khoản: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiểm tra file XML có tồn tại không
     */
    public boolean fileExists() {
        File file = new File(XML_FILE);
        return file.exists();
    }

    /**
     * Xóa file XML (dùng cho testing)
     */
    public boolean deleteFile() {
        File file = new File(XML_FILE);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("🗑️ Đã xóa file XML");
            }
            return deleted;
        }
        return false;
    }

    /**
     * Lấy đường dẫn tuyệt đối của file XML
     */
    public String getFilePath() {
        File file = new File(XML_FILE);
        return file.getAbsolutePath();
    }
}