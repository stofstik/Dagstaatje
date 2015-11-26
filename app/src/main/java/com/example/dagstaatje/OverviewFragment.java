package com.example.dagstaatje;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stofstik.dagstaatje.R;

public class OverviewFragment extends Fragment {

    ViewPagerActivityInterface mMainActivityCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mMainActivityCallback = (ViewPagerActivityInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ViewPagerActivityInterface");
        }
    }

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String EURO_FORMAT = "€ %.2f";

    private View rootView;
    private TextView
            tvShift, tvStartAmount, tvExtra, tvTurnOver, tvTab, tvTabPaid,
            tvReport, tvTotalIn, tvOut, tvPin, tvTotalOut, tvResult,
            tvCounted, tvDifference, tvEnvelope, tvNew;
    private long countHowMuchUpdates = 0;

    public static Fragment newInstance(int sectionNumber) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        rootView = inflater.inflate(R.layout.layout_dagstaat_overview, container, false);
        initializeViews();
        return rootView;
    }

    public void initializeViews() {
        tvShift = (TextView) rootView.findViewById(R.id.tv_name_of_shift);
        tvStartAmount = (TextView) rootView.findViewById(R.id.tv_overview_start);
        tvExtra = (TextView) rootView.findViewById(R.id.tv_overview_extra);
        tvTurnOver = (TextView) rootView.findViewById(R.id.tv_overview_turnover);
        tvTab = (TextView) rootView.findViewById(R.id.tv_overview_tab);
        tvTabPaid = (TextView) rootView.findViewById(R.id.tv_overview_tab_paid);
        tvReport = (TextView) rootView.findViewById(R.id.tv_overview_report);
        tvTotalIn = (TextView) rootView.findViewById(R.id.tv_overview_total);
        tvOut = (TextView) rootView.findViewById(R.id.tv_overview_out);
        tvPin = (TextView) rootView.findViewById(R.id.tv_overview_pin);
        tvTotalOut = (TextView) rootView.findViewById(R.id.tv_overview_total_out);
        tvResult = (TextView) rootView.findViewById(R.id.tv_overview_result);
        tvCounted = (TextView) rootView.findViewById(R.id.tv_overview_counted);
        tvDifference = (TextView) rootView.findViewById(R.id.tv_overview_difference);
        tvEnvelope = (TextView) rootView.findViewById(R.id.tv_overview_envelope);
        tvNew = (TextView) rootView.findViewById(R.id.tv_overview_new);
    }

    /*
    Get the current dagstaat from the main activity and set the TextViews
     */
    public void update() {
        if (rootView == null) {
            Log.d("update", "rootView == null...");
            return;
        }
        Dagstaat dagstaat = mMainActivityCallback.getCurrentDagstaat();
        countHowMuchUpdates++;
        Log.d("update", countHowMuchUpdates + " dagstaat: " + dagstaat.toString());
        tvShift.setText(dagstaat.getShift());
        tvStartAmount.setText(String.format(EURO_FORMAT, dagstaat.getStart()));
        tvExtra.setText(String.format(EURO_FORMAT, dagstaat.getExtra()));
        tvTurnOver.setText(String.format(EURO_FORMAT, dagstaat.getTurnover()));
        tvTab.setText(String.format(EURO_FORMAT, dagstaat.getTab()));
        tvTabPaid.setText(String.format(EURO_FORMAT, dagstaat.getTabPaid()));
        tvReport.setText(String.format(EURO_FORMAT, dagstaat.getReport()));
        tvTotalIn.setText(String.format(EURO_FORMAT, dagstaat.getTotalIn()));
        tvOut.setText(String.format(EURO_FORMAT, dagstaat.getOut()));
        tvPin.setText(String.format(EURO_FORMAT, dagstaat.getPin()));
        tvTotalOut.setText(String.format(EURO_FORMAT, dagstaat.getTotalOut()));
        tvResult.setText(String.format(EURO_FORMAT, dagstaat.getResult()));
        tvCounted.setText(String.format(EURO_FORMAT, dagstaat.getCounted()));
        tvDifference.setText(String.format(EURO_FORMAT, dagstaat.getDifference()));
        tvEnvelope.setText(String.format(EURO_FORMAT, dagstaat.getEnvelope()));
        tvNew.setText(String.format(EURO_FORMAT, dagstaat.getNew()));
    }
}
