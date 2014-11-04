package com.github.sendgridjp;

import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;
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
import org.apache.commons.codec.binary.Base64;
import com.sendgrid.smtpapi.SMTPAPI;

public class SendGridSmtpJavaJis {

    public static void main(String[] args) throws Exception {
        SendGridSmtpJavaJis instance = new SendGridSmtpJavaJis();
        instance.send();
    }

    public void send()  throws Exception {
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

        // encode smtpapi
        String rfc1522 = rfc1522(getSmtpApiHeaderValue(tos));
        message.setHeader("x-smtpapi", rfc1522);

        message.setFrom(from);
        message.setSubject(
            "[sendgrid-smtp-java-example] フクロウのお名前はfullnameさん",
            "ISO-2022-JP");
        message.addRecipient(Message.RecipientType.TO, new InternetAddress("test@test.com")); // dummy address

        // multipart body
        Multipart multipart = new MimeMultipart("alternative");
        BodyPart textPart = new MimeBodyPart();
        textPart.setContent(
            "familyname さんは何をしていますか？\r\n 彼はplaceにいます。",
            "text/plain; charset=iso-2022-jp");
        BodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(
            "<strong> familyname さんは何をしていますか？</strong><br />彼はplaceにいます。",
            "text/html;charset=iso-2022-jp");
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

    private String rfc1522(String unicode)  throws CharacterCodingException {
        // Unicode -> ISO-2022-JP
        byte[] jis = unicode2iso2022jp(unicode);
        // ISO-2022-JP -> Base64 encode
        String base64 = Base64.encodeBase64String(jis);
        // rfc1522
        String rfc1522 = String.format("=?ISO-2022-JP?B?%s?=", base64);
        return rfc1522;
    }

    private byte[] unicode2iso2022jp(String unicode) throws CharacterCodingException {
        ByteBuffer encoded = null;
        byte[] capacity = null;
        List<Byte> temp = new ArrayList<Byte>();
        byte[] result = null;
        CharsetEncoder jisEncoder = Charset.forName("ISO-2022-JP").newEncoder();
        encoded = jisEncoder.encode(CharBuffer.wrap(unicode.toCharArray()));
        capacity = encoded.array();
        for (int i = 0; i < encoded.limit(); i++) {
            temp.add(capacity[i]);
        }
        result = new byte[temp.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = temp.get(i);
        }
        return result;
    }
}
