package com.ccajk.Tabs;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Belal on 2/3/2016.
 */
//Extending FragmentStatePagerAdapter
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;
    Tab1 tab1;
    Tab2 tab2;


    //Constructor to the class
    public ViewPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
        tab1 = new Tab1();
        tab2 = new Tab2();

    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                //Tab1 tab1 = new Tab1();
                return tab1;
            case 1:
                //Tab2 tab2 = new Tab2();
                return tab2;

            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}