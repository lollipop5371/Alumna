package com.example.alumna.utils;

import com.example.alumna.bean.UserBean;

import java.util.ArrayList;

/**
 * Created by Leebobo on 2017/4/26.
 */

public class DataUtils {

   public static ArrayList<UserBean> list=new ArrayList<UserBean>();

    static {
        UserBean user=new UserBean(1);
        for(int i=0;i<5;++i){
            user.setUsername(i+"00");
            list.add(user);
        }
    }

    public static ArrayList<UserBean> getList(){
        return list;
    }

}
