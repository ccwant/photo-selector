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
package com.ccwant.photo.selector.adapter;


import java.util.List;

import com.ccwant.album.R;
import com.ccwant.photo.selector.bean.CCwantAlbum;
import com.ccwant.photo.selector.load.CCwantImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CCwantSelectAlbumAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater inflater;
	List<CCwantAlbum> mData;
	
	public CCwantSelectAlbumAdapter(Context context, List<CCwantAlbum> list) {
		mContext = context;
		inflater = LayoutInflater.from(context);
		mData = list;
		
	}
	public int getCount() {
		return mData.size();
	}
	public Object getItem(int arg0) {

		return null;
	}

	public long getItemId(int arg0) {

		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final int coord = position;
		ViewHolder holder = null;
		if (convertView == null) {

			convertView = inflater.inflate(R.layout.ccwant_item_select_album, parent,
					false);
			holder = new ViewHolder();
			holder.imgThumnail = (ImageView) convertView.findViewById(R.id.ccwant_img_thumbnail_album_item_ccwant);
			holder.txtName = (TextView) convertView.findViewById(R.id.ccwant_txt_name_album_item_ccwant);
			holder.txtCount = (TextView) convertView.findViewById(R.id.ccwant_txt_count_album_item_ccwant);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final CCwantAlbum album=mData.get(position);
		final String path=album.mPhotoList.get(0).photoPath;

		CCwantImageLoader.getIntance(mContext).ImageLoader(path, holder.imgThumnail);
		holder.txtName.setText(album.mName);
		holder.txtCount.setText("共"+album.mCount+"项");

		return convertView;
	}

	public class ViewHolder {
		ImageView imgThumnail;
		TextView txtName;
		TextView txtCount;
	}

	

}
