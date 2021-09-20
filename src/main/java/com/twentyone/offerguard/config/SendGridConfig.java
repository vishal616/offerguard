package com.twentyone.offerguard.config;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

@Slf4j
public class SendGridConfig {

	public static String sendGridKey;

	@Value("${offer.guard.sendgrid.key}")
	public void setSendGridKey(String apiKey) {
		sendGridKey = apiKey;
	}

	public static void sendEmail() {
		log.info("Sending email updates to the recipients");

		Email fromEmail = new Email("info@twyntyone.com");
		Email toEmail = new Email("vishal.vishalmishra.mishra2@gamil.com");
		String subject = "Offer Guard Updates";

		Content content = new Content("text/plain", "and easy to do anywhere, even with Java");

		Mail mail = new Mail(fromEmail, subject, toEmail, content);
		SendGrid sg = new SendGrid(sendGridKey);
		Request request = new Request();

		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			Response response = sg.api(request);
			log.info("Email sent successfully {} {} {}", response.getStatusCode(), response.getBody(), response.getHeaders());
		} catch (IOException ex) {
			log.error("Email sending process failed");
		}
	}

}
