package com.yitianyitiandan.googlemap;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //TabLayout param
    private final String[] titles = {"map", "test2", "test3", "test4"}; //tab name
    private ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setTabLayout
        initTabLayout();
    }
    private void initTabLayout(){
        SlidingTabLayout stl;
        ViewPager vp;
        MyPagerAdapter adapter;
        //將各fragment加入arrayList
        fragments.add(MapFragment.getInstance());
        fragments.add(InfoFragment.newInstance());
        fragments.add(InfoFragment.newInstance());
        fragments.add(InfoFragment.newInstance());
        stl= findViewById(R.id.stl);
        vp = findViewById(R.id.vp);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(adapter);
        stl.setViewPager(vp);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getCount() {
            return fragments.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
    }
}
