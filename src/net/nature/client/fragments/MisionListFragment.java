package net.nature.client.fragments;

import net.nature.client.Mission;
import net.nature.client.R;
import net.nature.client.database.ApplicationData;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@SuppressLint("NewApi")
public class MisionListFragment extends Fragment  {
	private Mission[] missions;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_mission_list, null);

		 // Instantiate a ViewPager and a PagerAdapter.
        ViewPager mPager = (ViewPager) rootView.findViewById(R.id.pager);
        ScreenSlidePagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        return rootView;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		missions = ApplicationData.getMissions(activity.getApplicationContext());
	}    

	/**
	 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
	 * sequence.
	 */
	class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			Mission mission = missions[position];			

			MissionFragment f = new MissionFragment();
			Bundle bundle = new Bundle();			
			bundle.putString("question",  mission.getQuestion());
			bundle.putString("objective",  mission.getObjective());
			f.setArguments(bundle);
			return f;
		}

		@Override
		public int getCount() {
			return 2;
		}
	}
}