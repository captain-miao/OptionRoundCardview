package com.github.captain_miao.optionroundcardview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.captain_miao.optroundcardview.OptRoundCardView;

/**
 * @author YanLu
 * @since 17/9/3
 */

public class PlaceholderFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int section = getArguments().getInt(ARG_SECTION_NUMBER);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        OptRoundCardView roundCardView;
        switch (section){
            case 1:
                roundCardView = (OptRoundCardView) rootView.findViewById(R.id.bottom_card_view);
                roundCardView.showCorner(false, false, false, false);
                break;
            case 2:
                roundCardView = (OptRoundCardView) rootView.findViewById(R.id.top_card_view);
                roundCardView.showCorner(false, false, false, false);
                break;
            default:
                break;
        }


        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format, section));
        return rootView;
    }
}
