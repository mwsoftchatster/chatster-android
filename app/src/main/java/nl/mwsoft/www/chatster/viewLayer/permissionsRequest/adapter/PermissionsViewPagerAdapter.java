/*
  Copyright (C) 2017 - 2020 MWSOFT

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

