package in.macro.codes.Kncok.DiscoverElements;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import in.macro.codes.Kncok.GlobalStory.GlobalDiscover;


public class DiscoverPageAdapter extends FragmentPagerAdapter {




    public DiscoverPageAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {

        switch(position) {


           case 0:
                GlobalDiscover chatsFragment = new GlobalDiscover();
                return  chatsFragment;

            case 1:
                FriendsFeeds chatsFragment2 = new FriendsFeeds();
                return  chatsFragment2;





            default:
                return  null;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "Feeds";

            case 1:
                return "Messages";

            default:
                return null;
        }
    }


}
