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
package com.ccwant.photo.selector.load;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.ccwant.photo.selector.load.assist.CCwantLoadedFrom;
import com.ccwant.photo.selector.load.assist.CCwantQueueProcessingType;
import com.ccwant.photo.selector.load.display.CCwantFadeInBitmapDisplayer;
import com.ccwant.photo.selector.load.display.CCwantSimpleBitmapDisplayer;
import com.ccwant.photo.selector.load.imageware.CCwantImageAware;
import com.ccwant.photo.selector.load.imageware.CCwantImageViewAware;
import com.ccwant.photo.selector.util.CCwantFileManager;
import com.ccwant.photo.selector.util.CCwantPictureHelper;
import com.ccwant.photo.selector.util.CCwantThumbnailHelper;
import com.ccwant.photo.selector.util.MD5;

/**
 * 图片异步加载器
 * 此处参考开源项目Universal-Image-Loader
 * @author Administrator
 *
 */
public class CCwantImageLoader {
	private final static String TAG="CCwantImageLoader";
	
	private static CCwantImageLoader mImageLoader;
	
	private static Context mContext;
	//一级缓存的容量（20张图片）
	private static final int MAX_CAPACITY=20;
	
	/**
	 * 一级缓存：强引用缓存 （内存）抛出内存溢出异常时回收
	 * 20张图片（使用最新的20张图片）
	 * key图片地址value图片
	 * LinkedHashMap可以设置最大容量
	 * accessOrder true基于访问排序 false基于插入排序
	 * 访问排序-->LRU算法  近期最少使用算法
	 */
	private  LinkedHashMap<String, Bitmap> firstCacheMap=new LinkedHashMap<String, Bitmap>(MAX_CAPACITY,0.75f,true){
		//更具返回值移除map最老的值
		protected boolean removeEldestEntry(java.util.Map.Entry<String,Bitmap> eldest) {
			if(this.size()>MAX_CAPACITY){
				//加入二级缓存，同时加入本地缓存
				secondCacheMap.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
				//加入本地缓存
				setupDiskCache(eldest.getKey(),eldest.getValue());
				//diskCache(eldest.getKey(),eldest.getValue());
				//移除一级缓存
				return true;
			}
			return false;
		}
	};

	/**
	 * 二级缓存：软引用缓存（内存）内存不足时回收
	 * 线程安全
	 */
	private static ConcurrentHashMap<String , SoftReference<Bitmap>> secondCacheMap=new ConcurrentHashMap<String, SoftReference<Bitmap>>();
	
	//三级缓存：本地缓存（硬盘）
	
	
	public static CCwantImageLoader getIntance(Context context){
		if(mImageLoader==null){
			synchronized (CCwantImageLoader.class) {
				mImageLoader=new CCwantImageLoader(context);
			}
		}
		return mImageLoader;
	}
	

	public static final int DEFAULT_THREAD_POOL_SIZE = 3;
	public static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY - 2;
	public static final CCwantQueueProcessingType DEFAULT_TASK_PROCESSING_TYPE = CCwantQueueProcessingType.FIFO;
	
	private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
	private int threadPriority = DEFAULT_THREAD_PRIORITY;
	
	private CCwantQueueProcessingType tasksProcessingType = DEFAULT_TASK_PROCESSING_TYPE;
	//任务执行器
	private Executor taskExecutor;
	//缓存图像的任务执行器
	private Executor taskExecutorForCachedImages;
	// 线程池--任务分发器
	private Executor taskDistributor;
	
	
	private Handler mHandler=new Handler();
	
	
	public CCwantImageLoader(Context context){
		mContext=context;
		
		taskExecutorForCachedImages = CCwantDefaultConfigurationFactory.createExecutor(threadPoolSize, threadPriority, tasksProcessingType);
		taskDistributor = CCwantDefaultConfigurationFactory.createTaskDistributor();
	}

