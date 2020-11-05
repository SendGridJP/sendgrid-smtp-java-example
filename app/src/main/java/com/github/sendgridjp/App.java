/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.github.sendgridjp;

public class App {
    public static void main(String[] args) throws Exception {
        if (args[0].equals("JavaMailDecoExample")) {
            JavaMailDecoExample.send();
        } else if (args[0].equals("JavaMailMultipartExample")) {
            JavaMailMultipartExample.send();
        } else if (args[0].equals("JavaMailTextExample")) {
            JavaMailTextExample.send();
        } else {
            throw new Exception("Not supported Class name");
        }
    }
}
