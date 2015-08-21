/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich, Daniel Martí
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
package com.ccwant.photo.selector.load.display;


import com.ccwant.photo.selector.load.assist.CCwantLoadedFrom;
import com.ccwant.photo.selector.load.imageware.CCwantImageAware;

import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;


/**
 * Displays image with "fade in" animation
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com), Daniel Martí
 * @since 1.6.4
 */
public class CCwantFadeInBitmapDisplayer implements CCwantBitmapDisplayer {

	private final int durationMillis;

	private final boolean animateFromNetwork;
	private final boolean animateFromDisk;
	private final boolean animateFromMemory;

	/**
	 * @param durationMillis Duration of "fade-in" animation (in milliseconds)
	 */
	public CCwantFadeInBitmapDisplayer(int durationMillis) {
		this(durationMillis, true, true, true);
	}

	/**
	 * @param durationMillis     Duration of "fade-in" animation (in milliseconds)
	 * @param animateFromNetwork Whether animation should be played if image is loaded from network
	 * @param animateFromDisk    Whether animation should be played if image is loaded from disk cache
	 * @param animateFromMemory  Whether animation should be played if image is loaded from memory cache
	 */
	public CCwantFadeInBitmapDisplayer(int durationMillis, boolean animateFromNetwork, boolean animateFromDisk,
								 boolean animateFromMemory) {
		this.durationMillis = durationMillis;
		this.animateFromNetwork = animateFromNetwork;
		this.animateFromDisk = animateFromDisk;
		this.animateFromMemory = animateFromMemory;
	}

	@Override
	public void display(Bitmap bitmap, CCwantImageAware imageAware, CCwantLoadedFrom loadedFrom) {
		imageAware.setImageBitmap(bitmap);

		if ((animateFromNetwork && loadedFrom == CCwantLoadedFrom.NETWORK) ||
				(animateFromDisk && loadedFrom == CCwantLoadedFrom.DISC_CACHE) ||
				(animateFromMemory && loadedFrom == CCwantLoadedFrom.MEMORY_CACHE)) {
			animate(imageAware.getWrappedView(), durationMillis);
		}
	}

	/**
	 * Animates {@link ImageView} with "fade-in" effect
	 *
	 * @param imageView      {@link ImageView} which display image in
	 * @param durationMillis The length of the animation in milliseconds
	 */
	public static void animate(View imageView, int durationMillis) {
		if (imageView != null) {
			AlphaAnimation fadeImage = new AlphaAnimation(0, 1);
			fadeImage.setDuration(durationMillis);
			fadeImage.setInterpolator(new DecelerateInterpolator());
			imageView.startAnimation(fadeImage);
		}
	}
}
