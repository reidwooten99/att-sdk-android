package com.att.testaab;


import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

public class ContactsFragmentView extends FragmentActivity implements ActionBar.TabListener{
	
	//AppSectionsPagerAdapter mAppSectionsPagerAdapter;
	// ViewPager mViewPager;
	
	FragmentTransaction ft;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_fragment_view);
		
		//mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
				
		 ft = getFragmentManager().beginTransaction();
		 final ActionBar actionBar = getActionBar();
		 actionBar.setHomeButtonEnabled(false);
		 actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		 
		/* mViewPager = (ViewPager) findViewById(R.id.pager);
	       mViewPager.setAdapter(mAppSectionsPagerAdapter);
	        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
	            @Override
	            public void onPageSelected(int position) {
	                // When swiping between different app sections, select the corresponding tab.
	                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
	                // Tab.
	                actionBar.setSelectedNavigationItem(position);
	            }
	        });*/
	        
	            // Create a tab with text corresponding to the page title defined by the adapter.
	            // Also specify this Activity object, which implements the TabListener interface, as the
	            // listener for when this tab is selected.
	            actionBar.addTab(
	                    actionBar.newTab()
	                            .setText("MyInfo").setTabListener(this) );
	           
	                    
	                            
	            actionBar.addTab(
	                    actionBar.newTab()
	                            .setText("Groups") 
	                            .setTabListener(this));
	            
	            actionBar.addTab(
	                    actionBar.newTab()
	                            .setText("Search") 
	                            .setTabListener(this));
	      
	    }		
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contacts_fragment_view, menu);
		return true;
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub		
		if (tab.getText().equals("MyInfo")) {
			ContactListFragment contactListFragment;
			contactListFragment = new ContactListFragment();
			ft = getFragmentManager().beginTransaction();
			ft.add(R.id.pager, contactListFragment);
			ft.commit();
		} 
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	/*public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
                    //return new LaunchpadSectionFragment();
                	return new ContactListFragment();

                case 1:
                    // The other sections of the app are dummy placeholders.
                    Fragment fragment = new DummySectionFragment();
                    Bundle args = new Bundle();
                    args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
                    fragment.setArguments(args); 
                	return fragment;
                	
                default : 
                	
                	return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Section " + (position + 1);
        }
    }
*/	
	/* public static  class LaunchpadSectionFragment extends Fragment {

	        @Override
	        public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                Bundle savedInstanceState) {
	        	
	            View rootView = inflater.inflate(R.layout.activity_contacts_list, container, false);  
	            return rootView;
	        }
	    }*/
	
	
	 
	/* public static class DummySectionFragment extends Fragment {

	        public static final String ARG_SECTION_NUMBER = "section_number";

	        @Override
	        public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                Bundle savedInstanceState) {
	     	            View rootView = inflater.inflate(R.layout.activity_group_list, container, false);
	            Bundle args = getArguments();
	            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
	                    getString(R.string.dummy_section_text, args.getInt(ARG_SECTION_NUMBER)));
	            return rootView;
	        }
	    }*/


}
