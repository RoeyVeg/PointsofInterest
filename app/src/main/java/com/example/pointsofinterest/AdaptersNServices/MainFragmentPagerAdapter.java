package com.example.pointsofinterest.AdaptersNServices;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.pointsofinterest.R;

import java.util.ArrayList;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    Context context;
    private String[] titles = new String[3];

    //fragment adapter to bind the fragment to the view pager and the tab layout, has 3 fragments


    public MainFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments, Context context) {
        super(fm);
        this.fragmentArrayList.addAll(fragments);
        this.context = context;

        this.titles[0] = context.getString(R.string.homeTabName);
        this.titles[1] = context.getString(R.string.favoritesTabName);
        this.titles[2] = context.getString(R.string.aroundMeTabName);

    }


    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {


        return titles[position];
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object =  super.instantiateItem(container, position);

        fragmentArrayList.remove(position);
        fragmentArrayList.add(position,(Fragment) object);

        return object;    }
}
