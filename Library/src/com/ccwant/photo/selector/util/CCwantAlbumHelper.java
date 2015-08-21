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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.Media;

import com.ccwant.photo.selector.bean.CCwantAlbum;
import com.ccwant.photo.selector.bean.CCwantPhoto;

public class CCwantAlbumHelper {
	
	ContentResolver mContentResolver;
	public CCwantAlbumHelper(){
		
	}
	public void init(Context context){
		mContentResolver = context.getContentResolver();
	}
	/**
	 * 获得相册集
	 * @return
	 */
	public List<CCwantAlbum> getAlbum() {
		List<CCwantAlbum> data = new ArrayList<CCwantAlbum>();
		HashMap<String, CCwantAlbum> bucketList = new HashMap<String, CCwantAlbum>();
		String columns[] = new String[] { Media._ID, Media.BUCKET_ID,
				Media.PICASA_ID, Media.DATA, Media.DISPLAY_NAME, Media.TITLE,
				Media.SIZE, Media.BUCKET_DISPLAY_NAME,Media.DATE_MODIFIED };
		Cursor cursor = mContentResolver.query(Media.EXTERNAL_CONTENT_URI,
				columns, null, null, null);
		// 获取图片总数
		int totalNum = cursor.getCount();
		if (cursor.moveToFirst()) {
			do {
				//照片ID
				int _id = cursor.getInt(cursor.getColumnIndex(Media._ID));
				//照片路径
				String path = cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA));
				//照片大小
				String size = cursor.getString(cursor.getColumnIndexOrThrow(Media.SIZE));
				//照片名称
				String name = cursor.getString(cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME));
				
				
				//修改日期
				String modifiedDate= cursor.getString(cursor.getColumnIndexOrThrow(Media.DATE_MODIFIED));
				//相册名
				String bucketName = cursor.getString(cursor.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME));
				//相册ID
				String bucketId = cursor.getString(cursor.getColumnIndexOrThrow(Media.BUCKET_ID));
				
				// album.setName(bucketName);
				CCwantAlbum album = bucketList.get(bucketId);
				if (bucketList.get(bucketId) == null) {// 如果不存在这个相册,保存ID、及相册名称
					album = new CCwantAlbum();
					album.mName = bucketName;
					album.mCount++;
					//创建一个照片封面对象
					CCwantPhoto photo = new CCwantPhoto();
					photo.photoID = _id;
					photo.photoName = name;
					photo.photoPath = path;
					photo.modifiedDate=modifiedDate;
					
					
					//将照片封面加入到相册中
					album.mPhotoList.add(photo);
					bucketList.put(bucketId, album);
				} else {// 如果存在,将相册相片数量+1
					album.mCount++;
					//创建一个照片对象
					CCwantPhoto photo = new CCwantPhoto();
					photo.photoID = _id;
					photo.photoName = name;
					photo.photoPath = path;
					photo.modifiedDate=modifiedDate;
					//将照片加入到相册中
					album.mPhotoList.add(photo);
				}

			} while (cursor.moveToNext());
		}
		cursor.close();
		Iterator<Entry<String, CCwantAlbum>> itr = bucketList.entrySet()
				.iterator();
		while (itr.hasNext()) {
			Map.Entry<String, CCwantAlbum> entry = (Map.Entry<String, CCwantAlbum>) itr
					.next();
			data.add(entry.getValue());
		}
		return data;
	}

}
