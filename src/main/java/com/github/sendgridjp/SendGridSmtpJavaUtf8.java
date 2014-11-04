package com.github.sendgridjp;

import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Authenticator;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.activation.FileDataSource;
import javax.activation.DataHandler;
import com.sendgrid.smtpapi.SMTPAPI;

public class SendGridSmtpJavaUtf8 {

    public static void main(String[] args) throws Exception {
        SendGridSmtpJavaUtf8 instance = new SendGridSmtpJavaUtf8();
        instance.send();
    }

    public void send() throws Exception {
        // Load env
        String username  = System.getenv("SENDGRID_USERNAME");
        String password  = System.getenv("SENDGRID_PASSWORD");
        String[] tos     = System.getenv("TOS").split(",",0);
        String from      = System.getenv("FROM");

        // SMTP props
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.sendgrid.net");
        props.put("mail.smtp.port", 587);
        props.put("mail.smtp.auth", "true");

        // Auth and transport
        Authenticator auth = new SMTPAuthenticator(username, password);
        Session mailSession = Session.getDefaultInstance(props, auth);
        mailSession.setDebug(true);
        Transport transport = mailSession.getTransport();
        MimeMessage message = new MimeMessage(mailSession);

        // smtpapi
        message.setHeader("x-smtpapi", getSmtpApiHeaderValue(tos));

        message.setFrom(from);
        message.setSubject(
            "[sendgrid-smtp-java-example] フクロウのお名前はfullnameさん",
            "utf-8");
        message.addRecipient(Message.RecipientType.TO, new InternetAddress("test@test.com")); // dummy address

        // multipart body
        Multipart multipart = new MimeMultipart("alternative");
        BodyPart textPart = new MimeBodyPart();
        textPart.setContent(
            "familyname さんは何をしていますか？\r\n 彼はplaceにいます。",
            "text/plain; charset=utf-8");
        BodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(
            "<strong> familyname さんは何をしていますか？</strong><br />彼はplaceにいます。",
            "text/html;charset=utf-8");
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(htmlPart);

        // Attachment
        String filename = "./gif.gif";
        BodyPart filePart = new MimeBodyPart();
        FileDataSource fileData = new FileDataSource(filename);
        filePart.setDataHandler(new DataHandler(fileData));
        filePart.setFileName(fileData.getName());
        multipart.addBodyPart(filePart);

        message.setContent(multipart);

        // send mail
        transport.connect();
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
        transport.close();
    }

    private String getSmtpApiHeaderValue(String[] tos) {
        SMTPAPI smtpapi = new SMTPAPI();
        smtpapi.setTos(tos);
        smtpapi.addSubstitution("fullname", "田中 太郎");
        smtpapi.addSubstitution("fullname", "佐藤 次郎");
        smtpapi.addSubstitution("fullname", "鈴木 三郎");
        smtpapi.addSubstitution("familyname", "田中");
        smtpapi.addSubstitution("familyname", "佐藤");
        smtpapi.addSubstitution("familyname", "鈴木");
        smtpapi.addSubstitution("place", "office");
        smtpapi.addSubstitution("place", "home");
        smtpapi.addSubstitution("place", "office");
        smtpapi.addSection("office", "中野");
        smtpapi.addSection("home", "目黒");
        smtpapi.addCategory("Category1");
        return smtpapi.jsonString();
    }
}
