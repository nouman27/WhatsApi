package com.nks.whatsapp;

import java.io.IOException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.nks.whatsapp.marketing.app.Constants;

public class Util {

	private static Random random = new Random((new Date()).getTime());
	private static BASE64Encoder encoder = new BASE64Encoder();
	private static BASE64Decoder decoder = new BASE64Decoder();

	public static String encrypt(String input) {
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		return encoder.encode(salt) + encoder.encode(input.getBytes());
	}

	public static String decrypt(String input) {
		if (input.length() > 12) {
			String cipher = input.substring(12);
			try {
				return new String(decoder.decodeBuffer(cipher));
			} catch (IOException e) {
			}
		}
		return null;
	}

	public static String getFriendlyDate(Date messageDate) {
		int days = daysBetween(new Date(), messageDate);
		if (days == 0)
			return "Today";
		if (days == 1)
			return "Yesterday";
		return Constants.getDateFormatter().format(messageDate);
	}

	public static int daysBetween(Date date1, Date date2) {
		Calendar c1 = DateToCalendar(date1);
		Calendar c2 = DateToCalendar(date2);
		c1.set(Calendar.HOUR_OF_DAY, 0);
		c1.set(Calendar.MINUTE, 0);
		c1.set(Calendar.SECOND, 0);
		c2.set(Calendar.HOUR_OF_DAY, 0);
		c2.set(Calendar.MINUTE, 0);
		c2.set(Calendar.SECOND, 0);
		long difference = c1.getTime().getTime() - c2.getTime().getTime();
		return (int) TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
	}

	public static int secondsBetween(Date date1, Date date2) {
		Calendar c1 = DateToCalendar(date1);
		Calendar c2 = DateToCalendar(date2);
		Calendar c3 = Calendar.getInstance();

		c1.set(Calendar.YEAR, c3.get(Calendar.YEAR));
		c1.set(Calendar.DAY_OF_YEAR, c3.get(Calendar.DAY_OF_YEAR));
		c2.set(Calendar.YEAR, c3.get(Calendar.YEAR));
		c2.set(Calendar.DAY_OF_YEAR, c3.get(Calendar.DAY_OF_YEAR));
		long difference = c1.getTime().getTime() - c2.getTime().getTime();
		return (int) TimeUnit.SECONDS
				.convert(difference, TimeUnit.MILLISECONDS);
	}

	public static Calendar DateToCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	public static Comparator<Message> getSortByTimeComparator(
			final boolean ascending) {
		return new Comparator<Message>() {
			@Override
			public int compare(Message o1, Message o2) {
				if (o1.getStatuses().size() == 0) {
					if (o2.getStatuses().size() == 0)
						return 0;
					else
						return ascending ? -1 : 1;
				}
				Date d1 = o1.getStatuses().get(0).getTime();
				Date d2 = o2.getStatuses().get(0).getTime();

				return ascending ? d2.compareTo(d1) : d1.compareTo(d2);

			}
		};
	}
}
