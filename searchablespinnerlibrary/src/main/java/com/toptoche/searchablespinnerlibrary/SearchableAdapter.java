package com.toptoche.searchablespinnerlibrary;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchableAdapter<T> extends ArrayAdapter<T> {


    private ArrayList<T> mOriginalValues;
    private List<T> mObjects;
    private ArrayFilter mFilter;

    private final Object mLock = new Object();

    public SearchableAdapter(Context context, int resource, List<T> objects) {


        super(context, resource, objects);
        mObjects = objects;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    @Override
    public T getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {

            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<T>(mObjects);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<T> list;
                synchronized (mLock) {
                    list = new ArrayList<T>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
//                String prefixString = prefix.toString().toLowerCase();
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<T> values;
                synchronized (mLock) {
                    values = new ArrayList<T>(mOriginalValues);
                }

                final int count = values.size();
                final ArrayList<T> newValues = new ArrayList<T>();

                for (int i = 0; i < count; i++) {
                    final T value = values.get(i);
                    final String valueText = value.toString().toLowerCase();

                    // First match against the whole, non-splitted value
                    if (valueText.contains(prefixString)) {
                        newValues.add(value);
                    }
//                    if (valueText.startsWith(prefixString)) {
//                        newValues.add(value);
//                    } else {
//                        final String[] words = valueText.split(" ");
//                        final int wordCount = words.length;
//
//                        // Start at index 0, in case valueText starts with space(s)
//                        for (int k = 0; k < wordCount; k++) {
//                            if (words[k].startsWith(prefixString)) {
//                                newValues.add(value);
//                                break;
//                            }
//                        }
//                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mObjects = (List<T>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
