package in.macro.codes.Kncok;

        import android.os.Bundle;
        import android.view.View;

        import androidx.fragment.app.Fragment;
        import androidx.fragment.app.FragmentManager;
        import androidx.fragment.app.FragmentPagerAdapter;

        import in.macro.codes.Kncok.DiscoverElements.DiscoverMain;


class PageAdapterMain extends FragmentPagerAdapter {




    public PageAdapterMain(FragmentManager fm) {
        super(fm);

    }


    @Override
    public Fragment getItem(int position) {

        switch(position) {


//            case 0:
//                DiscoverMain globalStory = new DiscoverMain();
//                View fragmentRootView = globalStory.getView();
//                Bundle data = new Bundle();
//
//                if ( globalStory.isAdded()) {
//
//                    data.putString("visible", "true");
//                } else {
//                    data.putString("visible", "false");
//                }
//
//                globalStory.setArguments(data);
//                return globalStory;

            case 0:
                return new Main2Fragment();




            default:
                return  null;
        }

    }

    @Override
    public int getCount() {
        return 1;
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
