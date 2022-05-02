package nl.mwsoft.www.chatster.viewLayer.permissionsRequest.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import nl.mwsoft.www.chatster.viewLayer.permissionsRequest.fragment.AccessFilesFragment;
import nl.mwsoft.www.chatster.viewLayer.permissionsRequest.fragment.MakeManagePhoneCallsFragment;
import nl.mwsoft.www.chatster.viewLayer.permissionsRequest.fragment.ReadContactsFragment;

public class PermissionsViewPagerAdapter  extends FragmentPagerAdapter {

    public PermissionsViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        if (position == 0) {
            fragment = new ReadContactsFragment();
        } else if (position == 1) {
            fragment = new MakeManagePhoneCallsFragment();
        } else if (position == 2) {
            fragment = new AccessFilesFragment();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
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
        }

        return title;
    }
}