	/**
	 * 加载图片
	 * @param key 图片地址
	 * @param imageView 图片控件
	 */
	public void ImageLoader(String key,ImageView imageView){

		if(key==null || imageView==null){
			return;
		}
		imageView.setTag(key);
		DefaultPhotoDisplayer defaultPhotoDisplayer=new DefaultPhotoDisplayer(key,imageView);
		runTask(defaultPhotoDisplayer);
		ImageLoaderTask imageLoaderTask=new ImageLoaderTask(key,imageView);
		taskDistributor.execute(imageLoaderTask);
	
	}
	public void ImageLoader(String key,ImageView imageView,boolean isOnlyNetWorkLoad){

		if(key==null || imageView==null){
			return;
		}
		imageView.setTag(key);
		
		ImageLoaderTask imageLoaderTask=new ImageLoaderTask(key,imageView,isOnlyNetWorkLoad);
		taskDistributor.execute(imageLoaderTask);
	
	}


	
	/**
	 * 添加到一级缓存
	 * @param key
	 * @param result
	 */
	private void addFristCache(String key,Bitmap value) {
		
		if(value !=null){
			synchronized(firstCacheMap){
				firstCacheMap.put(key, value);
			}
		}
	}
	
	private void setupDiskCache(String key, Bitmap bmp){
		DiskCacheRunnable diskCacheRunnable=new DiskCacheRunnable(key, bmp);
		taskExecutorForCachedImages.execute(diskCacheRunnable);
	}
	
