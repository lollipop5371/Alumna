package com.example.alumna.view.Interface;


import com.example.alumna.bean.UserBean;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/4/25.
 */

public interface InformModifyViewImpl {
    void showInform(UserBean user);

    /*读取所有editview的信息*/
    HashMap<String,Object> modifyInform();

    void showToast(String str);

    //为imageview设置tag
    void ImageViewSetTag(String url);
}
