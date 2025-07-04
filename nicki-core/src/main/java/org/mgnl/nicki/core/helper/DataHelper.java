
package org.mgnl.nicki.core.helper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/*-
 * #%L
 * nicki-core
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.json.JsonArray;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.objects.DynamicObject;

import lombok.extern.slf4j.Slf4j;


/**
 * The Class DataHelper.
 */
@Slf4j
public class DataHelper {

	/** The Constant FORMAT_DAY. */
	public final static String FORMAT_DAY = "yyyyMMdd";
	
	/** The Constant FORMAT_DISPLAY_DAY. */
	public final static String FORMAT_DISPLAY_DAY = "dd.MM.yyyy";
	
	/** The Constant FORMAT_TIME. */
	// 20091030115321
	public final static String FORMAT_TIME = "yyyyMMddHHmmss";
	
	/** The Constant FORMAT_MILLI. */
	public final static String FORMAT_MILLI = "dd.MM.yyyy HH:mm:ss:SSS";

//	public final static String FORMAT_MILLI = "yyyyMMddHHmmssSSS";
	
	/** The Constant FORMAT_TIMESTAMP. */
public final static String FORMAT_TIMESTAMP = "yyyy-MM-dd HH:mm:ss Z";
	
	/** The Constant FORMAT_GERMAN_TIMESTAMP. */
	public final static String FORMAT_GERMAN_TIMESTAMP = "dd.MM.yyyy HH:mm:ss";

	/**
	 * Gets the local date.
	 *
	 * @param date the date
	 * @return the local date
	 */
	public static LocalDate getLocalDate(Date date) {
		if (date != null) {
			return date.toInstant()
				      .atZone(ZoneId.systemDefault())
				      .toLocalDate();
		} else {
			return null;
		}
	}

	/**
	 * Gets the date.
	 *
	 * @param localDate the local date
	 * @return the date
	 */
	public static Date getDate(LocalDate localDate) {
		if (localDate != null) {
			return Date.from(localDate.atStartOfDay()
				      .atZone(ZoneId.systemDefault())
				      .toInstant());
		} else {
			return null;
		}
	}

	/**
	 * Gets the date.
	 *
	 * @param localDateTime the local date time
	 * @return the date
	 */
	public static Date getDate(LocalDateTime localDateTime) {
		if (localDateTime != null) {
			return Date.from(localDateTime
				      .atZone(ZoneId.systemDefault())
				      .toInstant());
		} else {
			return null;
		}
	}
	
	/**
	 * Gets the list.
	 *
	 * @param <T> the generic type
	 * @param entries the entries
	 * @return the list
	 */
	public static <T> List<T> getList (@SuppressWarnings("unchecked") T ... entries) {
		List<T> list = new ArrayList<T>();
		if (entries != null) {
			for (T entry : entries) {
				list.add(entry);
			}
		}
		return list;
	}
	
	/**
	 * Compare to.
	 *
	 * @param date1 the date 1
	 * @param date2 the date 2
	 * @return the int
	 */
	public static int compareTo(Date date1, Date date2) {
		if (date1 == null && date2 == null) {
			return 0;
		} else if (date1 == null) {
			return -1;
		} else if (date2 == null) {
			return 1;
		} else {
			return date1.compareTo(date2);
		}
	}
        

	/**
	 * Extracts an Integer from a String.
	 *
	 * @param stringValue String containing the Integer
	 * @param defaultValue default value if parsing does not work
	 * @return the Integer
	 */
	public static int getInteger(String stringValue, int defaultValue) {
		stringValue = StringUtils.strip(stringValue);
		if (StringUtils.isNotEmpty(stringValue)) {
			try {
				return Integer.parseInt(stringValue);
			} catch (Exception e) {
				log.debug("Error parsing " + stringValue);
			}
		}
		return defaultValue;
	}
	
