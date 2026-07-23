package com.savbill.radius.utils;
/*
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Pojo {

	public static void main(String[] args) throws InterruptedException {
		
		Pojo pojo=new Pojo();
		pojo.sendmail();
		
		
		Date startdate=new Date();
		double testing=(10000d+10000d);
		testing=testing/1000000000;
		
		Long totaltime= 0L;
		Double totalTimeMin=0d;
			
		totaltime=Long.parseLong("20");
		//		System.out.println("Time is "+totaltime);
		totalTimeMin=totaltime/60d;
		
		double  upload=20d;
		double  gigaWords= (1000000000d*4d)*1d;
    	upload=upload+gigaWords;
    	//		System.out.println("upload:"+upload);

		
		//		System.out.println("Testing"+testing);
		String sample="{SUBSTRING(0,5,{Acct-Session-Id}}";
		if(sample.startsWith("{") && sample.endsWith("}")) {
				StringBuilder sb = new StringBuilder(sample); 
		        sb.deleteCharAt(sample.length() - 1); 
		        sb.deleteCharAt(0); 
		        sample=sb.toString(); 
		        if(sample.startsWith("SUBSTRING")) {
					sb = new StringBuilder(sample); 
			        sb.delete(0,10);
			        sample=sb.toString();
			        //		System.out.println("Output is:"+sample);
			        String[] sampleSpli = sample.split(",");
			        String value=sampleSpli[2];
			        int start=Integer.parseInt(sampleSpli[0]);
			        int end=Integer.parseInt(sampleSpli[1]);
			        //		System.out.println("Final Value:"+value.substring(start,end));
		        }
		}
		TimeUnit.SECONDS.sleep(5);
		Date end=new Date();
	    long diffInMillies = Math.abs(startdate.getTime() - end.getTime());
	    //		System.out.println("Date diff in ms is "+diffInMillies);
		

	}
	
	
	public void sendmail() {

        final String username = "usernme";
        final String password = "passowrd";
        String recipient = "receipt";
        String subject = "Test Email";
        String message = "Hello, this is a test email.";

        // Set mail properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "IP");
        props.put("mail.smtp.port", "25");

        // Create a Session object
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a new message
            Message email = new MimeMessage(session);
            email.setFrom(new InternetAddress(username));
            email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            email.setSubject(subject);
            email.setText(message);

            // Send the message
            Transport.send(email);

            //		System.out.println("Email sent successfully.");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
	}

}
*/
