package com.example.dagstaatje;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stofstik.dagstaatje.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class InputFragment extends Fragment implements TextWatcher,
        OnClickListener {

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

    private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-M-d H:m:s");

    /*
     We use this boolean to improve performance by only listening to EditText when we want.
     For example if we clear all texts, it is going to calculate with every .setText("")
     */
    private boolean mListen = false;

    /*
     * Used for the ViewPager
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /*
     * Used for string formatter, formats to € 0.00
     */
    private static final String EURO_FORMAT = "€ %.2f";

    /*
     * Keys used for SharedPreferences
     */
    private static final String KEY_DYNAMIC_IN = "DynamicEtIn";
    private static final String KEY_DYNAMIC_OUT = "DynamicEtOut";
    private static final String KEY_AMOUNT_IN = "DynamicAmountIn";
    private static final String KEY_AMOUNT_OUT = "DynamicAmountOut";
    private static final String KEY_START = "etStartAmount";
    private static final String KEY_IN = "etIn";
    private static final String KEY_IN_TWO = "etInTwo";
    private static final String KEY_TURN_OVER = "etTurnOver";
    private static final String KEY_TAB = "etTab";
    private static final String KEY_TAB_PAID = "etTabPaid";
    private static final String KEY_OUT = "etOut";
    private static final String KEY_OUT_TWO = "etOutTwo";
    private static final String KEY_PIN = "etPin";
    private static final String KEY_COUNTED = "etCounted";
    private static final String KEY_ENVELOPE = "etEnvelope";

    /*
     * The layout inflater for this fragment
     */
    private LayoutInflater mInflater;

    /*
     * These layouts contain the EditText that are inflated when the user needs
     * more than two input fields
     */
    private LinearLayout llInPlaceholder;
    private LinearLayout llOutPlaceholder;

    /*
     * These ArrayLists contain the dynamically created EditTexts
     */
    private ArrayList<EditText> listEditTextIn = new ArrayList<EditText>();
    private ArrayList<EditText> listEditTextOut = new ArrayList<EditText>();

    /*
     * These are the static EditTexts
     */
    private EditText etStartAmount, etIn, etInTwo, etTurnOver, etTab, etTabPaid, etOut,
            etOutTwo, etPin, etCounted, etEnvelope;

    /*
     * The TextViews, these get updated real-time as the user enters data
     */
    private TextView tvTotalExtra, tvReport, tvTotalIn, tvTotalOut, tvResult, tvDifference,
            tvNew;

    /*
     * Root view to be able to access it's child easily (e.g. findViewById)
     */
    private View rootView;

    public static Fragment newInstance(int sectionNumber) {
        InputFragment fragment = new InputFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_input, container, false);
        mInflater = inflater;
        return rootView;
    }

    /*
     * we use onActivityCreated as onResume caused loadSharedPreferences to be
     * run twice.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeViews();
        loadSharedPrefs();
        setFocus();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveSharedPrefs();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_input, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_input:
                this.clearAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * Speaks for itself, initializes all the views, placed in separate method
     * for readability
     */
    public void initializeViews() {
        llInPlaceholder = (LinearLayout) rootView
                .findViewById(R.id.llInPlaceholder);
        llOutPlaceholder = (LinearLayout) rootView
                .findViewById(R.id.llOutPlaceholder);

        Button bMinExtra = (Button) rootView.findViewById(R.id.bRemoveIn);
        Button bPlusExtra = (Button) rootView.findViewById(R.id.bAddIn);
        Button bMinUIt = (Button) rootView.findViewById(R.id.bRemoveOut);
        Button bPlusUit = (Button) rootView.findViewById(R.id.bAddOut);

        bMinExtra.setOnClickListener(this);
        bPlusExtra.setOnClickListener(this);
        bMinUIt.setOnClickListener(this);
        bPlusUit.setOnClickListener(this);

        etStartAmount = (EditText) rootView.findViewById(R.id.etStartAmount);
        etIn = (EditText) rootView.findViewById(R.id.etIn);
        etInTwo = (EditText) rootView.findViewById(R.id.etInTwo);
        etTurnOver = (EditText) rootView.findViewById(R.id.etTurnOver);
        etTab = (EditText) rootView.findViewById(R.id.etTab);
        etTabPaid = (EditText) rootView.findViewById(R.id.etTabPaid);
        etOut = (EditText) rootView.findViewById(R.id.etOut);
        etOutTwo = (EditText) rootView.findViewById(R.id.etOutTwo);
        etPin = (EditText) rootView.findViewById(R.id.etPin);
        etCounted = (EditText) rootView.findViewById(R.id.etCounted);
        etEnvelope = (EditText) rootView.findViewById(R.id.etEnvelope);

        mListen = false;
        etStartAmount.addTextChangedListener(this);
        etIn.addTextChangedListener(this);
        etInTwo.addTextChangedListener(this);
        etTurnOver.addTextChangedListener(this);
        etTab.addTextChangedListener(this);
        etTabPaid.addTextChangedListener(this);
        etOut.addTextChangedListener(this);
        etOutTwo.addTextChangedListener(this);
        etPin.addTextChangedListener(this);
        etCounted.addTextChangedListener(this);
        etEnvelope.addTextChangedListener(this);
        mListen = true;

        tvTotalExtra = (TextView) rootView.findViewById(R.id.tvTotalExtra);
        tvReport = (TextView) rootView.findViewById(R.id.tvReport);
        tvTotalIn = (TextView) rootView.findViewById(R.id.tvTotalIn);
        tvTotalOut = (TextView) rootView.findViewById(R.id.tvTotalOut);
        tvResult = (TextView) rootView.findViewById(R.id.tvResult);
        tvDifference = (TextView) rootView.findViewById(R.id.tvDifference);
        tvNew = (TextView) rootView.findViewById(R.id.tvNew);
    }

    /*
     * This method saves all the EditText data to the SharedPrefs
     */
    public void saveSharedPrefs() {
        // save static EditText data
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_START, etStartAmount.getText().toString());
        editor.putString(KEY_IN, etIn.getText().toString());
        editor.putString(KEY_IN_TWO, etInTwo.getText().toString());
        editor.putString(KEY_TURN_OVER, etTurnOver.getText().toString());
        editor.putString(KEY_TAB, etTab.getText().toString());
        editor.putString(KEY_TAB_PAID, etTabPaid.getText().toString());
        editor.putString(KEY_OUT, etOut.getText().toString());
        editor.putString(KEY_OUT_TWO, etOutTwo.getText().toString());
        editor.putString(KEY_PIN, etPin.getText().toString());
        editor.putString(KEY_COUNTED, etCounted.getText().toString());
        editor.putString(KEY_ENVELOPE, etEnvelope.getText().toString());

        // Save dynamically created EditText data, get every EditText in it's
        // list and save it's text as value. Use a string appended with an
        // integer as key
        for (int i = 0; i < listEditTextIn.size(); i++) {
            editor.putString(KEY_DYNAMIC_IN + i, listEditTextIn.get(i)
                    .getText().toString());
        }
        for (int i = 0; i < listEditTextOut.size(); i++) {
            editor.putString(KEY_DYNAMIC_OUT + i, listEditTextOut.get(i)
                    .getText().toString());
        }

        // Save the amount of created views. We need this when we load the
        // SharedPrefs as we have to inflate and fill the saved EditTexts
        editor.putInt(KEY_AMOUNT_IN, listEditTextIn.size());
        editor.putInt(KEY_AMOUNT_OUT, listEditTextOut.size());

        // Commit
        editor.apply();
    }

    /*
     * This method inflates all the saved views and fills all the EditTexts with
     * data from SharedPrefs
     */
    public void loadSharedPrefs() {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        // Before we load, we clear lists and remove views.
        clearListsAndViews();
        // Get the amount of 'Extra' input fields the user has created
        int iAmountIn = sharedPreferences.getInt(KEY_AMOUNT_IN, 0);
        // Then run a loop in the same way as we saved (KEY + i)
        for (int i = 0; i < iAmountIn; i++) {
            // Add a field, filled with the saved text
            addFieldIn(sharedPreferences.getString(KEY_DYNAMIC_IN + i, ""));
        }
        // Do the same for the 'Out' fields
        int iAmountOut = sharedPreferences.getInt(KEY_AMOUNT_OUT, 0);
        for (int i = 0; i < iAmountOut; i++) {
            addFieldOut(sharedPreferences.getString(KEY_DYNAMIC_OUT + i, ""));
        }

        mListen = false;
        // Load data for the static EditTexts
        etStartAmount.setText(sharedPreferences.getString(KEY_START, ""));
        etIn.setText(sharedPreferences.getString(KEY_IN, ""));
        etInTwo.setText(sharedPreferences.getString(KEY_IN_TWO, ""));
        etTurnOver.setText(sharedPreferences.getString(KEY_TURN_OVER, ""));
        etTab.setText(sharedPreferences.getString(KEY_TAB, ""));
        etTabPaid.setText(sharedPreferences.getString(KEY_TAB_PAID, ""));
        etOut.setText(sharedPreferences.getString(KEY_OUT, ""));
        etOutTwo.setText(sharedPreferences.getString(KEY_OUT_TWO, ""));
        etPin.setText(sharedPreferences.getString(KEY_PIN, ""));
        etCounted.setText(sharedPreferences.getString(KEY_COUNTED, ""));
        etEnvelope.setText(sharedPreferences.getString(KEY_ENVELOPE, ""));
        mListen = true;

        // Calculate and set TextViews
        updateCurrentDagstaat();
    }

    /*
     * This method adds another 'Extra' field for the user it can be
     * passed a String when loading content from SharedPrefs on boot
     */
    public void addFieldIn(String str) {
        // inflate view from XML
        View v = mInflater.inflate(R.layout.layout_in_kas, null);
        // get EditText contained in XMl
        EditText et = (EditText) v.findViewById(R.id.extra_in_kas_inflated);
        // set EditText to passed argument
        et.setText(str);
        // add TextChangedListener
        et.addTextChangedListener(this);
        // add Edi123tText to ArrayList for later calculations
        listEditTextIn.add(et);
        // add inflated view to placeholder
        llInPlaceholder.addView(v);
    }

    /*
     * This method adds another 'Out' field for the user it can be
     * passed a String when loading content from SharedPrefs on boot for detailed comments
     * see the addFieldIn method
     */
    public void addFieldOut(String str) {
        View v = mInflater.inflate(R.layout.layout_uit_kas, null);
        EditText et = (EditText) v.findViewById(R.id.uit_kas_inflated);
        et.setText(str);
        et.addTextChangedListener(this);
        listEditTextOut.add(et);
        llOutPlaceholder.addView(v);
    }

    /*
     * These two almost identical methods remove the last of the inflated
     * layouts added to their placeholders and remove the last entries in the
     * ArrayLists, afterwards we need to recalculate of course and update
     * TextViews
     */
    public void removeFieldIn() {
        int children = llInPlaceholder.getChildCount();
        if (children > 0) {
            llInPlaceholder.removeViewAt(children - 1);
            int size = listEditTextIn.size();
            listEditTextIn.remove(size - 1);
            updateCurrentDagstaat();
        }
    }

    public void removeFieldOut() {
        int children = llOutPlaceholder.getChildCount();
        if (llOutPlaceholder.getChildCount() > 0) {
            llOutPlaceholder.removeViewAt(children - 1);
            int size = listEditTextOut.size();
            listEditTextOut.remove(size - 1);
            updateCurrentDagstaat();
        }
    }

    /*
     * This method removes all dynamically created views from their placeholders
     */
    private void clearListsAndViews() {
        listEditTextOut.clear();
        listEditTextIn.clear();
        llInPlaceholder.removeAllViews();
        llOutPlaceholder.removeAllViews();
    }

    /*
     * This method clears all EditTexts and removes all dynamically created views
     */
    public void clearAll() {
        // clear EditTexts
        mListen = false;
        etStartAmount.setText("");
        etIn.setText("");
        etInTwo.setText("");
        etTurnOver.setText("");
        etTab.setText("");
        etTabPaid.setText("");
        etOut.setText("");
        etOutTwo.setText("");
        etPin.setText("");
        etCounted.setText("");
        etEnvelope.setText("");
        mListen = true;
        // clear extra fields
        clearListsAndViews();
        // set focus back to start
        setFocus();
        // re-calculate and reset all TextViews
        updateCurrentDagstaat();
    }

    /*
     * This method sets focus to the top most EditText and sets the cursor to
     * the end of the text
     */
    private void setFocus() {
        etStartAmount.requestFocus();
        int position = etStartAmount.getText().length();
        etStartAmount.setSelection(position);
    }

    private void updateCurrentDagstaat() {
        // Update current dagstaat with the values from the EditTexts
        mMainActivityCallback.getCurrentDagstaat()
                .setShift(DATE_FORMAT.format(Calendar.getInstance().getTime()));
        mMainActivityCallback.getCurrentDagstaat()
                .setStart(parseTextToDouble(etStartAmount.getText().toString()));
        mMainActivityCallback.getCurrentDagstaat().setExtra(
                parseTextToDouble(etIn.getText().toString())
                        + parseTextToDouble(etInTwo.getText().toString()
                        + mDynamicExtra()));
        mMainActivityCallback.getCurrentDagstaat()
                .setTurnover(parseTextToDouble(etTurnOver.getText().toString()));
        mMainActivityCallback.getCurrentDagstaat()
                .setTab(parseTextToDouble(etTab.getText().toString()));
        mMainActivityCallback.getCurrentDagstaat()
                .setTabPaid(parseTextToDouble(etTabPaid.getText().toString()));
        mMainActivityCallback.getCurrentDagstaat()
                .setOut(parseTextToDouble(etOut.getText().toString())
                        + parseTextToDouble(etOutTwo.getText().toString()
                        + mDynamicOut()));
        mMainActivityCallback.getCurrentDagstaat()
                .setPin(parseTextToDouble(etPin.getText().toString()));
        mMainActivityCallback.getCurrentDagstaat()
                .setCounted(parseTextToDouble(etCounted.getText().toString()));
        mMainActivityCallback.getCurrentDagstaat()
                .setEnvelope(parseTextToDouble(etEnvelope.getText().toString()));

        // Update TextViews with the values from current dagstaat
        tvTotalExtra.setText(String.format(EURO_FORMAT,
                mMainActivityCallback.getCurrentDagstaat().getExtra()));
        tvReport.setText(String.format(EURO_FORMAT,
                mMainActivityCallback.getCurrentDagstaat().getReport()));
        tvTotalIn.setText(String.format(EURO_FORMAT,
                mMainActivityCallback.getCurrentDagstaat().getTotalIn()));
        tvTotalOut.setText(String.format(EURO_FORMAT,
                mMainActivityCallback.getCurrentDagstaat().getTotalOut()));
        tvResult.setText(String.format(EURO_FORMAT,
                mMainActivityCallback.getCurrentDagstaat().getResult()));
        tvDifference.setText(String.format(EURO_FORMAT,
                mMainActivityCallback.getCurrentDagstaat().getDifference()));
        tvNew.setText(String.format(EURO_FORMAT,
                mMainActivityCallback.getCurrentDagstaat().getNew()));

        // Update the overview fragment with the values from current dagstaat
        mMainActivityCallback.updateOverviewFragment();
    }


    /*
    Get the
     */
    public void updateFields() {
        etCounted.setText((String.format(Locale.UK, "%.2f",
                mMainActivityCallback.getCurrentDagstaat().getCounted())));
        etEnvelope.setText((String.format(Locale.UK, "%.2f",
                mMainActivityCallback.getCurrentDagstaat().getEnvelope())));
    }

    /*
     * This method returns a double value of all the dynamically created "in"
     * EditTexts
     */
    private double mDynamicExtra() {
        double result = 0;
        for (EditText et : listEditTextIn) {
            result += parseTextToDouble(et.getText().toString());
        }
        return result;
    }

    /*
     * This method returns a double value of all the dynamically created "out"
     * EditTexts
     */
    private double mDynamicOut() {
        double result = 0;
        for (EditText et : listEditTextOut) {
            result += parseTextToDouble(et.getText().toString());
        }
        return result;
    }

    private double parseTextToDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /*
     * After text in an EditText has changed we calculate everything, update current dagstaat
     * and set all TextViews
     */
    @Override
    public void afterTextChanged(Editable arg0) {
        // Only calculate when we want it to
        if (mListen) {
            updateCurrentDagstaat();
        }
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bRemoveIn:
                removeFieldIn();
                break;
            case R.id.bAddIn:
                addFieldIn("");
                break;
            case R.id.bRemoveOut:
                removeFieldOut();
                break;
            case R.id.bAddOut:
                addFieldOut("");
                break;
        }
    }

}
