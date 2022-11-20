package in.macro.codes.Kncok.Groups;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import in.macro.codes.Kncok.Groups.GroupProfileInfo;
import in.macro.codes.Kncok.Groups.GroupUsersListFrag;

public class GroupProfilePageAdapter  extends FragmentPagerAdapter {
    private String gname;
    public GroupProfilePageAdapter(FragmentManager fm) {
        super(fm);
    }

    public GroupProfilePageAdapter(FragmentManager fm ,String gname){
        super(fm);
        this.gname=gname;
    }
    @Override
    public Fragment getItem(int position) {

        switch(position) {


            case 0:

                GroupProfileInfo global_status = new GroupProfileInfo();
                Bundle bundle = new Bundle();
                bundle.putString("gname",gname);
                global_status.setArguments(bundle);
                return global_status;

            case 1:
                GroupUsersListFrag chatsFragment = new GroupUsersListFrag();
                Bundle bundle2 = new Bundle();
                bundle2.putString("gname",gname);
                chatsFragment.setArguments(bundle2);
                return  chatsFragment;




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
                return "Group";

            case 1:
                return "Group Info";

            default:
                return null;
        }
    }
}
