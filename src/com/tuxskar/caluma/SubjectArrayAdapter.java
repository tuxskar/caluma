package com.tuxskar.caluma;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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
  
  public void addCalendarEvent(SubjectSimple element){
	  Calendar calStart1 = new GregorianCalendar(2014, 11, 25, 10,
				0);
		calStart1.setTimeZone(MainActivity.CalendarsFragment.timezone);
		Calendar calEnd2 = new GregorianCalendar(2014, 11, 25, 12, 45);
		// Calendar calnew = new GregorianCalendar("20141126T1000");
		calEnd2.setTimeZone(MainActivity.CalendarsFragment.timezone);
		String eventString = MainActivity.CalendarsFragment.CalendarToString(new GregorianCalendar(
				2014, 11, 25, 0, 0));
		Log.d("Calendar Date in STRING:", eventString);
		MainActivity.addEventCorrect("Mates", calStart1, calEnd2,
    			"Matemticas aplicadas a la bioinformtica",
    			"FREQ=WEEKLY;BYDAY=MO,TU,WE"
    					+ ";UNTIL="
    					+ eventString,
    			"ETSII aula 3.0.6", context);
  }
  
  
} 