	/**
	 * 添加到三级缓存
	 * @param key 图片的路径会被当做图片的名称保存在硬盘上
	 * @param value 
	 */
	private static void diskCache(String key, Bitmap value) {

		//消息摘要算法Meaage Diagest Version 5(抗修改性，长度相同)
		
		String fileName=MD5.ecodeByMD5(key);
		String path=CCwantFileManager.getSaveFilePath(mContext)+File.separator+fileName;
		
		Log.d(TAG, "添加到本地缓存："+path);
		File file=new File(path);
		FileOutputStream fos=null;
		try {
			fos = new FileOutputStream(file);
			value.compress(Bitmap.CompressFormat.PNG, 100,fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			
			try {
				if(fos!=null){
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * 从缓存中读取图片
	 * @param path
	 * @return
	 */
	private Bitmap getFromCache(String key) {
		//从一级缓存加载
		synchronized (firstCacheMap) {
			Bitmap bitmap =firstCacheMap.get(key);
			//保持图片的refresh新鲜
			if(bitmap!=null && !bitmap.isRecycled()){
				//firstCacheMap.remove(bitmap);
				//firstCacheMap.put(key, bitmap);
				return bitmap;
			}
		}
		//从二级缓存加载
		SoftReference<Bitmap> softBitmap=secondCacheMap.get(key);
		if(softBitmap!=null){
			Bitmap bitmap =softBitmap.get();
			if(bitmap!=null && !bitmap.isRecycled()){
				firstCacheMap.put(key, bitmap);
				return bitmap;
			}
		}else{//软引用已经被回收,清除
			secondCacheMap.remove(key);
		}
		//从三级缓存加载
		Bitmap localBitmap=getFromLocal(key);
		if(localBitmap!=null && !localBitmap.isRecycled()){
			if(localBitmap==null){
				Log.e(TAG, "localBitmap---------------null");
			}
			if(key==null){
				Log.e(TAG, "key---------------null");
			}
			if(firstCacheMap==null){
				Log.e(TAG, "firstCacheMap---------------null");
			}

			firstCacheMap.put(key, localBitmap);
			return localBitmap;
		}
		return null;
	}
	
	
	/**
	 * 从本地缓存中读取
	 * @param key
	 * @return
	 */
	private Bitmap getFromLocal(String key) {

		String fileName=MD5.ecodeByMD5(key);
		if(fileName==null){
			return null;
		}
		String path=CCwantFileManager.getSaveFilePath(mContext)+File.separator+fileName;
		FileInputStream fis=null;
		try {
			fis = new FileInputStream(path);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			
		}finally{
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		return null;
	}
	private class ImageLoaderTask implements Runnable{

		private String key;
		private ImageView imageView;
		boolean loadHD;
		public ImageLoaderTask(String key,ImageView imageView){
			this.key=key;
			this.imageView=imageView;
		}
		public ImageLoaderTask(String key,ImageView imageView,boolean loadHD){
			this.key=key;
			this.imageView=imageView;
			this.loadHD=loadHD;
		}
		
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Bitmap mBitmap=null;
			if(loadHD){
			
				DownLoadHDRunnable downLoadHDRunnable= new DownLoadHDRunnable(key, imageView);
				taskDistributor.execute(downLoadHDRunnable);
			}else{
				DefaultPhotoDisplayer defaultPhotoDisplayer=new DefaultPhotoDisplayer(key,imageView);
				runTask(defaultPhotoDisplayer);
				//从缓存中读取图片
				mBitmap=getFromCache(key);
				if(mBitmap!=null){
					if(imageView.getTag().equals(key)){
						BitmapDisplayer bd=new BitmapDisplayer(key, mBitmap, imageView);
						runTask(bd);
					}
				}
				if(mBitmap==null){
					//从网络中读取图片
					//设置空白图片
					DownLoadRunnable p = new DownLoadRunnable(key, imageView);
					taskDistributor.execute(p);
				}
			}
			
		
			
			
		}
		
	}
	private void runTask(Runnable r){
		mHandler.post(r);
	}
	
	
	//加载网络图片线程
	private class DownLoadRunnable implements Runnable{
		
		private String key;
		private ImageView imageView;
		public DownLoadRunnable(String key,ImageView imageView){
			this.key=key;
			this.imageView=imageView;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Bitmap bmp =Download(key);
			BitmapDisplayer bd = new BitmapDisplayer(key,bmp, imageView);
			runTask(bd);

		}
		
	}

	private class DownLoadHDRunnable implements Runnable{
		
		private String key;
		private ImageView imageView;
		public DownLoadHDRunnable(String key,ImageView imageView){
			this.key=key;
			this.imageView=imageView;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Bitmap bmp =DownloadHD(key);
			BitmapDisplayer bd = new BitmapDisplayer(key,bmp, imageView);
			runTask(bd);

		}
		
	}
	// 用于在UI线程中更新界面
		private class DefaultPhotoDisplayer implements Runnable {
			private String key;
			ImageView imageView;
			public DefaultPhotoDisplayer(String key,ImageView imageView) {
				this.key = key;
				this.imageView = imageView;
			}

			public void run() {
				if (imageView.getTag() != null && imageView.getTag().equals(key)) {
				ColorDrawable drawable=new ColorDrawable(0xffdddddd);
				imageView.setImageDrawable(drawable);
				}

		}
	}

	// 用于在UI线程中更新界面
	private class BitmapDisplayer implements Runnable {
		private String key;
		Bitmap bitmap;
		ImageView imageView;

		public BitmapDisplayer(String key, Bitmap b, ImageView imageView) {
			this.key = key;
			bitmap = b;
			this.imageView = imageView;
		}

		public void run() {

			if (bitmap != null) {
				// 添加到一级缓存中去
				addFristCache(key, bitmap);
				if (imageView.getTag() != null && imageView.getTag().equals(key)) {

					CCwantImageAware imageAware = new CCwantImageViewAware(imageView);
					CCwantSimpleBitmapDisplayer display = new CCwantSimpleBitmapDisplayer();
					display.display(bitmap, imageAware,CCwantLoadedFrom.DISC_CACHE);
			
				}
			}

		}
	}

	//本地缓存的线程
	private class DiskCacheRunnable implements Runnable{
		
		private String key;
		private Bitmap mBitmap;
		public DiskCacheRunnable(String key,Bitmap bmp){
			this.key=key;
			this.mBitmap=bmp;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			diskCache(key, mBitmap);
		}
	}
	/**
	 * 下载图片
	 * 两种方式：
	 * 1.获取图片缩略图
	 * 2.获取apk的缩略图
	 * @param key
	 * @return
	 */
	private Bitmap Download(String key) {
		Bitmap result=null;
		result=CCwantThumbnailHelper.getThumbnail(mContext, key);
		return result;
	}
	private Bitmap DownloadHD(String key) {
		Bitmap result=null;
		result=CCwantPictureHelper.getPicture(mContext, key);
		return result;
	}
	

}
