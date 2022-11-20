package in.macro.codes.Kncok;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import in.macro.codes.Kncok.Groups.GroupFragment;


class FriendGroupAdapter extends FragmentPagerAdapter{


    private String text;
     FriendGroupAdapter(FragmentManager fm , String text) {
        super(fm);
        this.text=text;
    }






    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch(position) {




            case 0:
                FriendSearch chatsFragment = new FriendSearch();
                Bundle bundle = new Bundle();
                bundle.putString("text",text);
                chatsFragment.setArguments(bundle);
                return  chatsFragment;

            case 1:

                GroupSearch groupsFragment = new GroupSearch();
                Bundle bundle2 = new Bundle();
                bundle2.putString("text",text);
                groupsFragment.setArguments(bundle2);
                return groupsFragment;

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
