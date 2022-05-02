package nl.mwsoft.www.chatster.viewLayer.main.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import nl.mwsoft.www.chatster.viewLayer.main.fragment.ChatsFragment;
import nl.mwsoft.www.chatster.viewLayer.main.fragment.ContactsFragment;
import nl.mwsoft.www.chatster.viewLayer.main.fragment.GroupChatsFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new ChatsFragment();
        }
        else if (position == 1) {
            fragment = new GroupChatsFragment();
        }
        else if (position == 2) {
            fragment = new ContactsFragment();
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
            // set title here if needed
        }
        else if (position == 1) {
            // set title here if needed
        }
        else if (position == 2) {
            // set title here if needed
        }
        return title;
    }
}
