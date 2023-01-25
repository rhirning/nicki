package org.mgnl.nicki.core.helper;

/*-
 * #%L
 * nicki-core
 * %%
 * Copyright (C) 2017 - 2023 Ralf Hirning
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
