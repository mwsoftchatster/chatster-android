package nl.mwsoft.www.chatster.viewLayer.intro.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import nl.mwsoft.www.chatster.viewLayer.intro.fragment.IntroEnjoyFragment;
import nl.mwsoft.www.chatster.viewLayer.intro.fragment.IntroFeaturesOneFragment;
import nl.mwsoft.www.chatster.viewLayer.intro.fragment.IntroFeaturesThreeFragment;
import nl.mwsoft.www.chatster.viewLayer.intro.fragment.IntroFeaturesTwoFragment;
import nl.mwsoft.www.chatster.viewLayer.intro.fragment.IntroOverviewFragment;
import nl.mwsoft.www.chatster.viewLayer.intro.fragment.IntroVoiceTypingFragment;


public class IntroViewPagerAdapter extends FragmentPagerAdapter {

    public IntroViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new IntroOverviewFragment();
        } else if (position == 1) {
            fragment = new IntroFeaturesOneFragment();
        } else if (position == 2) {
            fragment = new IntroFeaturesTwoFragment();
        } else if (position == 3) {
            fragment = new IntroFeaturesThreeFragment();
        } else if (position == 4) {
            fragment = new IntroVoiceTypingFragment();
        } else if (position == 5) {
            fragment = new IntroEnjoyFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0) {
            //title = ;
        } else if (position == 1) {
            //title = ;
        } else if (position == 2) {
            //title = ;
        } else if (position == 3) {
            //title = ;
        } else if (position == 4) {
            //title = ;
        } else if (position == 5) {
            //title = ;
        }
        return title;
    }
}

