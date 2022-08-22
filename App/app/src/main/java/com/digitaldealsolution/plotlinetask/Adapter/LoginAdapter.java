package com.digitaldealsolution.plotlinetask.Adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.digitaldealsolution.plotlinetask.Fragments.LoginTabFragment;
import com.digitaldealsolution.plotlinetask.Fragments.SignupTabFragment;

public class LoginAdapter extends FragmentPagerAdapter {
    private Context context;
    private int totalTab;

    public LoginAdapter(FragmentManager fm, Context context, int totalTab) {
        super(fm);
        this.context = context;
        this.totalTab = totalTab;
    }

    @Override
    public int getCount() {
        return totalTab;
    }

    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                LoginTabFragment loginTabFragment = new LoginTabFragment();
                return loginTabFragment;
            case 1:
                SignupTabFragment signupTabFragment = new SignupTabFragment();
                return signupTabFragment;
            default:
                return null;
        }
    }
}
