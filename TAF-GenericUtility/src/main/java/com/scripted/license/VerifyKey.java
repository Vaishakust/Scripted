package com.scripted.license;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;

public class VerifyKey {
	private static String cdir = System.getProperty("user.dir");
	@SuppressWarnings("unused")
	private static String propertiesDir = cdir + "/src/main/resources/license/licensedetails.properties";
	@SuppressWarnings("unused")
	private static String keyDir = cdir + "/src/main/resources/license/LicenseKey.txt";
	private static String org = "UST";
	private static String objsecretKey = "skriptmatelicence";

	public String readfile() throws GeneralSecurityException, IOException {
		String value = "";

		String path = System.getProperty("user.dir");
		String scriptpath = path.replace("TAF-GenericUtility", "TAF-Scripting");

		FileInputStream inputStream = new FileInputStream(
				scriptpath + "\\src\\main\\resources\\License\\LicenseKey.txt");
		try {		
			value = IOUtils.toString(inputStream, "UTF-8");
		} finally {
			inputStream.close();
		}
		return value;
	}

	public Boolean verifyexpiry() throws GeneralSecurityException, IOException, ParseException, InterruptedException {
		Boolean licensestatus = true;
		byte[] jsonData = null;
		String value = readfile();
		if (value == null || value.isEmpty()) {
			licensestatus = false;
			Thread.sleep(3000);
			Thread t = new Thread(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(null,
							"Please provide a valid key in src/main/resources/license txt to continue execution. Please contact QE platform support : noskript_support@ust.com",
							"License key", JOptionPane.PLAIN_MESSAGE);
				}
			});
			// t.run();
			t.start();
//			JOptionPane.showMessageDialog(null,"Please provide a valid key in license txt to continue execution", "License key", JOptionPane.PLAIN_MESSAGE);    		
		} else {
			try {
				jsonData = DataEncryptor.decryptPass(value).getBytes();
			} catch (IllegalBlockSizeException | BadPaddingException e) {
				licensestatus = false;
				Thread.sleep(3000);
				Thread t = new Thread(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(null,
								"Please provide a valid key in src/main/resources/license txt to continue execution. Please contact QE platform support : noskript_support@ust.com",
								"License key", JOptionPane.PLAIN_MESSAGE);
					}
				});
				// t.run();
				t.start();
//			JOptionPane.showMessageDialog(null,"Please provide a valid key in license txt to continue execution", "License key", JOptionPane.PLAIN_MESSAGE); 
			}
			String str = new String(jsonData);
			String[] strAr1 = str.split(":");
			String stardate = strAr1[0];
			String endDate = strAr1[1];
			String organization = strAr1[2];
			String secretKey = strAr1[3];
			if (organization.equals(org) && secretKey.equals(objsecretKey)) {
				try {
					System.out.println(stardate + endDate);
					hasExpiredmessage(stardate, endDate);
				} catch (InvalidLicensekeyException e) {
					licensestatus = false;
					String message = e.getMessage();
					String[] message1 = message.split(":");
					licensestatus = false;
					Thread.sleep(3000);
					Thread t = new Thread(new Runnable() {
						public void run() {
							JOptionPane.showMessageDialog(null,
									message1[0] + ". Please contact QE platform support : noskript_support@ust.com",
									"License key", JOptionPane.PLAIN_MESSAGE);
						}
					});
					// t.run();
					t.start();
					// JOptionPane.showMessageDialog(null, message1[0] + message1[1] + ".Please
					// contact service admin", "License key", JOptionPane.PLAIN_MESSAGE);
					// JOptionPane.showMessageDialog(null, message1[0] + ". Please contact QE
					// platform support : noskript_support@ust.com", "License key",
					// JOptionPane.PLAIN_MESSAGE);
				}
			} else {

				System.out.println("License key validation failed");
			}
		}
		return licensestatus;

	}

	@SuppressWarnings("unused")
	public void hasExpiredmessage(String stardate, String endDate) throws InvalidLicensekeyException, ParseException {
		String message = "";
		if (hasExpired(stardate, endDate)) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			message = "License Key expired: " + endDate;
			throw new InvalidLicensekeyException(message);
		}

	}

	public boolean hasExpired(String stardate, String endDate) throws ParseException {
		Boolean expired = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date current = new Date();
		String currentsys = dateFormat.format(current);
		Date currentsystemdate = new SimpleDateFormat("yyyy/MM/dd").parse(currentsys);

		String [] date1=  endDate.split("/");
		
		Date prev = getDate(Integer.valueOf(date1[2]),Integer.valueOf(date1[0]),Integer.valueOf(date1[1]));
				
		String expirydate = dateFormat.format(prev);
		Date expirydate1 = new SimpleDateFormat("yyyy/MM/dd").parse(expirydate);
		if (expirydate1.before(currentsystemdate)) {
			return expired = true;
		}
		return expired;
	}
	
	private Date getDate(int year,int month,int date){
	    Calendar cal = Calendar.getInstance();
	    cal.clear();
	    cal.set(Calendar.YEAR, year);
	    cal.set(Calendar.MONTH, month-1);
	    cal.set(Calendar.DAY_OF_MONTH, date);
	    return cal.getTime();
	}

}
