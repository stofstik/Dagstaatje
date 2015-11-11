package com.example.dagstaatje;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.stofstik.dagstaatje.R;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class CountFragment extends Fragment implements TextWatcher, View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static View rootView;

    private SharedPreferences sharedPreferences;

    private static int i500Value, i200Value, i100Value, i50Value, i20Value, i10Value, i5Value, i2Value,
            i1Value, i50centValue, i20centValue, i10centValue, i5centValue, iBills, iCoins;

    private static double d500Result, d200Result, d100Result, d50Result, d20Result, d10Result, d5Result,
            d2Result, d1Result, d50centResult, d20centResult, d10centResult, d5centResult, dTotal, dEnvelope;

    private static Button b500p, b200p, b100p, b50p, b20p, b10p, b5p, b2p, b1p, b50cp, b20cp, b10cp, b5cp;
    private static Button b500m, b200m, b100m, b50m, b20m, b10m, b5m, b2m, b1m, b50cm, b20cm, b10cm, b5cm;
    private static EditText et500, et200, et100, et50, et20, et10, et5, et2, et1, et50c, et20c, et10c, et5c;
    private static TextView tv500, tv200, tv100, tv50, tv20, tv10, tv5, tv2, tv1, tv50c, tv20c, tv10c, tv5c,
            tvTotal, tvCoins, tvBills;

    private static boolean listen;

    private static final String KEY_500 = "500";
    private static final String KEY_200 = "200";
    private static final String KEY_100 = "100";
    private static final String KEY_50 = "50";
    private static final String KEY_20 = "20";
    private static final String KEY_10 = "10";
    private static final String KEY_5 = "5";
    private static final String KEY_2 = "2";
    private static final String KEY_1 = "1";
    private static final String KEY_50C = "50C";
    private static final String KEY_20C = "20C";
    private static final String KEY_10C = "10C";
    private static final String KEY_5C = "5C";
    private static final String EURO_FORMAT = "€ %.2f";

    public static Fragment newInstance(int sectionNumber) {
        CountFragment fragment = new CountFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /*
     * we use onActivityCreated as onResume caused loadSharedPreferences to be
     * run twice.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        initializeViews();
        LoadSavedEditTexts();
        setFocus();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_count, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_count:
                this.clearAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_count, container,
                false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setFocus();
    }

    public void onPause() {
        super.onPause();
        saveEditTexts();
    }

    public void initializeViews() {
        b500m = (Button) rootView.findViewById(R.id.b500min);
        b200m = (Button) rootView.findViewById(R.id.b200min);
        b100m = (Button) rootView.findViewById(R.id.b100min);
        b50m = (Button) rootView.findViewById(R.id.b50min);
        b20m = (Button) rootView.findViewById(R.id.b20min);
        b10m = (Button) rootView.findViewById(R.id.b10min);
        b5m = (Button) rootView.findViewById(R.id.b5min);
        b2m = (Button) rootView.findViewById(R.id.b2min);
        b1m = (Button) rootView.findViewById(R.id.b1min);
        b50cm = (Button) rootView.findViewById(R.id.b50cmin);
        b20cm = (Button) rootView.findViewById(R.id.b20cmin);
        b10cm = (Button) rootView.findViewById(R.id.b10cmin);
        b5cm = (Button) rootView.findViewById(R.id.b5cmin);

        b500p = (Button) rootView.findViewById(R.id.b500plus);
        b200p = (Button) rootView.findViewById(R.id.b200plus);
        b100p = (Button) rootView.findViewById(R.id.b100plus);
        b50p = (Button) rootView.findViewById(R.id.b50plus);
        b20p = (Button) rootView.findViewById(R.id.b20plus);
        b10p = (Button) rootView.findViewById(R.id.b10plus);
        b5p = (Button) rootView.findViewById(R.id.b5plus);
        b2p = (Button) rootView.findViewById(R.id.b2plus);
        b1p = (Button) rootView.findViewById(R.id.b1plus);
        b50cp = (Button) rootView.findViewById(R.id.b50cplus);
        b20cp = (Button) rootView.findViewById(R.id.b20cplus);
        b10cp = (Button) rootView.findViewById(R.id.b10cplus);
        b5cp = (Button) rootView.findViewById(R.id.b5cplus);

        et500 = (EditText) rootView.findViewById(R.id.euro500);
        et200 = (EditText) rootView.findViewById(R.id.euro200);
        et100 = (EditText) rootView.findViewById(R.id.euro100);
        et50 = (EditText) rootView.findViewById(R.id.euro50);
        et20 = (EditText) rootView.findViewById(R.id.euro20);
        et10 = (EditText) rootView.findViewById(R.id.euro10);
        et5 = (EditText) rootView.findViewById(R.id.euro5);
        et2 = (EditText) rootView.findViewById(R.id.euro2);
        et1 = (EditText) rootView.findViewById(R.id.euro1);
        et50c = (EditText) rootView.findViewById(R.id.cent50);
        et20c = (EditText) rootView.findViewById(R.id.cent20);
        et10c = (EditText) rootView.findViewById(R.id.cent10);
        et5c = (EditText) rootView.findViewById(R.id.cent5);

        tv500 = (TextView) rootView.findViewById(R.id.tvEuro500Totaal);
        tv200 = (TextView) rootView.findViewById(R.id.tvEuro200Totaal);
        tv100 = (TextView) rootView.findViewById(R.id.tvEuro100Totaal);
        tv50 = (TextView) rootView.findViewById(R.id.tvEuro50Totaal);
        tv20 = (TextView) rootView.findViewById(R.id.tvEuro20Totaal);
        tv10 = (TextView) rootView.findViewById(R.id.tvEuro10Totaal);
        tv5 = (TextView) rootView.findViewById(R.id.tvEuro5Totaal);
        tv2 = (TextView) rootView.findViewById(R.id.tvEuro2Totaal);
        tv1 = (TextView) rootView.findViewById(R.id.tvEuro1Totaal);
        tv50c = (TextView) rootView.findViewById(R.id.tvCent50Totaal);
        tv20c = (TextView) rootView.findViewById(R.id.tvCent20Totaal);
        tv10c = (TextView) rootView.findViewById(R.id.tvCent10Totaal);
        tv5c = (TextView) rootView.findViewById(R.id.tvCent5Totaal);
        tvTotal = (TextView) rootView.findViewById(R.id.tvTotaalBedrag);
        tvBills = (TextView) rootView.findViewById(R.id.tvTotaalBriefjesBedrag);
        tvCoins = (TextView) rootView.findViewById(R.id.tvTotaalMuntenBedrag);

        listen = false;
        et500.addTextChangedListener(this);
        et200.addTextChangedListener(this);
        et100.addTextChangedListener(this);
        et50.addTextChangedListener(this);
        et20.addTextChangedListener(this);
        et10.addTextChangedListener(this);
        et5.addTextChangedListener(this);
        et2.addTextChangedListener(this);
        et1.addTextChangedListener(this);
        et50c.addTextChangedListener(this);
        et20c.addTextChangedListener(this);
        et10c.addTextChangedListener(this);
        et5c.addTextChangedListener(this);
        listen = true;

        b500p.setOnClickListener(this);
        b200p.setOnClickListener(this);
        b100p.setOnClickListener(this);
        b50p.setOnClickListener(this);
        b20p.setOnClickListener(this);
        b10p.setOnClickListener(this);
        b5p.setOnClickListener(this);
        b2p.setOnClickListener(this);
        b1p.setOnClickListener(this);
        b50cp.setOnClickListener(this);
        b20cp.setOnClickListener(this);
        b10cp.setOnClickListener(this);
        b5cp.setOnClickListener(this);

        b500m.setOnClickListener(this);
        b200m.setOnClickListener(this);
        b100m.setOnClickListener(this);
        b50m.setOnClickListener(this);
        b20m.setOnClickListener(this);
        b10m.setOnClickListener(this);
        b5m.setOnClickListener(this);
        b2m.setOnClickListener(this);
        b1m.setOnClickListener(this);
        b50cm.setOnClickListener(this);
        b20cm.setOnClickListener(this);
        b10cm.setOnClickListener(this);
        b5cm.setOnClickListener(this);

    }

    // sla variabelen op in SharedPreferences
    public void saveEditTexts() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_500, et500.getText().toString());
        editor.putString(KEY_200, et200.getText().toString());
        editor.putString(KEY_100, et100.getText().toString());
        editor.putString(KEY_50, et50.getText().toString());
        editor.putString(KEY_20, et20.getText().toString());
        editor.putString(KEY_10, et10.getText().toString());
        editor.putString(KEY_5, et5.getText().toString());
        editor.putString(KEY_2, et2.getText().toString());
        editor.putString(KEY_1, et1.getText().toString());
        editor.putString(KEY_50C, et50c.getText().toString());
        editor.putString(KEY_20C, et20c.getText().toString());
        editor.putString(KEY_10C, et10c.getText().toString());
        editor.putString(KEY_5C, et5c.getText().toString());
        editor.apply();
    }

    // laad variabelen van SharedPreferences
    private void LoadSavedEditTexts() {
        et500.setText(sharedPreferences.getString(KEY_500, ""));
        et200.setText(sharedPreferences.getString(KEY_200, ""));
        et100.setText(sharedPreferences.getString(KEY_100, ""));
        et50.setText(sharedPreferences.getString(KEY_50, ""));
        et20.setText(sharedPreferences.getString(KEY_20, ""));
        et10.setText(sharedPreferences.getString(KEY_10, ""));
        et5.setText(sharedPreferences.getString(KEY_5, ""));
        et2.setText(sharedPreferences.getString(KEY_2, ""));
        et1.setText(sharedPreferences.getString(KEY_1, ""));
        et50c.setText(sharedPreferences.getString(KEY_50C, ""));
        et20c.setText(sharedPreferences.getString(KEY_20C, ""));
        et10c.setText(sharedPreferences.getString(KEY_10C, ""));
        et5c.setText(sharedPreferences.getString(KEY_5C, ""));
    }

    // zet focus
    private static void setFocus() {
        et50.requestFocus();
        et50.setSelection(et50.getText().length());
    }

    public static void clearAll() {
        listen = false;
        et500.setText("");
        et200.setText("");
        et100.setText("");
        et50.setText("");
        et20.setText("");
        et10.setText("");
        et5.setText("");
        et2.setText("");
        et1.setText("");
        et50c.setText("");
        et20c.setText("");
        et10c.setText("");
        et5c.setText("");
        listen = true;
        new CalcTask().execute();
        setFocus();
    }

    // EditText++
    public void incrementEditText(EditText et) {
        String tmp = et.getText().toString();
        if (tmp.matches("")) {
            tmp = "0";
        }
        int iTmp = Integer.parseInt(tmp);
        iTmp++;
        et.setText("" + iTmp);
        et.setSelection(et.getText().length());
    }

    // EditText--
    public void decrementEditText(EditText et) {
        String tmp = et.getText().toString();
        if (tmp.matches("")) {
            tmp = "0";
        }
        int iTmp = Integer.parseInt(tmp);
        if (iTmp >= 1) {
            iTmp--;
            et.setText("" + iTmp);
        } else {
            et.setText("");
        }
        et.setSelection(et.getText().length());
    }

    public static int iEtContent(EditText et) {
        try {
            return Integer.parseInt(et.getText().toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private static class CalcTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            i500Value = iEtContent(et500);
            i200Value = iEtContent(et200);
            i100Value = iEtContent(et100);
            i50Value = iEtContent(et50);
            i20Value = iEtContent(et20);
            i10Value = iEtContent(et10);
            i5Value = iEtContent(et5);
            i2Value = iEtContent(et2);
            i1Value = iEtContent(et1);
            i50centValue = iEtContent(et50c);
            i20centValue = iEtContent(et20c);
            i10centValue = iEtContent(et10c);
            i5centValue = iEtContent(et5c);
        }

        @Override
        protected Void doInBackground(Void... params) {
            d500Result = i500Value * 500;
            d200Result = i200Value * 200;
            d100Result = i100Value * 100;
            d50Result = i50Value * 50;
            d20Result = i20Value * 20;
            d10Result = i10Value * 10;
            d5Result = i5Value * 5;
            d2Result = i2Value * 2;
            d1Result = i1Value;
            d50centResult = i50centValue * 0.5;
            d20centResult = i20centValue * 0.2;
            d10centResult = i10centValue * 0.1;
            d5centResult = i5centValue * 0.05;

            iBills = 0;
            iBills += i500Value;
            iBills += i200Value;
            iBills += i100Value;
            iBills += i50Value;
            iBills += i20Value;
            iBills += i10Value;
            iBills += i5Value;

            iCoins = 0;
            iCoins += i2Value;
            iCoins += i1Value;
            iCoins += i50centValue;
            iCoins += i20centValue;
            iCoins += i10centValue;
            iCoins += i5centValue;

            dTotal = 0;
            dTotal += d500Result;
            dTotal += d200Result;
            dTotal += d100Result;
            dTotal += d50Result;
            dTotal += d20Result;
            dTotal += d10Result;
            dTotal += d5Result;
            dTotal += d2Result;
            dTotal += d1Result;
            dTotal += d50centResult;
            dTotal += d20centResult;
            dTotal += d10centResult;
            dTotal += d5centResult;

            dEnvelope = 0;
            dEnvelope += d500Result;
            dEnvelope += d200Result;
            dEnvelope += d100Result;
            dEnvelope += d50Result;
            if (d20Result >= 100) {
                dEnvelope += d20Result - 100;
            }
            if (d10Result >= 50) {
                dEnvelope += d10Result - 50;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tv500.setText(String.format(Locale.UK, EURO_FORMAT, d500Result));
            tv200.setText(String.format(EURO_FORMAT, d200Result));
            tv100.setText(String.format(EURO_FORMAT, d100Result));
            tv50.setText(String.format(EURO_FORMAT, d50Result));
            tv20.setText(String.format(EURO_FORMAT, d20Result));
            tv10.setText(String.format(EURO_FORMAT, d10Result));
            tv5.setText(String.format(EURO_FORMAT, d5Result));
            tv2.setText(String.format(EURO_FORMAT, d2Result));
            tv1.setText(String.format(EURO_FORMAT, d1Result));
            tv50c.setText(String.format(EURO_FORMAT, d50centResult));
            tv20c.setText(String.format(EURO_FORMAT, d20centResult));
            tv10c.setText(String.format(EURO_FORMAT, d10centResult));
            tv5c.setText(String.format(EURO_FORMAT, d5centResult));

            tvBills.setText("" + iBills);
            tvCoins.setText("" + iCoins);
            tvTotal.setText(String.format(EURO_FORMAT, dTotal));

            if (dTotal != 0) {
                InputFragment.dCounted = dTotal;
                if (InputFragment.etCounted != null) { // etCounted can be null if the fragment is not yet created
                    InputFragment.etCounted.setText((String.format(Locale.UK, "%.2f", dTotal))); // use locale uk, else we won't be able to parse double later because of comma
                }
            }

            if (dEnvelope != 0) {
                InputFragment.dEnvelope = dEnvelope;
                if (InputFragment.etEnvelope != null) {
                    InputFragment.etEnvelope.setText((String.format(Locale.UK, "%.2f", dEnvelope)));
                }
            }
        }
    }

    // doe dit als er tekst veranderd
    @Override
    public void afterTextChanged(Editable args) {
        if (listen) {
            new CalcTask().execute();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b500plus:
                incrementEditText(et500);
                break;
            case R.id.b200plus:
                incrementEditText(et200);
                break;
            case R.id.b100plus:
                incrementEditText(et100);
                break;
            case R.id.b50plus:
                incrementEditText(et50);
                break;
            case R.id.b20plus:
                incrementEditText(et20);
                break;
            case R.id.b10plus:
                incrementEditText(et10);
                break;
            case R.id.b5plus:
                incrementEditText(et5);
                break;
            case R.id.b2plus:
                incrementEditText(et2);
                break;
            case R.id.b1plus:
                incrementEditText(et1);
                break;
            case R.id.b50cplus:
                incrementEditText(et50c);
                break;
            case R.id.b20cplus:
                incrementEditText(et20c);
                break;
            case R.id.b10cplus:
                incrementEditText(et10c);
                break;
            case R.id.b5cplus:
                incrementEditText(et5c);
                break;
            case R.id.b500min:
                decrementEditText(et500);
                break;
            case R.id.b200min:
                decrementEditText(et200);
                break;
            case R.id.b100min:
                decrementEditText(et100);
                break;
            case R.id.b50min:
                decrementEditText(et50);
                break;
            case R.id.b20min:
                decrementEditText(et20);
                break;
            case R.id.b10min:
                decrementEditText(et10);
                break;
            case R.id.b5min:
                decrementEditText(et5);
                break;
            case R.id.b2min:
                decrementEditText(et2);
                break;
            case R.id.b1min:
                decrementEditText(et1);
                break;
            case R.id.b50cmin:
                decrementEditText(et50c);
                break;
            case R.id.b20cmin:
                decrementEditText(et20c);
                break;
            case R.id.b10cmin:
                decrementEditText(et10c);
                break;
            case R.id.b5cmin:
                decrementEditText(et5c);
                break;
        }
    }
}