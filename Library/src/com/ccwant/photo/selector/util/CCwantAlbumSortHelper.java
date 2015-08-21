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





/**
 * 相册排序助手
 * @author Administrator
 *
 */
public class CCwantAlbumSortHelper {

    public enum AlbumSortMethod {
        name, size
    }

    private AlbumSortMethod mSort;

    private boolean mFileFirst;

    private HashMap<AlbumSortMethod, Comparator> mComparatorList = new HashMap<AlbumSortMethod, Comparator>();

    public CCwantAlbumSortHelper() {
        mSort = AlbumSortMethod.name;
        mComparatorList.put(AlbumSortMethod.name, comparatorAlbumName);
        mComparatorList.put(AlbumSortMethod.size, comparatorAlbumSize);
    }

    public void setSortMethog(AlbumSortMethod s) {
        mSort = s;
    }

    public AlbumSortMethod getAlbumSortMethod() {
        return mSort;
    }

    public void setFileFirst(boolean f) {
        mFileFirst = f;
    }

    public Comparator getComparator() {
        return mComparatorList.get(mSort);
    }

    private abstract class AlbumComparator implements Comparator<CCwantAlbum> {

        @Override
        public int compare(CCwantAlbum object1, CCwantAlbum object2) {
            
            return doCompare(object1, object2);
           

//            if (mFileFirst) {
//                // the files are listed before the dirs
//                return (object1.IsDir ? 1 : -1);
//            } else {
//                // the dir-s are listed before the files
//                return object1.IsDir ? -1 : 1;
//            }
        }

        protected abstract int doCompare(CCwantAlbum object1, CCwantAlbum object2);
    }

    private Comparator comparatorAlbumName = new AlbumComparator() {
        @Override
        public int doCompare(CCwantAlbum object1, CCwantAlbum object2) {
            return object1.mName.compareToIgnoreCase(object2.mName);
        }
    };

    private Comparator comparatorAlbumSize = new AlbumComparator() {
        @Override
        public int doCompare(CCwantAlbum object1, CCwantAlbum object2) {
            return longToCompareInt(object1.mCount - object2.mCount);
        }
    };


    private int longToCompareInt(long result) {
        return result > 0 ? 1 : (result < 0 ? -1 : 0);
    }


}
