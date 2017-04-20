package com.esen.together;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.esen.together.adapter.MainTabPagerAdapter;
import com.esen.together.adapter.SimpleRecyclerAdapter;
import com.esen.together.common.VersionModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	TabLayout tabLayout;
	ViewPager viewPager;
	MainTabPagerAdapter mainTabPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
			}
		});

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		// saka check, 17.04.20, 12.41, tabe menu, first way
		// initializing TabPagerAdapter
		mainTabPagerAdapter = new MainTabPagerAdapter(getSupportFragmentManager());

		// initializing tab layout
		tabLayout = (TabLayout) findViewById(R.id.tab_layout);

		// initializing ViewPager
		viewPager = (ViewPager) findViewById(R.id.pager);

		viewPager.setAdapter(mainTabPagerAdapter);
		tabLayout.setTabsFromPagerAdapter(mainTabPagerAdapter);
		// giving viewPager reference to tablayout so that the viewPager changes when tab is clicked
		tabLayout.setupWithViewPager(viewPager);
		// giving tablayout reference to viewPager so that the tablayout changes when viewPager is scrolled
		viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

		// saka check, 17.04.20, 12.41, tab menu, second way
		/*final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		setupViewPager(viewPager);

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
		tabLayout.setupWithViewPager(viewPager);

		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {

				viewPager.setCurrentItem(tab.getPosition());

				switch (tab.getPosition()) {
					case 0:
						showToast("One");
						break;
					case 1:
						showToast("Two");
						break;
					case 2:
						showToast("Three");
						break;
				}
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});*/
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_camera) {
			// Handle the camera action
		} else if (id == R.id.nav_gallery) {

		} else if (id == R.id.nav_slideshow) {

		} else if (id == R.id.nav_manage) {

		} else if (id == R.id.nav_share) {

		} else if (id == R.id.nav_send) {

		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	private void setupViewPager(ViewPager viewPager) {
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFrag(new DummyFragment(
				ContextCompat.getColor(this, R.color.blue_grey)), "CAT");
		adapter.addFrag(new DummyFragment(
				ContextCompat.getColor(this, R.color.amber)), "DOG");
		adapter.addFrag(new DummyFragment(
				ContextCompat.getColor(this, R.color.cyan)), "MOUSE");
		viewPager.setAdapter(adapter);
	}

	private static class ViewPagerAdapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragmentList = new ArrayList<>();
		private final List<String> mFragmentTitleList = new ArrayList<>();

		public ViewPagerAdapter(FragmentManager manager) {
			super(manager);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		public void addFrag(Fragment fragment, String title) {
			mFragmentList.add(fragment);
			mFragmentTitleList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitleList.get(position);
		}
	}

	public static class DummyFragment extends Fragment {
		int color;
		SimpleRecyclerAdapter adapter;

		public DummyFragment() {
		}

		@SuppressLint("ValidFragment")
		public DummyFragment(int color) {
			this.color = color;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.dummy_fragment, container, false);

			final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.dummyfrag_bg);
			frameLayout.setBackgroundColor(color);

			RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_scrollableview);

			LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
			recyclerView.setLayoutManager(linearLayoutManager);
			recyclerView.setHasFixedSize(true);

			List<String> list = new ArrayList<String>();
			for (int i = 0; i < VersionModel.data.length; i++) {
				list.add(VersionModel.data[i]);
			}

			adapter = new SimpleRecyclerAdapter(list);
			recyclerView.setAdapter(adapter);

			return view;
		}
	}
}
