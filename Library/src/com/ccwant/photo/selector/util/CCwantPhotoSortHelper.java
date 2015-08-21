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

import java.util.Comparator;
import java.util.HashMap;

import com.ccwant.photo.selector.bean.CCwantAlbum;
import com.ccwant.photo.selector.bean.CCwantPhoto;





/**
 * 相册排序助手
 * @author Administrator
 *
 */
public class CCwantPhotoSortHelper {

    public enum PhotoSortMethod {
        name,date,dateOrder
    }

    private PhotoSortMethod mSort;

    private boolean mFileFirst;

    private HashMap<PhotoSortMethod, Comparator> mComparatorList = new HashMap<PhotoSortMethod, Comparator>();

    public CCwantPhotoSortHelper() {
        mSort = PhotoSortMethod.name;
        mComparatorList.put(PhotoSortMethod.name, comparatorAlbumName);
        mComparatorList.put(PhotoSortMethod.date, comparatorAlbumDate);
        mComparatorList.put(PhotoSortMethod.dateOrder, comparatorAlbumDateOrder);
    }

    public void setSortMethog(PhotoSortMethod s) {
        mSort = s;
    }

    public PhotoSortMethod getAlbumSortMethod() {
        return mSort;
    }

    public void setFileFirst(boolean f) {
        mFileFirst = f;
    }

    public Comparator getComparator() {
        return mComparatorList.get(mSort);
    }

    private abstract class PhotoComparator implements Comparator<CCwantPhoto> {

        @Override
        public int compare(CCwantPhoto object1, CCwantPhoto object2) {
            
            return doCompare(object1, object2);
           

//            if (mFileFirst) {
//                // the files are listed before the dirs
//                return (object1.IsDir ? 1 : -1);
//            } else {
//                // the dir-s are listed before the files
//                return object1.IsDir ? -1 : 1;
//            }
        }

        protected abstract int doCompare(CCwantPhoto object1, CCwantPhoto object2);
    }

    private Comparator comparatorAlbumName = new PhotoComparator() {
        @Override
        public int doCompare(CCwantPhoto object1, CCwantPhoto object2) {
            return object1.photoName.compareToIgnoreCase(object2.photoName);
        }
    };

    private Comparator comparatorAlbumDate = new PhotoComparator() {
        @Override
        public int doCompare(CCwantPhoto object1, CCwantPhoto object2) {
            return object1.modifiedDate.compareToIgnoreCase(object2.modifiedDate);
        }
    };
    private Comparator comparatorAlbumDateOrder = new PhotoComparator() {
        @Override
        public int doCompare(CCwantPhoto object1, CCwantPhoto object2) {
            return object2.modifiedDate.compareToIgnoreCase(object1.modifiedDate);
        }
    };


    private int longToCompareInt(long result) {
        return result > 0 ? 1 : (result < 0 ? -1 : 0);
    }


}
