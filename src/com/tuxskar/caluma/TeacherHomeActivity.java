package com.tuxskar.caluma;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tuxskar.caluma.ws.WSErrorHandler;
import com.tuxskar.caluma.ws.WSHandler;
import com.tuxskar.caluma.ws.models.Degree;
import com.tuxskar.caluma.ws.models.School;
import com.tuxskar.caluma.ws.models.SimpleInfo;
import com.tuxskar.caluma.ws.models.SubjectSimple;
import com.tuxskar.caluma.ws.models.WSInfo;

public class TeacherHomeActivity extends Activity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	static RequestInterceptor requestInterceptor;
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	static SharedDB sharedDB;
	static Map<Long, ArrayList<Long>> tsubjectsIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sharedDB = new SharedDB(this.getApplicationContext());
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		requestInterceptor = new RequestInterceptor() {
			@Override
			public void intercept(RequestFacade request) {
				String token = sharedDB
						.getString(getString(R.string.userToken));
				request.addHeader("Authorization", " Token " + token);
				request.addHeader("WWW-Authenticate", " Token");
			}
		};
	}

	@Override
	protected void onStop() {
		sharedDB.putIDMap(getString(R.string.TSUBJECTIDS), tsubjectsIds);
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public static void addEventCorrect(String title, Calendar start,
			Calendar end, String description, String rrule, String location,
			Context context, long tSubjectId) {
		SharedPreferences sharedPref = context.getSharedPreferences(
				context.getString(R.string.calendarPreferences),
				Context.MODE_PRIVATE);
		long calID = sharedPref.getLong(
				context.getString(R.string.selectedCalendarId), 1);
		long startMillis = 0;
		long endMillis = 0;
		startMillis = start.getTimeInMillis();
		endMillis = end.getTimeInMillis();
		ContentResolver cr = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(Events.DTSTART, startMillis);
		values.put(Events.DTEND, endMillis);
		values.put(Events.RRULE, rrule);
		values.put(Events.TITLE, title);
		values.put(Events.DESCRIPTION, description);
		values.put(Events.EVENT_LOCATION, location);
		values.put(Events.CALENDAR_ID, calID);
		values.put(Events.EVENT_TIMEZONE, "Europe/Madrid");
		Uri uri = cr.insert(Events.CONTENT_URI, values);
		Long eventId = Long.parseLong(uri.getLastPathSegment());
		// Toast.makeText(context,
		// "Created Calendar Event " + eventID + " CalId: " +
		// Long.toString(calID), Toast.LENGTH_SHORT).show();
		TeacherHomeActivity.sharedDB.saveID(tSubjectId, eventId);
	}

	public static int deleteEventId(Context context, Long tSubjectId) {
		int deleted = 0;
		for (Long eventId : TeacherHomeActivity.sharedDB
				.getEventIds(tSubjectId)) {
			deleted += context.getContentResolver().delete(Events.CONTENT_URI,
					Events._ID + " = " + Long.toString(eventId), null);
		}
		TeacherHomeActivity.sharedDB.removeTSubject(tSubjectId);
		return deleted;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return SubjectsSearcherFragment.newInstance(position);
			default:
				return PlaceholderFragment.newInstance(position + 1);
			}
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class SubjectsSearcherFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		static WSHandler service;
		View rootV;
		private WSInfo<School> wsSchool;
		private Degree selectedDegree;
		private int argSchoolSelected;
		private int argDegreeSelected;
		Context context;

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static SubjectsSearcherFragment newInstance(int sectionNumber) {
			SubjectsSearcherFragment fragment = new SubjectsSearcherFragment();
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

			RestAdapter restAdapter = new RestAdapter.Builder()
					.setEndpoint(WSHandler.SERVICE_ENDPOINT)
					.setErrorHandler(new WSErrorHandler())
					.setRequestInterceptor(
							TeacherHomeActivity.requestInterceptor)
					.setConverter(new GsonConverter(gson)).build();

			service = restAdapter.create(WSHandler.class);

			return fragment;
		}

		public SubjectsSearcherFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			context = container.getContext();
			rootV = inflater.inflate(R.layout.subjects_searcher, container,
					false);
			getSchools();
			return rootV;
		}

		public void getSchools() {
			service.listSchoolCB(new Callback<WSInfo<School>>() {
				@Override
				public void failure(RetrofitError arg0) {
					if (arg0.getCause() != null) {
						Toast.makeText(context,
								"Fail getSchool" + arg0.getCause().toString(),
								Toast.LENGTH_LONG).show();
						Log.e("failure school", arg0.getCause().toString());
					} else {
						Toast.makeText(context,
								"Fail getSchool no Cause" + arg0.toString(),
								Toast.LENGTH_LONG).show();
						Log.e("failure school", arg0.toString());

					}
				}

				@Override
				public void success(WSInfo<School> result, Response arg1) {
					Log.d("success school", result.toString());
					wsSchool = result;
					setSchools();
				}
			});
		}

		private void setSchools() {
			Spinner schoolSpinner = (Spinner) rootV
					.findViewById(R.id.spinner_schools);
			ArrayAdapter<School> dataAdapter = new ArrayAdapter<School>(
					this.getActivity(), android.R.layout.simple_spinner_item,
					wsSchool.getResults());
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			schoolSpinner.setAdapter(dataAdapter);

			schoolSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							setDegrees(arg2);
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
						}
					});

		}

		private void setDegrees(int schoolSelected) {
			argSchoolSelected = schoolSelected;
			Spinner degreeSpinner = (Spinner) rootV
					.findViewById(R.id.spinner_degree);
			ArrayAdapter<SimpleInfo> dataAdapter = new ArrayAdapter<SimpleInfo>(
					this.getActivity(), android.R.layout.simple_spinner_item,
					wsSchool.getResults().get(argSchoolSelected).getDegrees());
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			degreeSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							argDegreeSelected = arg2;
							getSubjects(wsSchool.getResults()
									.get(argSchoolSelected).getDegrees()
									.get(argDegreeSelected).getId());
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
						}
					});
			degreeSpinner.setAdapter(dataAdapter);

		}

		public void getSubjects(long degreeId) {
			service.getDegree(degreeId, new Callback<Degree>() {
				@Override
				public void failure(RetrofitError arg0) {
					Log.d("failure degree", arg0.getResponse().toString());
				}

				@Override
				public void success(Degree result, Response arg1) {
					selectedDegree = result;
					populateSubjects();
				}
			});
		}

		private void populateSubjects() {
			ArrayAdapter<SubjectSimple> adapter = new SubjectArrayAdapter(
					this.getActivity(), selectedDegree.getSubjects());
			ListView subjects_list = (ListView) rootV
					.findViewById(R.id.subjects_list);
			subjects_list.setAdapter(adapter);
		}

	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			TextView tv = new TextView(container.getContext());
			Log.v("FRAGMENT",
					Integer.toString(this.getArguments().getInt(
							ARG_SECTION_NUMBER)));
			tv.setText(Integer.toString(this.getArguments().getInt(
					ARG_SECTION_NUMBER)));
			((LinearLayout) rootView.findViewById(R.id.content)).addView(tv);
			return rootView;
		}
	}
}
