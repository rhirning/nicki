
package org.mgnl.nicki.pdf.configuration;

/*-
 * #%L
 * nicki-pdf
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


import com.lowagie.text.Font;
import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Enum FontStyle.
 */
public enum FontStyle{
		
		/** The bold. */
		BOLD("bold", Font.BOLD),
		
		/** The normal. */
		NORMAL("normal", Font.NORMAL),
		
		/** The default. */
		DEFAULT("default", Font.NORMAL),
		
		/** The underlined. */
		UNDERLINED("underlined", Font.UNDERLINE);
		
		/** The style. */
		private int style;
		
		/**
		 * The Class font.
		 */
		static class font {
			
			/** The Constant MAP. */
			static final Map<String, FontStyle> MAP = new HashMap<String, FontStyle>();
		}
		
		/**
		 * Instantiates a new font style.
		 *
		 * @param name the name
		 * @param style the style
		 */
		FontStyle(String name, int style) {
			this.style = style;
			font.MAP.put(name, this);
		}
		
		/**
		 * Gets the font style.
		 *
		 * @return the font style
		 */
		public int getFontStyle() {
			return style;
		}
		
		/**
		 * By name.
		 *
		 * @param name the name
		 * @return the font style
		 */
		public static FontStyle byName(String name) {
			if(name instanceof String)
				return font.MAP.get(name.toLowerCase());
			else
				return null;
		}
	
}
