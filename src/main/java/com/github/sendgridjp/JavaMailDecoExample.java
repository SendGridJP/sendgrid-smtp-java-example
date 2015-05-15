package com.github.sendgridjp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.InternetAddress;
import javax.mail.util.ByteArrayDataSource;

import com.sendgrid.smtpapi.SMTPAPI;

public class JavaMailDecoExample {
  // 設定情報
  static final String USERNAME = System.getenv("SENDGRID_USERNAME");
  static final String PASSWORD = System.getenv("SENDGRID_PASSWORD");
  static final String[] TOS    = System.getenv("TOS").split(",");
  static final String[] NAMES  = System.getenv("NAMES").split(",");
  static final String FROM     = System.getenv("FROM");
  static final String CHARSET  = "ISO-2022-JP"; // "UTF-8";
  static final String ENCODE   = "7bit"; // "base64"; // "quoted-printable";

  public static void main(String[] args)
  throws IOException, MessagingException, UnsupportedEncodingException {
    // SMTP接続情報
    Properties props = new Properties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.host", "smtp.sendgrid.net");
    props.put("mail.smtp.port", 587);
    props.put("mail.smtp.auth", "true");
    Authenticator auth = new SMTPAuthenticator();
    Session mailSession = Session.getDefaultInstance(props, auth);
    mailSession.setDebug(true); // console log for debug

    // メッセージの構築
    MimeMessage message = new MimeMessage(mailSession);

    // ダミーの宛先(X-SMTPAPIの宛先が優先される)
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(FROM));

    // From
    message.setFrom(FROM);

    // Subject
    message.setSubject("こんにちはSendGrid", CHARSET);

    // Body
    // Root part
    MimeMultipart rootPart = new MimeMultipart();
    if (TOS[0].contains("@ezweb.ne.jp")) {
      rootPart.setSubType("mixed");
    } else {
      rootPart.setSubType("related");
    }
    // Alternative part
    MimeMultipart altPart = new MimeMultipart();
    altPart.setSubType("alternative");
    // Text part
    String body = "こんにちは、nameさん\r\nようこそ〜テキストメールの世界へ！";
    MimeBodyPart textBodyPart = new MimeBodyPart();
    textBodyPart.setText(body, CHARSET, "plain");
    textBodyPart.setHeader("Content-Transfer-Encoding", ENCODE);
    altPart.addBodyPart(textBodyPart);
    // Html part
    String htmlBody =
      "<html>" +
      "<body bgcolor=\"#d9edf7\" style=\"background-color: #d9edf7;\">" +
      "こんにちは、nameさん<br>ようこそ〜HTMLメールの世界へ！<br>" +
      "<img src=\"cid:123@456\">" +
      "</body></html>";
    MimeBodyPart htmlBodyPart = new MimeBodyPart();
    htmlBodyPart.setText(htmlBody, CHARSET, "html");
    htmlBodyPart.setHeader("Content-Transfer-Encoding", ENCODE);
    altPart.addBodyPart(htmlBodyPart);
    // Alternative part
    MimeBodyPart altBodyPart = new MimeBodyPart();
    altBodyPart.setContent(altPart);
    rootPart.addBodyPart(altBodyPart, 0);
    // Attachment part
    File image = new File("./logo.gif");
    InputStream input = new FileInputStream(image);
    MimeBodyPart attachmentPart = getMimeBodyPart(
      input, "logo.gif", "image/gif", "123@456"
    );
    rootPart.addBodyPart(attachmentPart);

    message.setContent(rootPart);

    // X-SMTPAPIヘッダ
    String smtpapi = createSmtpapi(TOS, NAMES);
    smtpapi = MimeUtility.encodeText(smtpapi);
    message.setHeader("X-SMTPAPI", MimeUtility.fold(76, smtpapi));

    // 送信
    mailSession.getTransport().send(message);
  }

  // InputStreamからbyte配列を生成
  private static byte[] getBytes(InputStream is) throws IOException {
    ByteArrayOutputStream b = new ByteArrayOutputStream();
    OutputStream os = new BufferedOutputStream(b);
    int c;
    try {
      while ((c = is.read()) != -1) {
        os.write(c);
      }
    } finally {
      if (os != null) {
        os.flush();
        os.close();
      }
    }
    return b.toByteArray();
  }

  // InputStreamからMimeBodyPartを生成
  private static MimeBodyPart getMimeBodyPart(
    InputStream is, String name, String type, String cid
  )
  throws IOException, MessagingException, UnsupportedEncodingException {
    MimeBodyPart attachment = new MimeBodyPart();
    DataSource dataSource = new ByteArrayDataSource(getBytes(is), type);
    DataHandler dataHandler = new DataHandler(dataSource);
    attachment.setDataHandler(dataHandler);
    attachment.setFileName(MimeUtility.encodeWord(name));
    attachment.setContentID("<" + cid + ">");
    if (TOS[0].contains("@docomo.ne.jp")) {
      attachment.setDisposition(MimeBodyPart.INLINE);
    } else {
      attachment.setDisposition(MimeBodyPart.ATTACHMENT);
    }
    return attachment;
  }

  // X-SMTPAPIヘッダに設定する値の生成
  private static String createSmtpapi(String[] tos, String[] names) {
    SMTPAPI smtpapi = new SMTPAPI();
    smtpapi.setTos(tos);
    smtpapi.addSubstitutions("name", names);
    smtpapi.addCategory("category1");
    return smtpapi.rawJsonString();
  }

  // SMTP
  private static class SMTPAuthenticator extends javax.mail.Authenticator {
    public PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(USERNAME, PASSWORD);
    }
  }
}
