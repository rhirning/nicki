
package org.mgnl.nicki.pdf.configuration;

import com.lowagie.text.Font;
import java.util.HashMap;
import java.util.Map;

public enum FontStyle{
		BOLD("bold", Font.BOLD),
		NORMAL("normal", Font.NORMAL),
		DEFAULT("default", Font.NORMAL),
		UNDERLINED("underlined", Font.UNDERLINE);
		
		private int style;
		static class font {
			static final Map<String, FontStyle> MAP = new HashMap<String, FontStyle>();
		}
		
		FontStyle(String name, int style) {
			this.style = style;
			font.MAP.put(name, this);
		}
		
		public int getFontStyle() {
			return style;
		}
		
		public static FontStyle byName(String name) {
			if(name instanceof String)
				return font.MAP.get(name.toLowerCase());
			else
				return null;
		}
	
}
