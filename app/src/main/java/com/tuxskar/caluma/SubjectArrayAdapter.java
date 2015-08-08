package com.tuxskar.caluma;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SubjectArrayAdapter extends ArrayAdapter<SubjectSimple> {

    private final Activity context;

    public SubjectArrayAdapter(Activity context, List<SubjectSimple> subjects) {
        super(context, R.layout.subject_row, subjects);
        this.context = context;
    }

    static class ViewHolder {
        protected TextView text;
        protected CheckBox checkbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            rowView = inflator.inflate(R.layout.subject_row, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.label);
            viewHolder.checkbox = (CheckBox) rowView.findViewById(R.id.check);
            viewHolder.checkbox.setTag(getItem(position));
            rowView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) rowView.getTag();
        SubjectSimple element = getItem(position);
        holder.text.setText(element.getTitle());
        boolean ongoing = element.getT_subject().length > 0;
        holder.checkbox.setEnabled(ongoing);
        holder.checkbox
                .setOnClickListener(new CompoundButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SubjectSimple element = (SubjectSimple) v.getTag();
                        boolean isChecked = ((CheckBox) v).isChecked();
                        if (isChecked) {
                            // add calendar event
                            boolean ret = addCalendarEvent(element);
                            ((CheckBox) v).setChecked(ret);
                            element.setSelected(ret);
                        } else {
                            removeCalendarEvent(element);
                            ((CheckBox) v).setChecked(false);
                            element.setSelected(false);
                        }
                    }
                });
        return rowView;
    }

    public boolean addCalendarEvent(final SubjectSimple element) {
        // TODO future: ask for the user to select the teachingSubjects that
        // fits
        // better choosing level and course
        if (element.getT_subject().length > 0) {
            StudentHomeActivity.SubjectsSearcherFragment.service.getTSubject(
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
                            String rrule[] = {"SU", "MO", "TU", "WE", "TH",
                                    "FR", "SA"};
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

                                StudentHomeActivity.addEventCorrect(
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

                                StudentHomeActivity.addEventCorrect(
                                        ex.getTitle(),
                                        startDate,
                                        endDate,
                                        ex.getDescription() == null ? "" : ex
                                                .getDescription(), "", ex
                                                .getAddress(), context, result
                                                .getId());
                            }
                            message += "Añadido calendario para "
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
            return true;
        } else {
            Toast.makeText(context,
                    "Esta asignatura no se está impartiendo actualmente " + element.getTitle(),
                    Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void removeCalendarEvent(SubjectSimple element) {
        // TODO Remove calendar event using the element id
        // TODO future: not select just the first tSubject but all the tSubjects
        // associated to the subject
        if (element.getT_subject().length > 0) {
            StudentHomeActivity.deleteEventId(context, element.getT_subject()[0]);
            Toast.makeText(context,
                    "Se ha eliminado la asignatura " + element.getTitle(),
                    Toast.LENGTH_LONG).show();
        }

    }
}