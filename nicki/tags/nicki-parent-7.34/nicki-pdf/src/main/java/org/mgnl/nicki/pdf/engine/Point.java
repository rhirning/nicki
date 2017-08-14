package org.mgnl.nicki.pdf.engine; 

public class Point {
	
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
