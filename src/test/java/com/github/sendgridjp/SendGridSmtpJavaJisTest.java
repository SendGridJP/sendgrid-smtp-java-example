package com.github.sendgridjp;

import junit.framework.TestCase;

public class SendGridSmtpJavaJisTest extends TestCase {
    public void testSend() throws Exception {
        SendGridSmtpJavaJis instance = new SendGridSmtpJavaJis();
        instance.send();
    }
}
