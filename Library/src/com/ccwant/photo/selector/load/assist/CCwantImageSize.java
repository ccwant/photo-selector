/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.ccwant.photo.selector.load.assist;

/**
 * Present width and height values
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public class CCwantImageSize {

	private static final int TO_STRING_MAX_LENGHT = 9; // "9999x9999".length()
	private static final String SEPARATOR = "x";

	private final int width;
	private final int height;

	public CCwantImageSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public CCwantImageSize(int width, int height, int rotation) {
		if (rotation % 180 == 0) {
			this.width = width;
			this.height = height;
		} else {
			this.width = height;
			this.height = width;
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/** Scales down dimensions in <b>sampleSize</b> times. Returns new object. */
	public CCwantImageSize scaleDown(int sampleSize) {
		return new CCwantImageSize(width / sampleSize, height / sampleSize);
	}

	/** Scales dimensions according to incoming scale. Returns new object. */
	public CCwantImageSize scale(float scale) {
		return new CCwantImageSize((int) (width * scale), (int) (height * scale));
	}

	@Override
	public String toString() {
		return new StringBuilder(TO_STRING_MAX_LENGHT).append(width).append(SEPARATOR).append(height).toString();
	}
}
