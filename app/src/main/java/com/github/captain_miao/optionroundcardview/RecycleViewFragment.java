package com.github.captain_miao.optionroundcardview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author YanLu
 * @since 17/9/3
 */

public class RecycleViewFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private RecyclerView mRecyclerView;
    private SimpleAdapter mAdapter;
    public RecycleViewFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RecycleViewFragment newInstance(int sectionNumber) {
        RecycleViewFragment fragment = new RecycleViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //int section = getArguments().getInt(ARG_SECTION_NUMBER);
        View rootView = inflater.inflate(R.layout.fragment_rv, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new SimpleAdapter();
        mAdapter.add("jordyamc");
        mAdapter.add("jordyamc");
        mAdapter.add("jordyamc");
        mAdapter.add("jordyamc");
        mAdapter.add("jordyamc");
        mAdapter.add("jordyamc");
        mAdapter.add("jordyamc");
        mAdapter.add("jordyamc");
        mAdapter.add("jordyamc");
        mAdapter.add("jordyamc");
        mAdapter.add("jordyamc");
        mAdapter.add("jordyamc");
        mAdapter.add("jordyamc");
        mAdapter.add("jordyamc");
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }
}