	/**
	 * Extracts an Long from a String.
	 *
	 * @param stringValue String containing the Long
	 * @param defaultValue default value if parsing does not work
	 * @return the Long
	 */
	public static long getLong(String stringValue, long defaultValue) {
		stringValue = StringUtils.strip(stringValue);
		if (StringUtils.isNotEmpty(stringValue)) {
			try {
				return Long.parseLong(stringValue);
			} catch (Exception e) {
				log.debug("Error parsing " + stringValue);
			}
		}
		return defaultValue;
	}

	/**
	 * Makes a boolean out of a String. The value is true if the String is: "J", "Y", "1", or "TRUE" (case insensitive)
	 * @param value String to read
	 * @return the boolean value
	 */
	public static boolean booleanOf(String value) {

		if (StringUtils.equalsIgnoreCase(value, "J")
				|| StringUtils.equalsIgnoreCase(value, "Ja")
				|| StringUtils.equalsIgnoreCase(value, "Y")
				|| StringUtils.equalsIgnoreCase(value, "YES")
				|| StringUtils.equalsIgnoreCase(value, "1")
				|| StringUtils.equalsIgnoreCase(value, "TRUE")
				) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the first non blank entry of the String array.
	 * @param strings the String array
	 * @return the first non empty entry
	 */
	public static String getValue(String[] strings) {
		if (strings == null) {
			return null;
		}
		for (String entry : strings) {
			if (StringUtils.isNotBlank(entry)) {
				return entry;
			}
		}
		return null;
	}

	/**
	 * Checks if all entries of the String array are blank.
	 * @param strings the String array
	 * @return boolean
	 */
	public static boolean isEmpty(String[] strings) {
		if (strings == null) {
			return true;
		}
		for (String entry : strings) {
			if (StringUtils.isNotBlank(entry)) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Checks if not all entries of the String array are blank.
	 * @param strings the String array
	 * @return boolean
	 */
	public static boolean isNotEmpty(String[] strings) {
		return !isEmpty(strings);
	}

	/**
	 * Simplifies a Map<String, String[]> by using only the first value of the String Arrays.
	 *
	 * @param pMap Map<String, String[]>
	 * @return Map<String, String>
	 */
	public static Map<String, String> getSimpleMap(Map<String, String[]> pMap) {
		Map<String, String> map = new HashMap<String, String>();
		for (String key : pMap.keySet()) {
			if (isNotEmpty(pMap.get(key))) {
				map.put(key, getValue(pMap.get(key)));
			}
		}
		return map;
	}

	/**
	 * Removes all "'" and removes control characters (char <= 32) from both ends of this String.
	 *
	 * @param text  the String to be trimmed, may be null
	 * @return the trimmed string, null if null String input
	 */
	public static String cleanup(String text) {
		return StringUtils.trim(StringUtils.remove(text, "'"));
	}
	
	/**
	 * Splits a String into a String array. Other than String.split each delimiter is recognized, so that empty values are possible / allowed.
	 * <br/><br/>
	 * Input: "1|2||3|4|5" with delimiter "|" results in {"1", "2", "", "3", "4", "5"}
	 * @param data input String
	 * @param separator delimiter
	 * @return String array
	 */
	public static String[] toStringArray(String data, String separator) {
		return getList(data, separator).toArray(new String[0]);
	}
	
	/**
	 * Splits a String into a List<String>. Other than String.split each delimiter is recognized, so that empty values are possible / allowed.
	 * <br/><br/>
	 * Input: "1|2||3|4|5" with delimiter "|" results in {"1", "2", "", "3", "4", "5"}
	 *
	 * @param dataAsString the data as string
	 * @param separator delimiter
	 * @return List<String> (not null)
	 */
	public static List<String> getList(String dataAsString, String separator) {
		String data = dataAsString;
		List<String> values = new ArrayList<>();
		while (StringUtils.contains(data, separator)) {
			values.add(StringUtils.trimToEmpty(StringUtils.substringBefore(data, separator)));
			data = StringUtils.substringAfter(data, separator);
		}
		if (StringUtils.isNotBlank(data)) {
			values.add(StringUtils.trimToEmpty(data));
		}

		return values;
	}

	/**
	 * Gets the map.
	 *
	 * @param data the data
	 * @param entrySeparator the entry separator
	 * @param valueSeparator the value separator
	 * @return the map
	 */
	public static Map<String, String> getMap(String data, String entrySeparator, String valueSeparator) {
		if (StringUtils.isEmpty(data)) {
			return new HashMap<String, String>();
		}
		List<String> list = getList(data, entrySeparator);
		Map<String, String> map = new HashMap<String, String>();
		for (String string : list) {
			try {
				String entry[] = StringUtils.split(string, valueSeparator);
				if (entry != null && entry.length > 1) {
					map.put(entry[0], entry[1]);
				}
			} catch (Exception e) {
				log.error("Error", e);
			}
			
		}
		return map;
	}
	
	/**
	 * Gets the as string.
	 *
	 * @param list the list
	 * @param separator the separator
	 * @return the as string
	 */
	public static String getAsString(Collection<String> list, String separator) {
		StringBuilder sb = new StringBuilder();
		if (list != null) {
			for (String string : list) {
				if (sb.length() > 0) {
					sb.append(separator);
				}
				sb.append(string);
			}
		}
		return sb.toString();
	}

	/**
	 * Gets the password.
	 *
	 * @param string the string
	 * @return the password
	 */
	public static String getPassword(String string) {
		try {
			if (StringUtils.startsWith(string, "!")) {
				return new String(Base64.decodeBase64(StringUtils.substringAfter(string, "!").getBytes()));
			}
		} catch (Exception e) {
			log.error("Error", e);
		}
		return string;
	}

	/** The Constant CONFIG_PATTERN_STRING. */
	public static final String CONFIG_PATTERN_STRING = "\\%\\{(.*?)\\}";
	
	/** The Constant CONFIG_PATTERN. */
	public static final Pattern CONFIG_PATTERN = Pattern.compile(CONFIG_PATTERN_STRING);
	
	/** The Constant PATTERN_STRING. */
	public static final String PATTERN_STRING = "\\$\\{(.*?)\\}";
	
	/** The Constant PATTERN. */
	public static final Pattern PATTERN = Pattern.compile(PATTERN_STRING);
	
	/** The Constant EXEC_PATTERN_STRING. */
	public static final String EXEC_PATTERN_STRING = "\\#\\{(.*?)\\}";
	
	/** The Constant EXEC_PATTERN. */
	public static final Pattern EXEC_PATTERN = Pattern.compile(EXEC_PATTERN_STRING);
	
	/**
	 * Translate.
	 *
	 * @param text the text
	 * @return the string
	 */
	public static String translate(String text) {
		String result = text;
		
		while (result != null) {
			Matcher matcher = PATTERN.matcher(result);
			if (matcher.find()) {
				String name = matcher.group(1);
				String value = Environment.getProperty(name);
				if (StringUtils.isNotBlank(value)) {
					result = StringUtils.replace(result, "${" + name + "}", value);
					continue;
				} else {
					result = StringUtils.replace(result, "${" + name + "}", name);
				}
			} else {
				break;
			}
		}
		
		while (result != null) {
			Matcher matcher = CONFIG_PATTERN.matcher(result);
			if (matcher.find()) {
				String name = matcher.group(1);
				String value = Config.getString(name);
				if (StringUtils.isNotBlank(value)) {
					result = StringUtils.replace(result, "%{" + name + "}", value);
					continue;
				} else {
					result = StringUtils.replace(result, "%{" + name + "}", name);
				}
			} else {
				break;
			}
		}

		
		while (result != null) {
			Matcher matcher = EXEC_PATTERN.matcher(result);
			if (matcher.find()) {
				String name = matcher.group(1);
				String value = executeCommand(name);
				if (StringUtils.isNotBlank(value)) {
					result = StringUtils.replace(result, "#{" + name + "}", value);
					continue;
				} else {
					result = StringUtils.replace(result, "#{" + name + "}", name);
				}
			} else {
				break;
			}
		}
		return result;
	}
	
	/**
	 * Execute command.
	 *
	 * @param command the command
	 * @return the string
	 */
	public static String executeCommand(String command) {

		StringBuilder output = new StringBuilder();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				if (output.length() > 0) {
					output.append("\n");
				}
				output.append(line);
			}

		} catch (Exception e) {
			log.error("Error executing command " + command, e);
		}

		return output.toString();

	}
	
	/**
	 * Translate.
	 *
	 * @param text the text
	 * @param parameterMap the parameter map
	 * @return the string
	 */
	public static String translate(String text, Map<String, String> parameterMap) {
		String result = text;
		
		while (result != null) {
			Matcher matcher = PATTERN.matcher(result);
			if (matcher.find()) {
				String name = matcher.group(1);
				String value = parameterMap.get(name);
				if (StringUtils.isNotBlank(value)) {
					result = StringUtils.replace(result, "${" + name + "}", value);
					continue;
				} else {
					result = StringUtils.replace(result, "${" + name + "}", name);
				}
			} else {
				break;
			}
		}
		
		while (result != null) {
			Matcher matcher = CONFIG_PATTERN.matcher(result);
			if (matcher.find()) {
				String name = matcher.group(1);
				String value = Config.getString(name);
				if (StringUtils.isNotBlank(value)) {
					result = StringUtils.replace(result, "%{" + name + "}", value);
					continue;
				} else {
					result = StringUtils.replace(result, "%{" + name + "}", name);
				}
			} else {
				break;
			}
		}
		return result;
	}
	
	/**
	 * Translate.
	 *
	 * @param text the text
	 * @param defaultValue the default value
	 * @return the string
	 */
	@Deprecated
	public static String translate(String text, String defaultValue) {
		String result = text;
		
		while (result != null) {
			Matcher matcher = PATTERN.matcher(result);
			if (matcher.find()) {
				String name = matcher.group(1);
				String value = Environment.getProperty(name);
				if (StringUtils.isNotBlank(value)) {
					result = StringUtils.replace(result, "${" + name + "}", value);
					continue;
				}
				result = StringUtils.replace(result, "${" + name + "}", defaultValue);
			} else {
				break;
			}
		}
		return result;
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

	/**
	 * Gets the day.
	 *
	 * @param value the value
	 * @return the day
	 */
	// FORMAT_DAY = "yyyyMMdd"
	public static String getDay(Date value) {
		return new SimpleDateFormat(FORMAT_DAY).format(value);
	}

	/**
	 * Date from string.
	 *
	 * @param stored the stored
	 * @return the date
	 * @throws ParseException the parse exception
	 */
	public static Date dateFromString(String stored) throws ParseException {
		return new SimpleDateFormat(FORMAT_DAY).parse(stored);
	}

	/**
	 * Gets the milli.
	 *
	 * @param value the value
	 * @return the milli
	 */
	// FORMAT_MILLI = "dd.MM.yyyy HH:mm:ss:SSS"
	public static String getMilli(Date value) {
		return new SimpleDateFormat(FORMAT_MILLI).format(value);
	}

	/**
	 * Milli from string.
	 *
	 * @param stored the stored
	 * @return the date
	 * @throws ParseException the parse exception
	 */
	public static Date milliFromString(String stored) throws ParseException {
		return new SimpleDateFormat(FORMAT_MILLI).parse(stored);
	}

	/**
	 * Gets the display day.
	 *
	 * @param value the value
	 * @return the display day
	 */
	// FORMAT_DISPLAY_DAY = "dd.MM.yyyy"
	public static String getDisplayDay(Date value) {
		return new SimpleDateFormat(FORMAT_DISPLAY_DAY).format(value);
	}

	/**
	 * Date from display day.
	 *
	 * @param stored the stored
	 * @return the date
	 * @throws ParseException the parse exception
	 */
	public static Date dateFromDisplayDay(String stored) throws ParseException {
		return new SimpleDateFormat(FORMAT_DISPLAY_DAY).parse(stored);
	}
	
	/**
	 * Gets the time.
	 *
	 * @param value the value
	 * @return the time
	 */
	// FORMAT_TIME = "yyyyMMddHHmmss"
	public static String getTime(Date value) {
		return new SimpleDateFormat(FORMAT_TIME).format(value);
	}

	/**
	 * Time from string.
	 *
	 * @param stored the stored
	 * @return the date
	 * @throws ParseException the parse exception
	 */
	public static Date timeFromString(String stored) throws ParseException {
		return new SimpleDateFormat(FORMAT_TIME).parse(stored);
	}
	
	/**
	 * Gets the timestamp.
	 *
	 * @param value the value
	 * @return the timestamp
	 */
	// FORMAT_TIMESTAMP = "yyyy-MM-dd HH:mm:ss Z"
	public static String getTimestamp(Date value) {
		return new SimpleDateFormat(FORMAT_TIMESTAMP).format(value);
	}

	/**
	 * Timestamp from string.
	 *
	 * @param stored the stored
	 * @return the date
	 * @throws ParseException the parse exception
	 */
	public static Date timestampFromString(String stored) throws ParseException {
		return new SimpleDateFormat(FORMAT_TIMESTAMP).parse(stored);
	}
	
	/**
	 * Gets the german timestamp.
	 *
	 * @param value the value
	 * @return the german timestamp
	 */
	// FORMAT_GERMAN_TIMESTAMP = "dd.MM.yyyy HH:mm:ss"
	public static String getGermanTimestamp(Date value) {
		return new SimpleDateFormat(FORMAT_GERMAN_TIMESTAMP).format(value);
	}

	/**
	 * German timestamp from string.
	 *
	 * @param stored the stored
	 * @return the date
	 * @throws ParseException the parse exception
	 */
	public static Date germanTimestampFromString(String stored) throws ParseException {
		return new SimpleDateFormat(FORMAT_GERMAN_TIMESTAMP).parse(stored);
	}

	
	/**
	 * Contains.
	 *
	 * @param <T> the generic type
	 * @param array the array
	 * @param entry the entry
	 * @return true, if successful
	 */
	public static <T> boolean contains(T[] array, T entry) {
		if (array != null) {
			for (T t : array) {
				if (t == entry) {
					return true;
				}
			}
		}
		return false;
		
	}

	/**
	 * Contains.
	 *
	 * @param list the list
	 * @param entry the entry
	 * @return true, if successful
	 */
	public static boolean contains(String[] list, String entry) {
		for (String listEntry : list) {
			if (StringUtils.equalsIgnoreCase(entry, listEntry)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Contains ignore case.
	 *
	 * @param list the list
	 * @param entry the entry
	 * @return true, if successful
	 */
	public static boolean containsIgnoreCase(String[] list, String entry) {
		if (list != null) {
			for (String listEntry : list) {
				if (StringUtils.equalsIgnoreCase(entry, listEntry)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Contains ignore case.
	 *
	 * @param list the list
	 * @param entry the entry
	 * @return true, if successful
	 */
	public static boolean containsIgnoreCase(Collection<String> list, String entry) {
		if (list != null) {
			for (String listEntry : list) {
				if (StringUtils.equalsIgnoreCase(entry, listEntry)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Contains.
	 *
	 * @param list the list
	 * @param entry the entry
	 * @return true, if successful
	 */
	public static boolean contains(String list, String entry) {
		if (list != null) {
			for (String listEntry : StringUtils.split(list)) {
				if (StringUtils.equals(entry, listEntry)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Adds the to string array.
	 *
	 * @param array the array
	 * @param value the value
	 * @return the string[]
	 */
	public static String[] addToStringArray(String[] array, String value) {
		if (array == null) {
			return new String[]{value};
		}
		List<String> list = Arrays.asList(array);
		if (!list.contains(value)) {
			list.add(value);
			return list.toArray(new String[0]);
		} else return array;
	}

	/**
	 * Contains.
	 *
	 * @param list the list
	 * @param value the value
	 * @return true, if successful
	 */
	public static boolean contains(Collection<String> list, String value) {
		return list != null && list.contains(value);
	}

	/**
	 * Adds the to list.
	 *
	 * @param <T> the generic type
	 * @param list the list
	 * @param value the value
	 * @return the collection
	 */
	public static <T> Collection<T> addToList(Collection<T> list, T value) {
		if (list == null) {
			list = new ArrayList<T>();
		}
		if (!list.contains(value)) {
			list.add(value);
		}
		return list;
	}
	
	/**
	 * In range.
	 *
	 * @param value the value
	 * @param from the from
	 * @param to the to
	 * @return true, if successful
	 */
	public static boolean inRange(int value, int from, int to) {
		if (value < from || value > to) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Gets the map.
	 *
	 * @param registerInfo the register info
	 * @param valueSeparator the value separator
	 * @return the map
	 */
	public static Map<String, String> getMap(JsonArray registerInfo, String valueSeparator) {
		Map<String, String> map = new HashMap<>();
		if (registerInfo != null) {
			for (JsonValue jsonValue : registerInfo) {
				JsonString jsonString = (JsonString) jsonValue;
				String [] value = StringUtils.split(jsonString.getString(), valueSeparator);
				if (value != null && value.length == 2) {
					map.put(value[0], value[1]);
				}
			}
		}
		return map;
	}
	
	/**
	 * Normalize.
	 *
	 * @param text the text
	 * @param transliterate the transliterate
	 * @return the string
	 */
	public static String normalize(String text, boolean transliterate) {
		if (text == null) {
			return null;
		} else {

			if (transliterate) {
				text = StringUtils.replace(text, "\u00c4", "AE");
				text = StringUtils.replace(text, "\u00e4", "ae");
				text = StringUtils.replace(text, "\u00d6", "OE");
				text = StringUtils.replace(text, "\u00f6", "oe");
				text = StringUtils.replace(text, "\u00dc", "UE");
				text = StringUtils.replace(text, "\u00fc", "ue");
				text = StringUtils.replace(text, "\u00df", "ss");
			} else {
				text = StringUtils.replace(text, "\u00df", "s");
			}
			text = java.text.Normalizer.normalize(text,	java.text.Normalizer.Form.NFD);
			text = text.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
			return StringUtils.lowerCase(text);
		}
	}

	/**
	 * Filter.
	 *
	 * @param <T> the generic type
	 * @param list the list
	 * @param clazz the clazz
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public static <T extends DynamicObject> List<DynamicObject> filter(List<DynamicObject> list, Class<T> clazz) {
		List<DynamicObject> result = new ArrayList<>();
		if (list != null) {
			for (DynamicObject entry : list) {
				if (clazz.isAssignableFrom(entry.getClass())) {
					result.add((T) entry);
				}
			}
		}
		return result;
	}
	
    /**
     * To byte array.
     *
     * @param obj the obj
     * @return the byte[]
     */
    public static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
        } catch (IOException e) {
        	log.error("Error serializing object: " + e.getMessage());
		} finally {
            if (oos != null) {
                try {
					oos.close();
				} catch (IOException e) {
		        	log.error("Error closing ObjectOutputStream", e);
				}
            }
            if (bos != null) {
                try {
					bos.close();
				} catch (IOException e) {
		        	log.error("Error closing ByteArrayOutputStream", e);
				}
            }
        }
        return bytes;
    }

    /**
     * To object.
     *
     * @param bytes the bytes
     * @return the object
     */
    public static Object toObject(byte[] bytes) {
        Object obj = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            obj = ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
        	log.error("Error creating object from ByteArray", e);
		} finally {
            if (bis != null) {
                try {
					bis.close();
				} catch (IOException e) {
		        	log.error("Error closing ByteArrayInputStream", e);
				}
            }
            if (ois != null) {
                try {
					ois.close();
				} catch (IOException e) {
		        	log.error("Error closing ObjectInputStream", e);
				}
            }
        }
        return obj;
    }
}
