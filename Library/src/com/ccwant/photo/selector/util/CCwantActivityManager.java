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
import java.util.LinkedList;  
import java.util.List;  
  
import android.app.Activity;  

public class CCwantActivityManager {  
  
    private List<Activity> activityList = new LinkedList<Activity>();  
    private static CCwantActivityManager instance;  
  
    private CCwantActivityManager() {  
    }  
  
   
    public static CCwantActivityManager getInstance() {  
        if (null == instance) {  
            instance = new CCwantActivityManager();  
        }  
        return instance;  
    }  
  
  
    public void addActivity(Activity activity) {  
        activityList.add(activity);  
    }  
  
  
    public void exit() {  
        for (Activity activity : activityList) {  
            activity.finish();  
        }  
    }  
    public void removeActivity(Activity activity)
    {
        for (Activity ac : activityList) {
            if(ac.equals(activity))
            {
                activityList.remove(ac);
                break;
            }
        }
            
    }
} 