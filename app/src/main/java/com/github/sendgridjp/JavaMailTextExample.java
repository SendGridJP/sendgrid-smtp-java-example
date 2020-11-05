package com.github.sendgridjp;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.InternetAddress;
import io.github.cdimascio.dotenv.Dotenv;

import com.sendgrid.smtpapi.SMTPAPI;

public class JavaMailTextExample {
  // 設定情報
  static Dotenv dotenv = Dotenv.load();
  static final String USERNAME = dotenv.get("SENDGRID_USERNAME");
  static final String PASSWORD = dotenv.get("SENDGRID_PASSWORD");
  static final String[] TOS    = dotenv.get("TOS").split(",");
  static final String[] NAMES  = dotenv.get("NAMES").split(",");
  static final String FROM     = dotenv.get("FROM");
  static final String CHARSET  = "ISO-2022-JP"; // "UTF-8";
  static final String ENCODE   = "7bit"; // "base64"; // "quoted-printable";

  public static void send()
  throws MessagingException, UnsupportedEncodingException {
    // SMTP接続情報
    Properties props = new Properties();
    props.put("mail.transport.protocol", "smtps");
    props.put("mail.smtp.host", "smtp.sendgrid.net");
    props.put("mail.smtp.port", "587");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.starttls.required", "true");
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
    String body = "こんにちは、nameさん\r\nようこそ〜テキストメールの世界へ！";
    message.setText(body, CHARSET, "plain");
    message.setHeader("Content-Transfer-Encoding", ENCODE);

    // X-SMTPAPIヘッダ
    String smtpapi = createSmtpapi(TOS, NAMES);
    smtpapi = MimeUtility.encodeText(smtpapi);
    message.setHeader("X-SMTPAPI", MimeUtility.fold(76, smtpapi));

    // 送信
    mailSession.getTransport().send(message);
  }

  // X-SMTPAPIヘッダに設定する値の生成
  private static String createSmtpapi(String[] tos, String[] names) {
    SMTPAPI smtpapi = new SMTPAPI();
    smtpapi.setTos(tos);
    smtpapi.addSubstitutions("name", names);
    smtpapi.addCategory("category1");
    return smtpapi.rawJsonString();
  }

  private static class SMTPAuthenticator extends javax.mail.Authenticator {
    public PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(USERNAME, PASSWORD);
    }
  }
}
