package org.mgnl.nicki.pdf.engine;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

public class Point {
	private static final Logger log = LoggerFactory.getLogger(Point.class);
	
		float x, y;

		public Point(float x, float y) {
			this.x = x;
			this.y = y;
		}

		public Point(Point point) {
			this.x = point.x;
			this.y = point.y;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}

		public String toString() {
			return "POINT[x:" + x + ";y:" + y + "]";
		}
	
}
