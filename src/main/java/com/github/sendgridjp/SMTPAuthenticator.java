package com.github.sendgridjp;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SMTPAuthenticator extends javax.mail.Authenticator {

  private String mUsername = "";
  private String mPassword = "";

  public SMTPAuthenticator(String username, String password) {
    mUsername = username;
    mPassword = password;
  }
  public PasswordAuthentication getPasswordAuthentication() {
    return new PasswordAuthentication(mUsername, mPassword);
  }
}
