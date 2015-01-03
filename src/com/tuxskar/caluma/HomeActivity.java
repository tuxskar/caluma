package com.tuxskar.caluma;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class HomeActivity extends Activity implements ActionBar.TabListener {

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
				String token = sharedDB.getString(getString(R.string.userToken));
				if (token == ""){
					token = WSHandler.android_key;
				}
				request.addHeader("Authorization", " Token "
						+ token);
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
		HomeActivity.sharedDB.saveID(tSubjectId, eventId);
	}

	public static int deleteEventId(Context context, Long tSubjectId) {
		int deleted = 0;
		for (Long eventId : HomeActivity.sharedDB.getEventIds(tSubjectId)) {
//			selArgs.add(Long.toString(eventId));
			deleted += context.getContentResolver().delete(Events.CONTENT_URI,
					Events._ID + " = " + Long.toString(eventId), null);
		}
//		ArrayList<String> selArgs = new ArrayList<String>();
//		String[] selArray = new String[selArgs.size()];
//		String[] selArray = {selArgs.get(0), selArgs.get(1), selArgs.get(2)};
//		selArray = selArgs.toArray(selArray);

		HomeActivity.sharedDB.removeTSubject(tSubjectId);
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
				return CalendarsFragment.newInstance(position);
			case 1:
				return SubjectsSearcherFragment.newInstance(position);
			default:
				return PlaceholderFragment.newInstance(position + 1);
			}
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.action_settings).toUpperCase(l);
			case 1:
				return getString(R.string.title_section1).toUpperCase(l);
			case 2:
				return getString(R.string.title_section2).toUpperCase(l);
			case 3:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class CalendarsFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */

		public static List<String> calendarNames;
		public static List<String> calendarAccounts;
		public static List<String> calendarTypes;
		public static List<Long> calendarIds;
		View rootV;
		public static Long SelectedCalendarId;
		public static Long createdEventId = (long) 277;
		public static String READ_FORMAT_DATETIME = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
		public static SimpleDateFormat sdf = new SimpleDateFormat(
				READ_FORMAT_DATETIME, Locale.ENGLISH);
		public static TimeZone timezone = TimeZone.getTimeZone("Europe/Madrid");
		public Context context;

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static CalendarsFragment newInstance(int sectionNumber) {
			CalendarsFragment fragment = new CalendarsFragment();
			return fragment;
		}

		public CalendarsFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			context = container.getContext();
			rootV = inflater.inflate(R.layout.fragment_calendars_settings,
					container, false);
			populateCalendars();

			// Creating adapter for spinner
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
					this.getActivity(), android.R.layout.simple_spinner_item,
					calendarNames);

			// Drop down layout style - list view with radio button
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			Spinner spinner = (Spinner) rootV
					.findViewById(R.id.calendars_spinner);

			// attaching data adapter to spinner
			spinner.setAdapter(dataAdapter);

			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					TextView tv_name = (TextView) rootV
							.findViewById(R.id.cal_name);
					TextView tv_account = (TextView) rootV
							.findViewById(R.id.cal_account);
					TextView tv_type = (TextView) rootV
							.findViewById(R.id.cal_type);
					tv_name.setText(calendarNames.get(arg2));
					tv_account.setText(calendarAccounts.get(arg2));
					tv_type.setText(calendarTypes.get(arg2));
					SelectedCalendarId = calendarIds.get(arg2);
					SharedPreferences sharedPref = context
							.getSharedPreferences(context
									.getString(R.string.calendarPreferences),
									Context.MODE_PRIVATE);
					;
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putLong(getString(R.string.selectedCalendarId),
							SelectedCalendarId);
					editor.commit();
					Log.d("CalendarID selected:",
							Long.toString(HomeActivity.CalendarsFragment.SelectedCalendarId));
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});

			// Button buttonNew
			Button buttonAddEvent = (Button) rootV
					.findViewById(R.id.add_events);
			buttonAddEvent.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TEST!!
					SharedPreferences sharedPref = context
							.getSharedPreferences(context
									.getString(R.string.calendarPreferences),
									Context.MODE_PRIVATE);
					long calID = sharedPref.getLong(
							context.getString(R.string.selectedCalendarId), 1);
					long startMillis = 0;
					Calendar beginTime = Calendar.getInstance();
					beginTime.set(2014, 11, 14, 7, 30);
					startMillis = beginTime.getTimeInMillis();
					ContentResolver cr = context.getContentResolver();
					ContentValues values = new ContentValues();
					values.put(Events.DTSTART, startMillis);
					// values.put(Events.DTEND, endMillis);
					values.put(Events.TITLE, "Elegido directamente");
					values.put(Events.DURATION, "PT30M");
					values.put(Events.DESCRIPTION,
							"Una descripci—n para el elegido directamente a mano");
					values.put(Events.EVENT_LOCATION,
							"En tu casa o en la mia :P");
					values.put(Events.CALENDAR_ID, calID);
					values.put(Events.EVENT_TIMEZONE, "Europe/Madrid");
					Uri uri = cr.insert(Events.CONTENT_URI, values);
					long eventID = Long.parseLong(uri.getLastPathSegment());
					Toast.makeText(
							context,
							"Created Calendar Event " + eventID + " CalId: "
									+ Long.toString(calID), Toast.LENGTH_SHORT)
							.show();
					Calendar calStart1 = new GregorianCalendar(2014, 9, 5, 10,
							0);
					calStart1.setTimeZone(timezone);
					Calendar calEnd2 = new GregorianCalendar(2014, 9, 5, 12, 45);
					calEnd2.setTimeZone(timezone);
					String until = calEnd2.getTime().toString();
					Log.v("DIME UNTIL", until);
					addEventCorrect("Mates", calStart1, calEnd2,
							"Matemticas aplicadas a la bioinformtica",
							"FREQ=WEEKLY;BYDAY=MO,TU,WE"
									+ ";UNTIL="
									+ CalendarToString(new GregorianCalendar(
											2014, 11, 29, 0, 0)),
							"ETSII aula 3.0.6");
				}

			});
			Button buttonRemoveEvent = (Button) rootV
					.findViewById(R.id.remove_events);
			buttonRemoveEvent.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					removeLastEvent();
				}

			});
			return rootV;
		}

		public static Calendar stringToCalendar(String strDate,
				TimeZone timezone) throws ParseException {
			sdf.setTimeZone(timezone);
			Date date = sdf.parse(strDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal;
		}

		public static String CalendarToString(Calendar calendar) {
			return sdf.format(calendar.getTime());
		}

		public void populateCalendars() {
			String[] projection = new String[] { Calendars._ID, Calendars.NAME,
					Calendars.ACCOUNT_NAME, Calendars.ACCOUNT_TYPE };
			Cursor calCursor = this.getActivity().getContentResolver()
					.query(Calendars.CONTENT_URI, projection,
							null, null, Calendars._ID + " DESC");
			calendarNames = new ArrayList<String>();
			calendarAccounts = new ArrayList<String>();
			calendarTypes = new ArrayList<String>();
			calendarIds = new ArrayList<Long>();
			if (calCursor.moveToFirst()) {
				do {
					String name = calCursor.getString(1);
					if (name == null) {
						name = "Sin nombre";
					}
					calendarNames.add(name);
					calendarAccounts.add(calCursor.getString(2));
					calendarTypes.add(calCursor.getString(3));
					calendarIds.add(calCursor.getLong(0));

				} while (calCursor.moveToNext());
			}
		}

		public void addEventCorrect(String title, Calendar start, Calendar end,
				String description, String rrule, String location) {
			ContentResolver contentResolver = this.getActivity()
					.getContentResolver();

			ContentValues calEvent = new ContentValues();
			calEvent.put(CalendarContract.Events.CALENDAR_ID,
					Long.toString(SelectedCalendarId));
			calEvent.put(CalendarContract.Events.TITLE, title);
			calEvent.put(CalendarContract.Events.DTSTART,
					start.getTimeInMillis());
			calEvent.put(CalendarContract.Events.DTEND, end.getTimeInMillis());
			calEvent.put(CalendarContract.Events.EVENT_TIMEZONE,
					"Europe/Madrid");
			calEvent.put(CalendarContract.Events.RRULE, rrule);
			calEvent.put(CalendarContract.Events.DESCRIPTION, description);
			calEvent.put(CalendarContract.Events.EVENT_LOCATION, location);
			Uri uri = contentResolver.insert(
					CalendarContract.Events.CONTENT_URI, calEvent);
			// The returned Uri contains the content-retriever URI for
			// the newly-inserted event, including its id
			createdEventId = Long.valueOf(uri.getLastPathSegment());
			Toast.makeText(this.getActivity(),
					"Created Calendar Event " + createdEventId,
					Toast.LENGTH_SHORT).show();
		}

		public void removeLastEvent() {
			String[] selArgs = new String[] { Long.toString(createdEventId) };
			int deleted = this.getActivity().getContentResolver()
					.delete(Events.CONTENT_URI, Events._ID + " =? ", selArgs);
			Toast.makeText(this.getActivity(),
					"Deleted Events: " + " " + deleted + " events",
					Toast.LENGTH_SHORT).show();
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
					.setRequestInterceptor(HomeActivity.requestInterceptor)
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
