package org.mgnl.nicki.core.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.AttributeMapper;
import org.mgnl.nicki.core.helper.DataHelper;

public class GenericAttributeMapper  implements AttributeMapper{
	
	private Map<String, String> internalToExternal = new HashMap<String, String>();
	private Map<String, String> externalToInternal = new HashMap<String, String>();
	private Collection<String> hiddenInternalAttributes = new ArrayList<String>(); 
	private Collection<String> hiddenExternalAttributes = new ArrayList<String>();
	private Collection<String> externalAttributes = new ArrayList<String>();
	
	public GenericAttributeMapper(String pathToMap, String configBase) {
		Properties properties = new Properties() ;
		try {
			properties.load(GenericAttributeMapper.class.getResourceAsStream(pathToMap));
			for (Entry<Object, Object> entry : properties.entrySet()) {
				add((String) entry.getKey(), (String) entry.getValue());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<String> rawHiddenExternalAttributes = DataHelper.getList(Config.getProperty(configBase + ".attributes.hidden"), ",");
		for (String attribute : rawHiddenExternalAttributes) {
			String internal = toInternalName(attribute);
			String external = toExternalName(internal);
			hiddenInternalAttributes.add(internal);
			hiddenExternalAttributes.add(external);
		}
		externalAttributes.addAll(internalToExternal.values());
		List<String> toBeRemoved = new ArrayList<String>();
		for (String key : externalAttributes) {
			if (hiddenExternalAttributes.contains(key)) {
				toBeRemoved.add(key);
			}
		}
		
		for (String string : toBeRemoved) {
			externalAttributes.remove(string);
		}
		
		
	}

	public String correctExternal(String name) {
		return toExternal(toInternal(name));
	}
	
	public String[] getExternalAttributes() {
		return externalAttributes.toArray(new String[]{});
	}
	
	private void add(String object, String object2) {
		internalToExternal.put(object.toLowerCase(), object2);
		externalToInternal.put(object2.toLowerCase(), object);
	}
	
	@Override
	public boolean isHiddenInternal(String internal) {
		return hiddenExternalAttributes.contains(toExternal(internal));
	}

	@Override
	public boolean isHiddenExternal(String external) {
		return hiddenInternalAttributes.contains(toInternal(external));
	}

	@Override
	public String toExternal(String internal) {
		return toExternalName(internal);
	}

	@Override
	public String toInternal(String external) {
		return toInternalName(external);
	}

	private String toExternalName(String internal) {
		String key = StringUtils.trimToEmpty(internal).toLowerCase();
		if (internalToExternal.containsKey(key)) {
			return internalToExternal.get(key);
		} else {
			return null;
		}
	}

	private String toInternalName(String external) {
		String key = StringUtils.trimToEmpty(external).toLowerCase();
		if (externalToInternal.containsKey(key)) {
			return externalToInternal.get(key);
		} else {
			return null;
		}
	}

	@Override
	public boolean hasExternal(String external) {
		String key = StringUtils.trimToEmpty(external).toLowerCase();
		return externalToInternal.containsKey(key);
	}

	@Override
	public boolean hasInternal(String internal) {
		String key = StringUtils.trimToEmpty(internal).toLowerCase();
		return internalToExternal.containsKey(key);
	}

	@Override
	public boolean isStrict() {
		return true;
	}
}
