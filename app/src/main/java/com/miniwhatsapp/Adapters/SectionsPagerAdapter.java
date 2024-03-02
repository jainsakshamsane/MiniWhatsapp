package com.miniwhatsapp.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.miniwhatsapp.Calls_Section;
import com.miniwhatsapp.Chat_Section;
import com.miniwhatsapp.Status_Section;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // Return the appropriate Fragment for each tab position
        switch (position) {
            case 0:
                return new Chat_Section();
            case 1:
                return new Status_Section();
            case 2:
                return new Calls_Section();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // Number of tabs
        return 3;
    }
}
