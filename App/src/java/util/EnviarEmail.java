/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 *
 * @author PRCC
 */
public class EnviarEmail {
    
    private final static String userAuthentication = "noreply@prcc.com.br";
    private final static String passwordUserAuthentication = "021089";
    private final static String sender = "noreply@prcc.com.br";
    private final static String smtp = "smtp.prcc.com.br";
    private final static int smtpPort = 587;
    private final static boolean authentication = false;

    public static void enviar(String message, String subject, String receiver)
            throws EmailException {

        SimpleEmail email = new SimpleEmail();
        email.setHostName(smtp);
        email.setCharset("UTF-8");
        email.setSmtpPort(smtpPort);
        email.setAuthentication(userAuthentication, passwordUserAuthentication);
        email.setSSLOnConnect(authentication);
        email.addTo(receiver);
        email.setFrom(sender);
        email.setSubject(subject);
        email.setMsg(message);
        email.send();
        email = null;
    }
}

