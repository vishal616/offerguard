package com.twentyone.offerguard.config;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class SendGridConfig {

	public static String sendGridKey;
	public static List<String> emailRecipients;
	public static String senderEmail;

	@Value("${offer.guard.sendgrid.key}")
	public void setSendGridKey(String apiKey) {
		sendGridKey = apiKey;
	}

	@Value("${offer.guard.emailalert.recipients}")
	public void setEmailRecipients(List<String> recipients) {
		emailRecipients = recipients;
	}

	@Value("${offer.guard.emailalert.sender}")
	public void setSenderEmail(String email) {
		senderEmail = email;
	}

	public static void sendEmail(String subject, Content content) throws IOException {
		log.info("Sending email updates to the recipients");

		try {
			Request request = buildRequestPayload(buildMailPayload(subject, content));
			Response response = new SendGrid(sendGridKey).api(request);
			log.info("Email sent successfully {} {} {}", response.getStatusCode(), response.getBody(), response.getHeaders());
		} catch (IOException e) {
			throw e;
		}
	}

	private static Mail buildMailPayload(String subject, Content content) {
		log.info("Building email payload");
		subject = "Offer Guard Updates";
		content = new Content("text/plain", "and easy to do anywhere, even with Java");
		Mail mail = new Mail();
		Personalization personalization = new Personalization();
		emailRecipients.forEach(email-> {
			personalization.addTo(new Email(email));
		});
		mail.addPersonalization(personalization);
		mail.setSubject(subject);
		mail.setFrom(new Email(senderEmail));
		mail.addContent(content);
		log.info("Email payload built successful");
		return mail;
	}

	private static Request buildRequestPayload(Mail mail) throws IOException {
		log.info("Building email request payload");
		Request request = new Request();
		request.setMethod(Method.POST);
		request.setEndpoint("mail/send");
		request.setBody(mail.build());
		log.info("Email request payload built successful");
		return request;
	}

}
