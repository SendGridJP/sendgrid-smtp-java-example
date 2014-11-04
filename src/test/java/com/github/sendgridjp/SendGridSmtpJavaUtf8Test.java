package com.github.sendgridjp;

import junit.framework.TestCase;

public class SendGridSmtpJavaUtf8Test extends TestCase {
    public void testSend() throws Exception {
        SendGridSmtpJavaUtf8 instance = new SendGridSmtpJavaUtf8();
        instance.send();
    }
}
