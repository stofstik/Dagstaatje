package com.example.dagstaatje;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stofstik.dagstaatje.R;

public class OverviewFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String EURO_FORMAT = "€ %.2f";

    private static View rootView;
    private static TextView tvStartAmount, tvExtra, tvTurnOver, tvTab, tvTabPaid, tvReport, tvTotalIn, tvOut,
            tvPin, tvTotalOut, tvResult, tvCounted, tvDifference, tvEnvelope,
            tvNew;

    public static Fragment newInstance(int sectionNumber) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_overview, container,
                false);
        initializeViews();
        update();
        return rootView;
    }

    public void initializeViews() {
        tvStartAmount = (TextView) rootView
                .findViewById(R.id.tvOverviewStartAmount);
        tvExtra = (TextView) rootView.findViewById(R.id.tvOverviewExtra);
        tvTurnOver = (TextView) rootView.findViewById(R.id.tvOverviewTurnOver);
        tvTab = (TextView) rootView.findViewById(R.id.tvOverviewTab);
        tvTabPaid = (TextView) rootView.findViewById(R.id.tvOverviewTabPaid);
        tvReport = (TextView) rootView.findViewById(R.id.tvOverviewReport);
        tvTotalIn = (TextView) rootView.findViewById(R.id.tvOverviewTotal);
        tvOut = (TextView) rootView.findViewById(R.id.tvOverviewOut);
        tvPin = (TextView) rootView.findViewById(R.id.tvOverviewPin);
        tvTotalOut = (TextView) rootView.findViewById(R.id.tvOverviewTotalOut);
        tvResult = (TextView) rootView.findViewById(R.id.tvOverviewResult);
        tvCounted = (TextView) rootView.findViewById(R.id.tvOverviewCounted);
        tvDifference = (TextView) rootView
                .findViewById(R.id.tvOverviewDifference);
        tvEnvelope = (TextView) rootView.findViewById(R.id.tvOverviewEnvelope);
        tvNew = (TextView) rootView.findViewById(R.id.tvOverviewNew);
    }

    public static void update() {
        // Use this if statement to check if the root view is created. update()
        // can get called from another fragment or activity on boot without this
        // root view created. Which results in a nullPointerException.
        if (rootView != null) {
            tvStartAmount.setText(String.format(EURO_FORMAT,
                    InputFragment.dStart));
            tvExtra.setText(String.format(EURO_FORMAT,
                    InputFragment.dTotalExtra));
            tvTurnOver.setText(String.format(EURO_FORMAT, InputFragment.dTurnOver));
            tvTab.setText(String.format(EURO_FORMAT, InputFragment.dTab));
            tvTabPaid.setText(String.format(EURO_FORMAT, InputFragment.dTabPaid));
            tvReport.setText(String.format(EURO_FORMAT, InputFragment.dReport));
            tvTotalIn.setText(String
                    .format(EURO_FORMAT, InputFragment.dTotalIn));
            tvOut.setText(String.format(EURO_FORMAT, InputFragment.dTotalOut));
            tvPin.setText(String.format(EURO_FORMAT, InputFragment.dPin));
            tvTotalOut.setText(String.format(EURO_FORMAT,
                    InputFragment.dTotalOutInclPin));
            tvResult.setText(String.format(EURO_FORMAT, InputFragment.dResult));
            tvCounted.setText(String
                    .format(EURO_FORMAT, InputFragment.dCounted));
            tvDifference.setText(String.format(EURO_FORMAT,
                    InputFragment.dDifference));
            tvEnvelope.setText(String.format(EURO_FORMAT,
                    InputFragment.dEnvelope));
            tvNew.setText(String
                    .format(EURO_FORMAT, InputFragment.dNewRegister));
        }
    }
}
