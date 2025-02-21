package com.scripted.Email;

import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.scripted.dataload.PropertyDriver;

public class Sendmail {

	/**
	 * Send mail with specified params
	 * 
	 * @param from    who this is from
	 * @param to      who this is for
	 * @param subject subject of the mail
	 * @param text    body of the mail
	 * @throws IOException
	 */
	public static Logger LOGGER = LogManager.getLogger(Sendmail.class);

	@SuppressWarnings("unused")
	public static void send(String emailProp, String jsonFile) throws IOException {
		PieChart.generateChartFromJsonValue(jsonFile);
		PropertyDriver emailProperty = new PropertyDriver();
		emailProperty.setPropFilePath(emailProp);

		Map<?, ?> result = ScenarioCount.scenarioCountdetails(jsonFile);

		Object passcount = result.get("passcount");
		Object failcount = result.get("failcount");
		Object totalcount = result.get("totalcount");
		String from = PropertyDriver.readProp("mail.from");
		String to = PropertyDriver.readProp("mail.to");
		String cc = PropertyDriver.readProp("mail.cc");
		String subject = PropertyDriver.readProp("mail.subject");
		String projectname = PropertyDriver.readProp("mail.projectname");
		String author = PropertyDriver.readProp("mail.author");
		String buildNo = PropertyDriver.readProp("mail.buildNo");
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		String mailuser = PropertyDriver.readProp("mail.user");
		String mailpassword = PropertyDriver.readProp("mail.password");
		String dateandtime = dateFormat.format(new Date());
		Properties prop = null;
		try (FileReader reader = new FileReader(PropertyDriver.getFilePath())) {
			prop = new Properties();
			prop.load(reader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Session mailSession = Session.getDefaultInstance(prop, new jakarta.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(mailuser, mailpassword);
			}
		});

		// Create a default MimeMessage object.
		Message simpleMessage = new MimeMessage(mailSession);

		InternetAddress fromAddress = null;
		InternetAddress toAddress = null;
		InternetAddress ccAddress = null;
		try {
			fromAddress = new InternetAddress(from);
			toAddress = new InternetAddress(to);
			ccAddress = new InternetAddress(cc);
		} catch (AddressException e) {
			e.printStackTrace();
			LOGGER.error("Error while send an email " + "Exception :" + e);
		}

		try {

			simpleMessage.setFrom(fromAddress);
			simpleMessage.setRecipient(RecipientType.TO, toAddress);
			simpleMessage.setRecipient(RecipientType.CC, ccAddress);
			simpleMessage.setSubject(subject);

			// This mail has 2 part, the BODY and the embedded image
			MimeMultipart multipart = new MimeMultipart("related");

			// first part (the html)

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent("<html>\n" + "<body>\n"
					+ "<img src=\"https://ustrepo.github.io/Skriptmate/img/EmailLogo.png\" width=\"600\" height=\"130\"><br><br>"
					+ "<table width=\"400\" border=\"1\">\n" + "<tr>\n" + "<th align=\"left\">Project Name</th>\n"
					+ "<td>" + projectname + "</td>\n" + "</tr>\n" + "<tr>\n"
					+ "<th align=\"left\">Test Executor</th>\n" + "<td>" + author + "</td>\n" + "</tr>\n" + "<tr>\n"
					+ "<th align=\"left\">Date and Time</th>\n" + "<td>" + dateandtime + "</td>\n" + "</tr>\n"
					+ "</table><br><br>\n" + "<img src=\"cid:image\"height=\"400\" width=\"400\"><br><br><br>\n"
					+ "<table width=\"400\" border=\"1\">\n" + "<tr>\n" + "<th>Total Scenarios</th>\n"
					+ "<th>Scenarios Passed</th>\n" + "<th>Scenarios Failed</th>\n" + "</tr>\n" + "<tr>\n"
					+ "<td><b><center>" + totalcount + "</center></td>\n" + "<td><b><center><font color=\"green\">"
					+ passcount + "</font></center></td>\n" + "<td><b><center><font color=\"red\">" + failcount
					+ "</font></center></td>\n" + "</tr>" + "</table>" + "</body>\n" + "</html>", "text/html");

			multipart.addBodyPart(messageBodyPart);

			// second part (the image)
			messageBodyPart = new MimeBodyPart();
			DataSource fds = new FileDataSource("src/main/resources/Reporting/Email/PieChart.jpeg");

			messageBodyPart.setDataHandler(new DataHandler(fds));
			messageBodyPart.setHeader("Content-ID", "<image>");

			// add image to the multipart
			multipart.addBodyPart(messageBodyPart);

			// put everything together
			simpleMessage.setContent(multipart);
			// Send message
			Transport.send(simpleMessage);

		} catch (MessagingException e) {
			LOGGER.error("Error while generating chart from json file " + "Exception :" + e);
			e.printStackTrace();
		}
	}

}