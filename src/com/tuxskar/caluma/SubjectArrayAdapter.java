package com.tuxskar.caluma;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tuxskar.caluma.ws.models.Exam;
import com.tuxskar.caluma.ws.models.SubjectSimple;
import com.tuxskar.caluma.ws.models.TeachingSubject;
import com.tuxskar.caluma.ws.models.Timetable;

public class SubjectArrayAdapter extends ArrayAdapter<SubjectSimple> {

	private final List<SubjectSimple> list;
	private final Activity context;

	public SubjectArrayAdapter(Activity context, List<SubjectSimple> list) {
		super(context, R.layout.subject_row, list);
		this.context = context;
		this.list = list;
	}

	static class ViewHolder {
		protected TextView text;
		protected CheckBox checkbox;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.subject_row, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) view.findViewById(R.id.label);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
			SubjectSimple element = list.get(position);
			if (element.getT_subject().length == 0) {
				viewHolder.checkbox.setActivated(false);
			} else {
				long tSubjectId = element.getT_subject()[0];
				boolean idFound = false;
				if (MainActivity.sharedDB.savedTSubject(tSubjectId)) {
					idFound = true;
				}
				viewHolder.checkbox.setChecked(idFound);
			}
			viewHolder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							SubjectSimple element = (SubjectSimple) viewHolder.checkbox
									.getTag();
							if (isChecked) {
								// add calendar event
								addCalendarEvent(element);
							} else {
								removeCalendarEvent(element);
							}

							element.setSelected(buttonView.isChecked());

						}

					});
			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(list.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.text.setText(list.get(position).getTitle());
		holder.checkbox.setChecked(list.get(position).isSelected());
		return view;
	}

	public void addCalendarEvent(final SubjectSimple element) {
		// TODO future: ask for the user to select the teachingSubjects that
		// fits
		// better choosing level and course
		if (element.getT_subject().length > 0) {
			MainActivity.SubjectsSearcherFragment.service.getTSubject(
					element.getT_subject()[0], new Callback<TeachingSubject>() {
						@Override
						public void failure(RetrofitError arg0) {
							Log.d("failure getting TeachingSubject", arg0
									.getResponse().toString());
						}

						@SuppressLint("SimpleDateFormat")
						@SuppressWarnings("deprecation")
						@Override
						public void success(TeachingSubject result,
								Response arg1) {
							String rrule[] = { "SU", "MO", "TU", "WE", "TH",
									"FR", "SA" };
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyyMMdd");
							String Until = sdf.format(result.getEnd_date()
									.getTime());
							String message = "";
							for (Timetable tm : result.getTimetables()) {
								String startTime[] = tm.getStart_time().split(
										":");
								String endTime[] = tm.getEnd_time().split(":");
								int tmDow = Integer.parseInt(tm.getWeek_day());
								tmDow = tmDow == 7 ? 1 : tmDow + 1; // Android
																	// normalization
																	// SU:1,
																	// MO:2, ...
								Date startSimpleDate = result.getStart_date();
								startSimpleDate.setHours(Integer
										.parseInt(startTime[0]));
								startSimpleDate.setMinutes(Integer
										.parseInt(startTime[1]));
								Calendar startDate = new GregorianCalendar();
								startDate.setTime(startSimpleDate);
								Date endSimpleDate = result.getStart_date();
								endSimpleDate.setHours(Integer
										.parseInt(endTime[0]));
								endSimpleDate.setMinutes(Integer
										.parseInt(endTime[1]));
								Calendar endDate = new GregorianCalendar();
								endDate.setTime(endSimpleDate);

								int dow = startDate.get(Calendar.DAY_OF_WEEK);
								int offset = 0;
								if (dow > tmDow) {
									offset = 7 - Math.abs(dow - tmDow);
								} else if (dow < tmDow) {
									offset = tmDow - dow;
								}
								startDate.add(Calendar.DAY_OF_MONTH, offset);
								endDate.add(Calendar.DAY_OF_MONTH, offset);

								String RRULE = "FREQ=WEEKLY;BYDAY="
										+ rrule[tmDow - 1] + ";UNTIL=" + Until;

								MainActivity.addEventCorrect(
										element.getTitle(),
										startDate,
										endDate,
										tm.getDescription() == null ? "" : tm
												.getDescription(), RRULE, tm
												.getAddress(), context, result
												.getId());

							}
							for (Exam ex : result.getExams()) {
								String date[] = ex.getDate().split("-");
								String startTime[] = ex.getStart_time().split(
										":");
								String endTime[] = ex.getEnd_time().split(":");

								Calendar startDate = new GregorianCalendar(
										Integer.parseInt(date[0]), Integer
												.parseInt(date[1]) - 1, Integer
												.parseInt(date[2]), Integer
												.parseInt(startTime[0]),
										Integer.parseInt(startTime[1]), Integer
												.parseInt(startTime[2]));
								Calendar endDate = new GregorianCalendar(
										Integer.parseInt(date[0]), Integer
												.parseInt(date[1]) - 1, Integer
												.parseInt(date[2]), Integer
												.parseInt(endTime[0]), Integer
												.parseInt(endTime[1]), Integer
												.parseInt(endTime[2]));

								MainActivity.addEventCorrect(
										ex.getTitle(),
										startDate,
										endDate,
										ex.getDescription() == null ? "" : ex
												.getDescription(), "", ex
												.getAddress(), context, result
												.getId());
							}
							message += "A–adido calendario para "
									+ element.getTitle() + " con "
									+ result.getTimetables().length
									+ " eventos " + result.getExams().length
									+ " examenes " + " desde "
									+ result.getStart_date().toString()
									+ " hasta "
									+ result.getEnd_date().toString();
							Toast.makeText(context, message, Toast.LENGTH_LONG)
									.show();
						}
					});
		} else {
			Toast.makeText(context,
					"Esta asignatura no se est‡ impartiendo actualmente",
					Toast.LENGTH_LONG).show();
		}
	}

	private void removeCalendarEvent(SubjectSimple element) {
		// TODO Remove calendar event using the element id
		// TODO future: not select just the first tSubject but all the tSubjects associated to the subject
		if (element.getT_subject().length > 0) {
			MainActivity.deleteEventId(context, element.getT_subject()[0]);
			Toast.makeText(context,
					"Se ha eliminado la asignatura " + element.getTitle(),
					Toast.LENGTH_LONG).show();
		}
		
	}
}