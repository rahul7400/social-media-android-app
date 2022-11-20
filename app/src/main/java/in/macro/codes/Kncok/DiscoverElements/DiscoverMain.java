package in.macro.codes.Kncok.DiscoverElements;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import in.macro.codes.Kncok.DiscoverElements.DiscoverPageAdapter;
import in.macro.codes.Kncok.R;
import in.macro.codes.Kncok.TabsAnimation.ZoomOutTransformation;

public class DiscoverMain extends Fragment {
    private ViewPager mViewPager;
    private DiscoverPageAdapter mSectionsPagerAdapter;
    View mView;


    public DiscoverMain() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_discover_main, container, false);
        mViewPager = (ViewPager) mView.findViewById(R.id.discover_view);
        mSectionsPagerAdapter = new DiscoverPageAdapter(getFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setCurrentItem(1);

        return mView;
    }
}