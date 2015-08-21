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
package com.ccwant.photo.selector.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ccwant.album.R;





import com.ccwant.photo.selector.adapter.CCwantSelectAlbumAdapter;
import com.ccwant.photo.selector.bean.CCwantAlbum;
import com.ccwant.photo.selector.bean.CCwantPhoto;
import com.ccwant.photo.selector.util.CCwantActivityManager;
import com.ccwant.photo.selector.util.CCwantAlbumHelper;
import com.ccwant.photo.selector.util.CCwantAlbumSortHelper;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 选择相册
 * @author CCwant by on 2015-6-12
 *
 */
public class CCwantSelectAlbumActivity extends Activity {

	List<CCwantAlbum> mData = new ArrayList<CCwantAlbum>();
	CCwantAlbumSortHelper mAlbumSortHelper;
	private ListView mLstContet;
	private CCwantSelectAlbumAdapter mAdapter;
	private ImageView mImgBack;
	private CCwantAlbumHelper mAlbumHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ccwant_activity_select_album);
		CCwantActivityManager.getInstance().addActivity(this);
		mAlbumSortHelper=new CCwantAlbumSortHelper();
		mAlbumHelper=new CCwantAlbumHelper();
		mAlbumHelper.init(this);
		mImgBack=(ImageView)findViewById(R.id.ccwnat_img_back_album);
		mImgBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent();
				setResult(RESULT_CANCELED, intent);
				finish();
			}
		});
		mLstContet = (ListView) findViewById(R.id.ccwant_lst_content_album);
		mAdapter = new CCwantSelectAlbumAdapter(this, mData);
		mLstContet.setAdapter(mAdapter);
		
		
		mLstContet.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CCwantSelectAlbumActivity.this,CCwantSelectPhotoActivity.class);
				intent.putExtra("CCwantAlbumName",(Serializable) mData.get(position).mName);
				intent.putExtra("CCwantPhotoList",(Serializable) mData.get(position).mPhotoList);
				startActivityForResult(intent,1);
			}
		});
		steupAlbumToListView();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		CCwantActivityManager.getInstance().removeActivity(this);
	}
	private void steupAlbumToListView(){
		List<CCwantAlbum> mlist=mAlbumHelper.getAlbum();
		Collections.sort(mlist, mAlbumSortHelper.getComparator());
		mData.clear();
		mData.addAll(mlist);
		mAdapter.notifyDataSetChanged();
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode==1){
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				ArrayList<String> list= bundle.getStringArrayList("result");
				
				Intent intent = new Intent();
				intent.putExtra("result",(Serializable)list);
				setResult(RESULT_OK, intent);
				finish();
			}	
		}
	}

	
	

}
