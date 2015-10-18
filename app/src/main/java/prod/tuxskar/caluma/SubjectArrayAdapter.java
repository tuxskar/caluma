package prod.tuxskar.caluma;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import prod.tuxskar.caluma.ws.models.Exam;
import prod.tuxskar.caluma.ws.models.SimpleInfo;
import prod.tuxskar.caluma.ws.models.SubjectSimple;
import prod.tuxskar.caluma.ws.models.TeachingSubject;
import prod.tuxskar.caluma.ws.models.Timetable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SubjectArrayAdapter extends ArrayAdapter<SubjectSimple> {

    private final Activity context;
    private CharSequence[] class_options;

    public SubjectArrayAdapter(Activity context, List<SubjectSimple> subjects) {
        super(context, R.layout.subject_row, subjects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        SubjectSimple element = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.subject_row, parent, false);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.label);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.check);
            holder.checkbox.setTag(getItem(position));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.text.setText(element.getTitle());
        boolean ongoing = element.getT_subject().length > 0;
        holder.checkbox.setEnabled(ongoing);
        boolean selected = element.isSelected();
        holder.checkbox.setChecked(selected);
        holder.checkbox.setTag(element);
        holder.checkbox
                .setOnClickListener(new CompoundButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SubjectSimple element = (SubjectSimple) v.getTag();
                        String subject_id = Long.toString(element.getId());
                        boolean isChecked = ((CheckBox) v).isChecked();
                        ((CheckBox) v).setChecked(element.isSelected());
                        if (isChecked) {
                            // add calendar event
                            selectTSubjectClass(element, (CheckBox) v);
                        } else {
                            removeCalendarEvent(element);
                            ((CheckBox) v).setChecked(false);
                            element.setSelected(false);
                            ArrayList<String> selectedSubjects = LoginActivity.sharedDB.getList("TSUBJECTS_SELECTED");
                            selectedSubjects.remove(subject_id);
                            LoginActivity.sharedDB.putList("TSUBJECTS_SELECTED", selectedSubjects);
                        }
                    }
                });
        return convertView;
    }

    public void selectTSubjectClass(final SubjectSimple element, final CheckBox v) {
        if (element.getT_subject().length > 0) {
            final SimpleInfo[] t_subjects_ids = element.getT_subject();
            Arrays.sort(t_subjects_ids);
            class_options = new CharSequence[element.getT_subject().length];
            for (int i = 0; i < t_subjects_ids.length; i++) {
                class_options[i] = t_subjects_ids[i].getTitle();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            final String selected_subject_title = element.getTitle();
            builder.setTitle(R.string.pick_subject_class)
                    .setItems(class_options, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            addTSubjectToCalendar(t_subjects_ids[which].getId(),
                                    selected_subject_title, v, element);
                        }
                    });
            builder.create();
            builder.show();
        } else {
            Toast.makeText(context,
                    "Esta asignatura no se está impartiendo actualmente " + element.getTitle(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void addTSubjectToCalendar(final long t_subject_id, final String selected_subject_title,
                                       final CheckBox v, final SubjectSimple element) {
        LoginActivity.getUserService(context).getTSubject(
                t_subject_id, new Callback<TeachingSubject>() {
                    @Override
                    public void failure(RetrofitError arg0) {
                        Log.d("fail getting TSubject", arg0
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
                                    selected_subject_title,
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
                        // Check the checkbox as selected subject
                        v.setChecked(true);
                        // Save element selected persistently and in memory
                        element.setSelected(true);
                        ArrayList<String> selectedSubjects = LoginActivity.sharedDB.getList("TSUBJECTS_SELECTED");
                        selectedSubjects.add(Long.toString(element.getId()));
                        LoginActivity.sharedDB.putList("TSUBJECTS_SELECTED", selectedSubjects);

                        // Notify the user that a subject have been saved
                        message += "Añadido calendario para "
                                + selected_subject_title + " con "
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
    }

    private void removeCalendarEvent(SubjectSimple element) {
        // associated to the subject
        if (element.getT_subject().length > 0) {
            for (SimpleInfo t_subject_id : element.getT_subject()) {
                StudentHomeActivity.deleteEventId(context, t_subject_id.getId());
            }
            Toast.makeText(context,
                    "Se ha eliminado la asignatura " + element.getTitle(),
                    Toast.LENGTH_LONG).show();
        }
    }

    static class ViewHolder {
        protected TextView text;
        protected CheckBox checkbox;
    }
}