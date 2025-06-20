
package org.mgnl.nicki.pdf.engine;


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


/**
 * The Class Point.
 */
public class Point {
	
		/** The y. */
		float x, y;

		/**
		 * Instantiates a new point.
		 *
		 * @param x the x
		 * @param y the y
		 */
		public Point(float x, float y) {
			this.x = x;
			this.y = y;
		}

		/**
		 * Instantiates a new point.
		 *
		 * @param point the point
		 */
		public Point(Point point) {
			this.x = point.x;
			this.y = point.y;
		}

		/**
		 * Gets the x.
		 *
		 * @return the x
		 */
		public float getX() {
			return x;
		}

		/**
		 * Gets the y.
		 *
		 * @return the y
		 */
		public float getY() {
			return y;
		}

		/**
		 * To string.
		 *
		 * @return the string
		 */
		public String toString() {
			return "POINT[x:" + x + ";y:" + y + "]";
		}
	
}
