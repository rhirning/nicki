package org.mgnl.nicki.vaadin.base.helper;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.mgnl.nicki.core.helper.DataHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.converter.Converter;

public class DateToStringConverter implements Converter<String, Date> {
	private static final long serialVersionUID = 1610290861847281094L;
	private static final Logger LOG = LoggerFactory.getLogger(DateToStringConverter.class);

	@Override
	public Date convertToModel(String value,
			Class<? extends Date> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		try {
			return DataHelper.dateFromDisplayDay(value);
		} catch (ParseException e) {
			LOG.debug("Error parsing string to date", e);
		}
		return null;
	}

	@Override
	public String convertToPresentation(Date value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value != null) {
			return DataHelper.getDisplayDay(value);
		} else {
			return "";
		}
	}

	@Override
	public Class<Date> getModelType() {
		return Date.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}




}
