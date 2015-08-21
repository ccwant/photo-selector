/*******************************************************************************
 * Copyright 2015 CCwant
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
package com.ccwant.photo.selector.util;

import java.io.File;
import java.io.FileInputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CCwantPictureHelper {

	
	
	  public static Bitmap getPicture(Context context, String path) {
			try {
				File file = new File(path);
				FileInputStream fis = new FileInputStream(file);
				byte[] date = new byte[fis.available()];
				fis.read(date);
				return getBitmapByBytes(date);
			} catch (Exception e) {
				e.printStackTrace();

			}
			return null;
	    }
	  /** 
	     * 根据图片字节数组，对图片可能进行二次采样，不致于加载过大图片出现内存溢出 
	     * @param bytes 
	     * @return 
	     */  
	    public static Bitmap getBitmapByBytes(byte[] bytes){  
	          
	    	int IMAGE_MAX_HEIGHT=1500;
	    	int IMAGE_MAX_WIDTH=1500;
	        //对于图片的二次采样,主要得到图片的宽与高  
	        int width = 0;  
	        int height = 0;  
	        int sampleSize = 1; //默认缩放为1  
	        BitmapFactory.Options options = new BitmapFactory.Options();  
	        options.inJustDecodeBounds = true;  //仅仅解码边缘区域  
	        //如果指定了inJustDecodeBounds，decodeByteArray将返回为空  
	        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);  
	        //得到宽与高  
	        height = options.outHeight;  
	        width = options.outWidth;  
	      
	        //图片实际的宽与高，根据默认最大大小值，得到图片实际的缩放比例  
	        while ((height / sampleSize > IMAGE_MAX_HEIGHT)  
	                || (width / sampleSize > IMAGE_MAX_WIDTH)) {  
	            sampleSize *= 2;  
	        }  
	      
	        //不再只加载图片实际边缘  
	        options.inJustDecodeBounds = false;  
	        //并且制定缩放比例  
	        options.inSampleSize = sampleSize;  
	        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);  
	    }  
}
