package com.example.alumna.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.alumna.MyApplication;
import com.example.alumna.R;
import com.example.alumna.bean.UserBean;
import com.example.alumna.presenter.InformModifyPresenter;
import com.example.alumna.utils.Image.GlideImageLoader;
import com.example.alumna.view.Interface.InformModifyViewImpl;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/4/25.
 */

public class InformModifyActivity extends AppCompatActivity implements View.OnClickListener, InformModifyViewImpl {

    private InformModifyPresenter presenter;
    private CircleImageView head_view;
    private EditText name_text,school_text,grade_text,location_text,signature_text;
    private static final int HEAD_PICKER=0x01;

    private Toolbar toolbar;
    private Button backBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_inform);

        //findview
        head_view=(CircleImageView)findViewById(R.id.head_view);
        name_text=(EditText)findViewById(R.id.name_text) ;
        school_text=(EditText)findViewById(R.id.school_text);
        grade_text=(EditText)findViewById(R.id.grade_text);
        location_text=(EditText)findViewById(R.id.location_text);
        signature_text=(EditText)findViewById(R.id.signature_text);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        backBtn=(Button) findViewById(R.id.back_Btn) ;
        setSupportActionBar(toolbar);

        //set listener
        head_view.setOnClickListener(this);
        location_text.setOnClickListener(this);
        presenter = new InformModifyPresenter(this);


        //load
        presenter.loadImfor(MyApplication.getcurUser().getUid());

    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setSelectLimit(1);    //选中数量限制
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.head_view:
                initImagePicker();
                Intent intent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent, HEAD_PICKER);
                break;
            case R.id.location_text:
            default:break;
        }
    }

    @Override
    public void showImfor(UserBean user) {
        Glide.with(this).load(user.getHead()).into(head_view);
        name_text.setText(user.getUsername());

        school_text.setText(user.getSchool());
        grade_text.setText(user.getGrade());
        location_text.setText(user.getLocation());
        signature_text.setText(user.getSignature());
    }

    @Override
    public void saveImfor() {
        //gettext from editview
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == HEAD_PICKER) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                Glide.with(MyApplication.getContext()).
                        load(images.get(0).path).into(head_view);
                presenter.UploadImage(images);
            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            }
        }
    }
}