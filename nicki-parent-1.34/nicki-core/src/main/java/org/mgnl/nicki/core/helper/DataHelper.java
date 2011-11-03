package org.mgnl.nicki.core.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

public class DataHelper {

	public final static String FORMAT_DAY = "yyyyMMdd";
	// 20091030115321
	public final static String FORMAT_TIME = "yyyyMMddHHmmss";
	public final static String FORMAT_MILLI = "yyyyMMddHHmmssSSS";
        
	public static SimpleDateFormat formatDay = new SimpleDateFormat(FORMAT_DAY);
	public static SimpleDateFormat formatTime = new SimpleDateFormat(FORMAT_TIME);
	public static SimpleDateFormat formatMilli = new SimpleDateFormat(FORMAT_MILLI);

	public static int getInteger(String stringValue, int defaultValue) {
		stringValue = StringUtils.strip(stringValue);
		if (StringUtils.isNotEmpty(stringValue) && StringUtils.isNumeric(stringValue)) {
			try {
				return Integer.parseInt(stringValue);
			} catch (Exception e) {
			}
		}
		return defaultValue;
	}

	public static boolean booleanOf(String value) {

		if (StringUtils.equalsIgnoreCase(value, "J")
				|| StringUtils.equalsIgnoreCase(value, "Y")
				|| StringUtils.equalsIgnoreCase(value, "1")
				|| StringUtils.equalsIgnoreCase(value, "TRUE")
				) {
			return true;
		}
		return false;
	}
	
	public static String getValue(String[] strings) {
		if (strings == null) {
			return null;
		}
		for (int i = 0; i < strings.length; i++) {
			if (StringUtils.isNotEmpty(strings[i])) {
				return strings[i];
			}
		}
		return null;
	}

	public static boolean isEmpty(String[] strings) {
		if (strings == null) {
			return true;
		}
		for (int i = 0; i < strings.length; i++) {
			if (StringUtils.isNotEmpty(strings[i])) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotEmpty(String[] strings) {
		return !isEmpty(strings);
	}

	public static Map<String, String> getSimpleMap(Map<String, String[]> pMap) {
		Map<String, String> map = new HashMap<String, String>();
		for (Iterator<String> iterator = pMap.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			if (isNotEmpty(pMap.get(key))) {
				map.put(key, getValue(pMap.get(key)));
			}
		}
		return map;
	}

	public static String cleanup(String text) {
		return StringUtils.trim(StringUtils.remove(text, "'"));
	}

	
	public static List<String> getList(String data, String separator) {
		return new ArrayList<String>(Arrays.asList(StringUtils.split(data, separator)));
	}

	public static Map<String, String> getMap(String data, String entrySeparator, String valueSeparator) {
		if (StringUtils.isEmpty(data)) {
			return new HashMap<String, String>();
		}
		List<String> list = getList(data, entrySeparator);
		Map<String, String> map = new HashMap<String, String>();
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			try {
				String entry[] = StringUtils.split(string, valueSeparator);
				map.put(entry[0], entry[1]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return map;
	}

	public static String getPassword(String string) {
		try {
			if (StringUtils.startsWith(string, "!")) {
				return new String(Base64.decodeBase64(StringUtils.substringAfter(string, "!").getBytes()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return string;
	}

	/**
	 * Checks if two lists have at least one common element.
	 * @param list1 separated String 
	 * @param list2 separated String
	 * @param separator Separator
	 * @return boolean
	 */
	public static boolean match(String list1, String list2, String separator) {
		if (StringUtils.isEmpty(list1) || StringUtils.isEmpty(list2)) {
			return false;
		}
		String elements1[] = StringUtils.split(list1, separator);
		String elements2[] = StringUtils.split(list2, separator);
		for (int i1 = 0; i1 < elements1.length; i1++) {
			for (int i2 = 0; i2 < elements2.length; i2++) {
				if (StringUtils.equals(elements1[i1], elements2[i2])) {
					return true;
				}
			}
		}
		return false;
	}

	public static String getDay(Date value) {
		return formatDay.format(value);
	}

	public static Date dateFromString(String stored) throws ParseException {
		// TODO Auto-generated method stub
		return formatDay.parse(stored);
	}
}
