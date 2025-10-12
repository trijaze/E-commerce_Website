package vn.bachhoa.util;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Lớp tiện ích để xử lý việc gửi email.
 */
public class EmailUtil {
    // --- CẤU HÌNH EMAIL ---
    private static final String FROM_EMAIL = "ngthhongnhung0405@gmail.com"; 
    private static final String PASSWORD = "zqab chsw dtev yzfq"; 

    /**
     * Gửi email chào mừng đến người dùng mới.
     * @param toEmail Email của người nhận.
     * @param username Tên đăng nhập của người dùng mới.
     */
    public static void sendWelcomeEmail(String toEmail, String username) {
        // Cấu hình các thuộc tính cho máy chủ SMTP của Gmail
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Máy chủ SMTP của Gmail
        props.put("mail.smtp.port", "587");             // Cổng TLS
        props.put("mail.smtp.auth", "true");            // Yêu cầu xác thực
        props.put("mail.smtp.starttls.enable", "true"); // Bật STARTTLS

        // Tạo một phiên (Session) với Authenticator để cung cấp username và password
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        try {
            // Tạo một đối tượng MimeMessage
            Message message = new MimeMessage(session);
            
            // Đặt người gửi
            message.setFrom(new InternetAddress(FROM_EMAIL));
            
            // Đặt người nhận
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            
            // Đặt tiêu đề email
            message.setSubject("Chào mừng bạn đến với Bách Hóa Online!");
            
            // Đặt nội dung email (sử dụng HTML để có định dạng đẹp)
            String htmlContent = "<h1>Chào mừng, " + username + "!</h1>"
                               + "<p>Cảm ơn bạn đã đăng ký tài khoản tại <strong>Bách Hóa Online</strong>.</p>"
                               + "<p>Chúc bạn có những trải nghiệm mua sắm tuyệt vời!</p>"
                               + "<p>Trân trọng,<br>Đội ngũ Bách Hóa Online</p>";
            message.setContent(htmlContent, "text/html; charset=utf-8");

            // Gửi email
            Transport.send(message);

            System.out.println(">>> [EmailUtil] Welcome email sent successfully to " + toEmail);

        } catch (Exception e) {
            System.err.println(">>> [EmailUtil] Failed to send welcome email to " + toEmail);
            e.printStackTrace(); // In lỗi ra console để debug
        }
    }
}
