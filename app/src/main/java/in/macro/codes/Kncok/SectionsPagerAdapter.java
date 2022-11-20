package in.macro.codes.Kncok;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import in.macro.codes.Kncok.Groups.GroupFragment;
import in.macro.codes.Kncok.NotificationFragment.RequestsFragment;


class SectionsPagerAdapter extends FragmentPagerAdapter{

    public FragmentTransaction setMaxLifecycle(Fragment fragment, Lifecycle.State state) {
        return null;
    }

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }






    @Override
    public Fragment getItem(int position) {

        switch(position) {




            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return  chatsFragment;

            case 1:

                GroupFragment groupsFragment = new GroupFragment();
            return groupsFragment;

            case 2:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;

            case 3:
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            default:
                return  null;
        }

    }

    @Override
    public int getCount() {
        return 4;
    }

    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "Messages";

            case 1:
                return "Groups";

            case 2:
                return "Notifications";

            default:
                return null;
        }
    }

}
