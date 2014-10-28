package com.tuxskar.caluma;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tuxskar.caluma.ws.WSErrorHandler;
import com.tuxskar.caluma.ws.WSHandler;
import com.tuxskar.caluma.ws.models.Degree;
import com.tuxskar.caluma.ws.models.School;
import com.tuxskar.caluma.ws.models.WSInfo;

public class MainActivity extends Activity implements ActionBar.TabListener {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
			    request.addHeader("WWW-Authenticate", " Basic realm='api'");
			  }
			};
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
					// seeCalendars();
					// Calendar calstart = Calendar.getInstance(), calend =
					// Calendar
					// .getInstance();
					// calend.add(Calendar.MINUTE, 31);
					Calendar calStart1 = new GregorianCalendar(2014, 9, 5, 10,
							0);
					calStart1.setTimeZone(timezone);
					Calendar calEnd2 = new GregorianCalendar(2014, 9, 5, 12, 45);
					// Calendar calnew = new GregorianCalendar("20141126T1000");
					calEnd2.setTimeZone(timezone);

					// int min_duration = 45;
					// Calendar calEnd1 = calStart1;
					// calEnd1.add(Calendar.MINUTE, min_duration);
					// addEventCorrect("Mates", calStart1, calEnd2,
					// "Matem‡ticas aplicadas a la bioinform‡tica",
					// "FREQ=WEEKLY;COUNT:50;BYDAY=MO,TU,WE"+";UNTIL="+calEnd2,
					// "ETSII aula 3.0.6");
					String until = calEnd2.getTime().toString();
					Log.v("DIME UNTIL", until);
					addEventCorrect("Mates", calStart1, calEnd2,
							"Matem‡ticas aplicadas a la bioinform‡tica",
							"FREQ=WEEKLY;BYDAY=MO,TU,WE"
									+ ";UNTIL="
									+ CalendarToString(new GregorianCalendar(
											2014, 11, 29, 0, 0)),
							"ETSII aula 3.0.6");
				}

			});
			// buttonRemoveEvent
			Button buttonRemoveEvent = (Button) rootV
					.findViewById(R.id.remove_events);
			buttonRemoveEvent.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					removeLastEvent();
				}

			});
			// buttonNew

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
			return sdf.format(calendar);
		}

		public void populateCalendars() {
			String[] projection = new String[] { Calendars._ID, Calendars.NAME,
					Calendars.ACCOUNT_NAME, Calendars.ACCOUNT_TYPE };
			Cursor calCursor = this.getActivity().getContentResolver()
					.query(Calendars.CONTENT_URI, projection,
					// Calendars.VISIBLE + " = 1",
							null, null, Calendars._ID + " DESC");
			if (calendarNames == null) {
				calendarNames = new ArrayList<String>();
				calendarAccounts = new ArrayList<String>();
				calendarTypes = new ArrayList<String>();
			}
			if (calendarIds == null) {
				calendarIds = new ArrayList<Long>();
			}
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
			// String TAG = "adding Event";
			// Log.d(TAG, "AddUsingContentProvider.addEvent()");
			ContentResolver contentResolver = this.getActivity()
					.getContentResolver();

			ContentValues calEvent = new ContentValues();
			calEvent.put(CalendarContract.Events.CALENDAR_ID,
					Long.toString(SelectedCalendarId));
			calEvent.put(CalendarContract.Events.TITLE, title);
			calEvent.put(CalendarContract.Events.DTSTART,
					start.getTimeInMillis());
			// calEvent.put(CalendarContract.Events.RDATE,
			// start.getTimeInMillis());
			calEvent.put(CalendarContract.Events.DTEND, end.getTimeInMillis());
			// calEvent.put(CalendarContract.Events.DURATION, "P20W");
			calEvent.put(CalendarContract.Events.EVENT_TIMEZONE,
					"Europe/Madrid");
			calEvent.put(CalendarContract.Events.RRULE, rrule);
			calEvent.put(CalendarContract.Events.DESCRIPTION, description);
			calEvent.put(CalendarContract.Events.EVENT_LOCATION, location);

			// ContentValues values = new ContentValues();
			// values.put(Events.DTSTART, start);
			// values.put(Events.DTEND, start);
			// values.put(Events.RRULE,
			// "FREQ=DAILY;COUNT=20;BYDAY=MO,TU,WE,TH,FR;WKST=MO");
			// values.put(Events.TITLE, "Some title");
			// values.put(Events.EVENT_LOCATION, "MŸnster");
			// values.put(Events.CALENDAR_ID, calId);
			// values.put(Events.EVENT_TIMEZONE, "Europe/Berlin");
			// values.put(Events.DESCRIPTION,
			// "The agenda or some description of the event");
			// // reasonable defaults exist:
			// values.put(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
			// values.put(Events.SELF_ATTENDEE_STATUS,
			// Events.STATUS_CONFIRMED);
			// values.put(Events.ALL_DAY, 1);
			// values.put(Events.ORGANIZER, "some.mail@some.address.com");
			// values.put(Events.GUESTS_CAN_INVITE_OTHERS, 1);
			// values.put(Events.GUESTS_CAN_MODIFY, 1);
			// values.put(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
			// Uri uri =
			// getContentResolver().
			// insert(Events.CONTENT_URI, values);
			// long eventId = new Long(uri.getLastPathSegment());

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

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static SubjectsSearcherFragment newInstance(int sectionNumber) {
			SubjectsSearcherFragment fragment = new SubjectsSearcherFragment();
			RestAdapter restAdapter = new RestAdapter.Builder()
					.setEndpoint(WSHandler.SERVICE_ENDPOINT)
					.setErrorHandler(new WSErrorHandler())
					.setRequestInterceptor(MainActivity.requestInterceptor)
					.build();

			service = restAdapter.create(WSHandler.class);

			return fragment;
		}

		public SubjectsSearcherFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			rootV = inflater.inflate(R.layout.subjects_searcher, container,
					false);
			Button buttonGetSchools = (Button) rootV
					.findViewById(R.id.get_schools);
			buttonGetSchools.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					getSchools();
				}
			});
			Button bSubjects = (Button) rootV.findViewById(R.id.get_degree);
			bSubjects.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					getDegree();
				}
			});

			return rootV;
		}

		public void getSchools() {
			service.listSchoolCB(new Callback<List<WSInfo<School>>>(){
				@Override
	            public void failure(RetrofitError arg0) {
					Log.d("failure school", arg0.getResponse().toString());
	            }

	            @Override
	            public void success(List<WSInfo<School>> result, Response arg1) {
					Log.d("success school", arg1.toString() + " " + result.toString());
	            }
				
				
			});
			
			
			
			
			
//			Callback<List<WSInfo<School>>> schoolsCB = null;
//			service.listSchoolCB(schoolsCB);
//			
////			List<WSInfo<School>> schools = service.listSchool();
//			int a = 2;
//			Toast.makeText(this.getActivity(), "Schools " + schoolsCB,
//					Toast.LENGTH_SHORT).show();
		}

		public void getDegree() {
			Degree degree = service.detailDegree(1);
			Toast.makeText(this.getActivity(), "Degree " + degree.toString(),
					Toast.LENGTH_SHORT).show();
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
