package org.mgnl.nicki.core.helper;

import java.util.Calendar;
import java.util.Date;

import org.mgnl.nicki.core.data.Period;
import org.mgnl.nicki.core.i18n.I18n;

public enum PERIOD {
	LAST_30 {
		@Override
		public Period getPeriod() {
			return lastDays(30);		}
	},
	LAST_90 {
		@Override
		public Period getPeriod() {
			return lastDays(90);		}
	},
	LAST_180 {
		@Override
		public Period getPeriod() {
			return lastDays(180);		}
	},
	LAST_360 {
		@Override
		public Period getPeriod() {
			return lastDays(360);		}
	},
	LAST_MONTH {
		@Override
		public Period getPeriod() {
			Calendar start = Period.getFirstDayOfMonth();
			start.add(Calendar.MONTH, -1);

			return new Period(start, Period.getFirstDayOfMonth());
		}
	},
	THIS_MONTH {
		@Override
		public Period getPeriod() {
			return new Period(Period.getFirstDayOfMonth(), Period.getLastDayOfMonth());
		}
	},
	LAST_YEAR {
		@Override
		public Period getPeriod() {
			Calendar start = Period.getFirstDayOfYear();
			start.add(Calendar.YEAR, -1);

			
			return new Period(start, Period.getFirstDayOfYear());
		}
	},
	THIS_YEAR {
		@Override
		public Period getPeriod() {
			return new Period(Period.getFirstDayOfYear(), Period.getLastDayOfYear());
		}
	},
	USER_DEFINED {
		@Override
		public Period getPeriod() {
			return null;
		}
	};
	
	public Calendar getStart() {
		return this.getPeriod().getStart();
	}

	public Calendar getEnd() {
		return this.getPeriod().getEnd();
	}

	public String getName() {
		return I18n.getText("nicki.period." + name());
	}

	public boolean matches(Date start) {
		return this.getPeriod().matches(start);
	}
	
	public Period lastDays(int days) {
		Calendar now = Calendar.getInstance();
		Calendar start = Calendar.getInstance();;
		start.add(Calendar.DAY_OF_MONTH, -1 * days);

		return new Period(start, now);
	}

	public abstract Period getPeriod();

}