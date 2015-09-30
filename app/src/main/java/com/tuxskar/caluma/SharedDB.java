package com.tuxskar.caluma;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SharedDB {
    public static String lastImagePath = "";
    static Map<Long, ArrayList<Long>> tsubjectsIds;
    Context mContext;
    SharedPreferences preferences;
    String DEFAULT_APP_IMAGEDATA_DIRECTORY;
    File mFolder = null;

    public SharedDB(Context appContext) {
        mContext = appContext;
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        // tsubjectsIds map initialization using singleton pattern
        if (SharedDB.tsubjectsIds == null) {
            SharedDB.tsubjectsIds = getIDMap(appContext
                    .getString(R.string.TSUBJECTIDS));
        }
    }

    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }

    public long getLong(String key) {
        return preferences.getLong(key, 0l);
    }

    public String getString(String key) {
        return preferences.getString(key, "");
    }

    public double getDouble(String key) {
        String number = getString(key);
        try {
            double value = Double.parseDouble(number);
            return value;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void putLong(String key, long value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void putDouble(String key, double value) {
        putString(key, String.valueOf(value));
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void putList(String key, ArrayList<String> marray) {
        SharedPreferences.Editor editor = preferences.edit();
        String[] mystringlist = marray.toArray(new String[marray.size()]);
        // the comma like character used below is not a comma it is the SINGLE
        // LOW-9 QUOTATION MARK unicode 201A and unicode 2017 they are used for
        // seprating the items in the list
        editor.putString(key, TextUtils.join("‚‗‚", mystringlist));
        editor.apply();
    }

    public void putIDMap(String key, Map<Long, ArrayList<Long>> mIDs) {
        // The shared preference list is a list of string with type like
        // "<tsubjectId>-<eventAntdroidID>"
        if (mIDs == null) {
            mIDs = SharedDB.tsubjectsIds;
        }
        ArrayList<String> mystringlist = new ArrayList<String>();
        for (Entry<Long, ArrayList<Long>> ma : mIDs.entrySet()) {
            String tsubjectID = String.valueOf(ma.getKey());
            for (Long eventId : ma.getValue()) {
                mystringlist.add(tsubjectID + "-" + String.valueOf(eventId));
            }
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, TextUtils.join(";", mystringlist));
        editor.apply();
    }

    @SuppressLint("UseSparseArrays")
    public Map<Long, ArrayList<Long>> getIDMap(String key) {
        // TODO: check performance using LongSparseArrays instead of HasMap
        // The shared preference list is a list of string with type like
        // "<tsubjectId>-<eventAntdroidID>"
        String[] pairStrings = TextUtils.split(preferences.getString(key, ""),
                ";");
        Map<Long, ArrayList<Long>> finalmap = new HashMap<Long, ArrayList<Long>>();

        Long subjectId, eventId;
        for (String pair : pairStrings) {
            String[] longPair = pair.split("-");
            subjectId = Long.parseLong(longPair[0]);
            eventId = Long.parseLong(longPair[1]);
            if (finalmap.containsKey(subjectId)) {
                ArrayList<Long> events = finalmap.get(subjectId);
                events.add(eventId);
            } else {
                ArrayList<Long> initial = new ArrayList<Long>();
                initial.add(eventId);
                finalmap.put(subjectId, initial);
            }
        }
        return finalmap;
    }

    public ArrayList<String> getList(String key) {
        // the comma like character used below is not a comma it is the SINGLE
        // LOW-9 QUOTATION MARK unicode 201A and unicode 2017 they are used for
        // seprating the items in the list
        String[] mylist = TextUtils
                .split(preferences.getString(key, ""), "‚‗‚");
        ArrayList<String> gottenlist = new ArrayList<String>(
                Arrays.asList(mylist));
        return gottenlist;
    }

    public void putListInt(String key, ArrayList<Integer> marray) {
        SharedPreferences.Editor editor = preferences.edit();
        Integer[] mystringlist = marray.toArray(new Integer[marray.size()]);
        // the comma like character used below is not a comma it is the SINGLE
        // LOW-9 QUOTATION MARK unicode 201A and unicode 2017 they are used for
        // seprating the items in the list
        editor.putString(key, TextUtils.join("‚‗‚", mystringlist));
        editor.apply();
    }

    public ArrayList<Integer> getListInt(String key) {
        // the comma like character used below is not a comma it is the SINGLE
        // LOW-9 QUOTATION MARK unicode 201A and unicode 2017 they are used for
        // seprating the items in the list
        String[] mylist = TextUtils
                .split(preferences.getString(key, ""), "‚‗‚");
        ArrayList<String> gottenlist = new ArrayList<String>(
                Arrays.asList(mylist));
        ArrayList<Integer> gottenlist2 = new ArrayList<Integer>();
        for (int i = 0; i < gottenlist.size(); i++) {
            gottenlist2.add(Integer.parseInt(gottenlist.get(i)));
        }

        return gottenlist2;
    }

    public void putListBoolean(String key, ArrayList<Boolean> marray) {
        ArrayList<String> origList = new ArrayList<String>();
        for (Boolean b : marray) {
            if (b == true) {
                origList.add("true");
            } else {
                origList.add("false");
            }
        }
        putList(key, origList);
    }

    public ArrayList<Boolean> getListBoolean(String key) {
        ArrayList<String> origList = getList(key);
        ArrayList<Boolean> mBools = new ArrayList<Boolean>();
        for (String b : origList) {
            if (b.equals("true")) {
                mBools.add(true);
            } else {
                mBools.add(false);
            }
        }
        return mBools;
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public void putFloat(String key, float value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public float getFloat(String key) {
        return preferences.getFloat(key, 0f);
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public Boolean deleteImage(String path) {
        File tobedeletedImage = new File(path);
        Boolean isDeleted = tobedeletedImage.delete();
        return isDeleted;
    }

    public void clear() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public Map<String, ?> getAll() {
        return preferences.getAll();
    }

    public void registerOnSharedPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void saveID(long id, Long eventId) {
        // Save a tsubject event on the tsubjects map
        if (SharedDB.tsubjectsIds.containsKey(id)) {
            SharedDB.tsubjectsIds.get(id).add(eventId);
        } else {
            ArrayList<Long> lEventId = new ArrayList<Long>();
            lEventId.add(eventId);
            SharedDB.tsubjectsIds.put(id, lEventId);
        }
    }

    public boolean savedTSubject(long id) {
        return SharedDB.tsubjectsIds.containsKey(id);
    }

    public void removeTSubject(long tSubjectId) {
        SharedDB.tsubjectsIds.remove(tSubjectId);
    }

    public ArrayList<Long> getEventIds(long tSubjectId) {
        // Returns the arraList associated to the tSubject argument
        return SharedDB.tsubjectsIds.containsKey(tSubjectId) ? SharedDB.tsubjectsIds
                .get(tSubjectId) : new ArrayList<Long>();
    }
}
