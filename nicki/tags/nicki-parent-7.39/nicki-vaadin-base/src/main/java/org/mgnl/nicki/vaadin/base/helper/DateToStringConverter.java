package org.mgnl.nicki.vaadin.base.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.mgnl.nicki.core.helper.DataHelper;

import com.vaadin.data.util.converter.StringToDateConverter;

@SuppressWarnings("serial")
public class DateToStringConverter extends StringToDateConverter {

	@Override
	public DateFormat getFormat(Locale locale){
		return new SimpleDateFormat(DataHelper.FORMAT_DISPLAY_DAY);
	}

}
