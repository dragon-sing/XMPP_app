package com.example.xmpp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.xmpp.Utils.ToolBarUtil;
import com.example.xmpp.fragment.ContactsFragment;
import com.example.xmpp.fragment.SessionFragment;

import java.util.ArrayList;
import java.util.List;



public class indexActivity extends AppCompatActivity {

    private  TextView indexTitle;
    private  ViewPager indexViewpager;
    private  LinearLayout indexBottom;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private ToolBarUtil toolBarUtil;
    private int[] iconArr;
    private String[] toolBarTitleArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        initView();
        initData();
        initListener();
    }
    private void initListener(){
        indexViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //修改颜色
                toolBarUtil.changeColor(position);
                //修改title
                indexTitle.setText(toolBarTitleArr[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        toolBarUtil.setOnToolBarClickListener(new ToolBarUtil.OnToolBarClickListener() {
            @Override
            public void onToolBarClick(int position) {
                indexViewpager.setCurrentItem(position);
            }
        });

    }
    private void initView(){
        indexViewpager= (ViewPager) findViewById(R.id.index_viewpager);
        indexBottom=(LinearLayout)findViewById(R.id.index_bottom);
        indexTitle=(TextView)findViewById(R.id.index_title);
    }
    private void initData() {
        //添加fragment到集合中
        mFragments.add(new SessionFragment());
        mFragments.add(new ContactsFragment());
        MyPagerAdapter adapter=new MyPagerAdapter(getSupportFragmentManager());
        indexViewpager.setAdapter(adapter);

        //底部按钮
        toolBarUtil = new ToolBarUtil();
        //文字内容
        toolBarTitleArr = new String[]{"消息","好友"};
        iconArr = new int[]{R.drawable.sel_message,R.drawable.sel_friend};
        toolBarUtil.createToolBar(indexBottom, toolBarTitleArr, iconArr);
        //设置默认选中消息
        toolBarUtil.changeColor(0);


    }
    class  MyPagerAdapter extends FragmentPagerAdapter{
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

    }
}
