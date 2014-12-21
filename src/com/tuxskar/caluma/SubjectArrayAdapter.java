package com.tuxskar.caluma;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.tuxskar.caluma.ws.models.SubjectSimple;

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
			viewHolder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							SubjectSimple element = (SubjectSimple) viewHolder.checkbox
									.getTag();
							Log.d("ischecked", String.valueOf(isChecked));
							Log.d("element", element.toString());
							// add calendar event
							addCalendarEvent(element);

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

	public void addCalendarEvent(SubjectSimple element) {
		// TODO: crear la clase t_subject
		// TODO futuro: sacar todos los t_subject y dejar al usuario elegir cual
		// quiere
		// Get el primer t_subject para este element solo si tiene algœn
		// t_suject
		// setear el dstart y dtend para todos los eventos
		// por cada timetable a–adir un evento peri—dico
		// por cada exam a–adir un evento peri—dico

		Calendar calStart1 = new GregorianCalendar(2014, 11, 15, 10, 0);
		Calendar calEnd2 = new GregorianCalendar(2014, 11, 15, 12, 45);
		String rrule[] = { "MO", "TU", "WE", "TH", "FR", "SA", "SU" };
		Calendar today = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String Until = sdf.format(today.getTime());
		String RRULE = "FREQ=WEEKLY;BYDAY=" + rrule[0] + ";UNTIL=" + Until;

		MainActivity.addEventCorrect(element.getTitle(), calStart1, calEnd2,
				"descripttionnn " + element.getDescription() == null ? ""
						: element.getDescription(), RRULE, "ETSII aula 3.0.6",
				context);
	}

}