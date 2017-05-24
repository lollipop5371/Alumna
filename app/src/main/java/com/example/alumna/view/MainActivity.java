package com.example.alumna.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.alumna.MyApplication;
import com.example.alumna.R;
import com.example.alumna.adapter.FriendAdapter;
import com.example.alumna.adapter.LeftDrawerAdapter;
import com.example.alumna.adapter.LeftDrawerAdapter.onItemClickListener;
import com.example.alumna.adapter.TopicListAdapter.FriendCircleAdapter;
import com.example.alumna.bean.LeftBean;
import com.example.alumna.bean.TopicBean;
import com.example.alumna.bean.UserBean;
import com.example.alumna.presenter.MainPresenter;
import com.example.alumna.utils.Image.ImageUtil;
import com.example.alumna.view.Interface.MainViewImpl;
import com.example.alumna.widgets.RecycleViewDivider;
import com.mingle.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainViewImpl {

    private RecyclerView mLeftRvView;
    private ArrayList<LeftBean> mLeftDatas;

    private RecyclerView topiclist;
    private FriendCircleAdapter adapter;

    private MainPresenter presenter;
    private Button publishBtn;

    private RecyclerView friendRV;


    private LinearLayout layoutCircle;
    private SwipeRefreshLayout refreshLayout;
    private Toolbar toolbar;
    private DrawerLayout drawer;

    //左侧菜单，右侧菜单
    private LinearLayout leftLayout;
    private RelativeLayout rightLayout;

    //左侧菜单头像
    private ImageView leftHeadView;

    private TextView nametv;

    //加载view
    private LoadingView loadingView;

    private final static boolean TYPE_TEXT=false;
    private static final boolean TYPE_IMAGE=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter=new MainPresenter(this);

        mLeftRvView = (RecyclerView) findViewById(R.id.main_left_recycler);
        topiclist = (RecyclerView) findViewById(R.id.topiclist);
        publishBtn=(Button)findViewById(R.id.publish_Btn) ;
        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.refresh_layout);
        layoutCircle = (LinearLayout) findViewById(R.id.friend_circle);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nametv=(TextView)findViewById(R.id.main_left_self_name) ;
        friendRV = (RecyclerView) findViewById(R.id.right_recycler);

        loadingView = (LoadingView) findViewById(R.id.loading_view);

        loadingView.setVisibility(View.VISIBLE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //listener
        leftLayout = (LinearLayout) findViewById(R.id.left_drawer_layout);
        leftHeadView = (ImageView) findViewById(R.id.left_head_view);
        rightLayout = (RelativeLayout) findViewById(R.id.right_drawer_layout);

        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,PublishActivity.class);
                i.putExtra("flag",TYPE_IMAGE);
                MainActivity.this.startActivity(i);
            }
        });

        publishBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent i=new Intent(MainActivity.this,PublishActivity.class);
                i.putExtra("flag",TYPE_TEXT);
                MainActivity.this.startActivity(i);
                return false;
            }
        });

        layoutCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,NearbyActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                presenter.loadTopicList(MyApplication.getcurUser().getUid());
            }
        });


        /**
         根据初始化左右侧滑菜单背景，菜单背景为头像的高斯模糊
         */


        nametv.setText(MyApplication.getcurUser().getUsername());

        Glide.with(this).load(MyApplication.getcurUser().getHead())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        leftHeadView.setImageBitmap(bitmap);
                        /*裁剪图片，适应侧滑菜单大小*/
                        Bitmap inputBitmap = ImageUtil.RGB565toARGB888(bitmap);
                        int width = inputBitmap.getWidth(),height = inputBitmap.getHeight();
                        Bitmap cropBitmap = Bitmap.createBitmap(inputBitmap,width/4,height/6,width/4*2,height/6*4);
                        /*模糊图片*/
                        Bitmap blurBitmap = ImageUtil.blurBitmap(getContext(), cropBitmap,3.0f);
                        Drawable outputDrawable = new BitmapDrawable(getResources(),blurBitmap);
                        /*加入灰色遮罩层，避免图片过亮影响其他控件*/
                        outputDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                        leftLayout.setBackground(outputDrawable);
                        rightLayout.setBackground(outputDrawable.getConstantState().newDrawable());

                    }
                });



        /**
         * 初始化左侧滑菜单Item
         */
        loadLeftDatas();
        LeftDrawerAdapter leftAdapter = new LeftDrawerAdapter(mLeftDatas);
        mLeftRvView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mLeftRvView.setAdapter(leftAdapter);

        leftAdapter.setListener(new onItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                switch (position){
                    case 0:
                        Intent i=new Intent(MainActivity.this,MemberActivity.class);
                        i.putExtra("uid",""+MyApplication.getcurUser().getUid());
                        MainActivity.this.startActivity(i);
                        break;
                    case 1:
                        i=new Intent(MainActivity.this,InformModifyActivity.class);
                        MainActivity.this.startActivity(i);
                        break;
                    case 2:
                        break;
                    default:break;
                }
            }
        });


        /**
         初始化朋友圈
         */
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        topiclist.setLayoutManager(layoutManager);
        presenter.loadTopicList(MyApplication.getcurUser().getUid());

        /**
         * 初始化好友列表
         */
        friendRV.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        friendRV.addItemDecoration(new RecycleViewDivider(this,LinearLayoutManager.VERTICAL,3,getResources().getColor(R.color.grayDivider)));
    }

    @Override
    protected void onResume() {
        presenter.loadFriendList(MyApplication.getcurUser().getUid());
        super.onResume();
    }

    private void loadLeftDatas() {
        mLeftDatas = new ArrayList<>();
        LeftBean data = new LeftBean(R.drawable.ic_menu_friend,"朋友圈");
        mLeftDatas.add(data);

        data = new LeftBean(R.drawable.ic_menu_setting,"修改个人信息");
        mLeftDatas.add(data);

        data = new LeftBean(R.drawable.ic_menu_about,"关于");
        mLeftDatas.add(data);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {
        loadingView.setVisibility(View.GONE);
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void showFriendCircle(ArrayList<TopicBean> list) {
        adapter=new FriendCircleAdapter(this,list);
        topiclist.setAdapter(adapter);
    }

    @Override
    public void showFriend(List<UserBean> userList) {
        FriendAdapter adapter = new FriendAdapter(userList,this);
        friendRV.setAdapter(adapter);
    }

    public Context getContext() {
        return this;
    }

}
