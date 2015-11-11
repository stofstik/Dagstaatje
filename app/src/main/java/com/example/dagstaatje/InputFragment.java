package com.example.dagstaatje;

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

import java.util.ArrayList;

public class InputFragment extends Fragment implements TextWatcher,
        OnClickListener {

    /*
     We use this boolean to improve performance by only listening to EditText when we want.
     For example if we clear all texts, it is going to calculate with every .setText("")
     */
   static boolean listen = false;

    /*
     * Used for the ViewPager
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /*
     * Used for string formatter, formats to € 0.00
     */
    private static final String EURO_FORMAT = "€ %.2f";

    /*
     * Declare SharedPrefs
     */
    private static SharedPreferences sharedPreferences;

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
     * These doubles are filled with the values of the EditTexts
     */
    public static double dStart, dExtra, dExtraTwo, dTurnOver, dTab, dTabPaid, dOut, dOutTwo,
            dPin, dCounted, dEnvelope;

    /*
     * These doubles will be filled with the calculated results
     */
    public static double dTotalExtra, dReport, dTotalIn, dTotalOut, dTotalOutInclPin,
            dResult, dDifference, dNewRegister;

    /*
     * The layout inflater for this fragment
     */
    private LayoutInflater mInflater;

    /*
     * These layouts contain the EditText that are inflated when the user needs
     * more than two input fields
     */
    private static LinearLayout llInPlaceholder;
    private static LinearLayout llOutPlaceholder;

    /*
     * These ArrayLists contain the dynamically created EditTexts
     */
    private static ArrayList<EditText> listEditTextIn = new ArrayList<EditText>();
    private static ArrayList<EditText> listEditTextOut = new ArrayList<EditText>();

    /*
     * These are the static EditTexts
     */
    public static EditText etStartAmount, etIn, etInTwo, etTurnOver, etTab, etTabPaid, etOut,
            etOutTwo, etPin, etCounted, etEnvelope;

    /*
     * The TextViews, these get updated real-time as the user enters data
     */
    private static TextView tvTotalExtra, tvReport, tvTotalIn, tvTotalOut, tvResult, tvDifference,
            tvNew;

    /*
     * Root view to be able to access it's child easily (e.g. findViewById)
     */
    private static View rootView;

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
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        initializeViews();
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
        inflater.inflate(R.menu.menu_input, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.clear_input:
                this.clearAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSharedPreferences();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveSharedPreferences();
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

        listen = false;
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
        listen = true;

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
    public void saveSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // save static EditText data
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
        editor.commit();
    }

    /*
     * This method inflates all the saved views and fills all the EditTexts with
     * data from SharedPrefs
     */
    public void loadSharedPreferences() {
        // Before we load, we clear lists and remove views.
        // Lists are static and will not be redeclared in onResume.
        // So when loadSharedPreferences runs it duplicates the data in the
        // list. The for loop will then create duplicate views as well.
        clearListsAndViews();
        // Get the amount of created EditTexts
        int iAmountIn = sharedPreferences.getInt(KEY_AMOUNT_IN, 0);
        // Then run a loop in the same way as we saved (KEY + i)
        for (int i = 0; i < iAmountIn; i++) {
            // Add a field, filled with the saved text
            addFieldIn(sharedPreferences.getString(KEY_DYNAMIC_IN + i, ""));
        }
        // Do the same for the "Out" fields
        int iAmountOut = sharedPreferences.getInt(KEY_AMOUNT_OUT, 0);
        for (int i = 0; i < iAmountOut; i++) {
            addFieldOut(sharedPreferences.getString(KEY_DYNAMIC_OUT + i, ""));
        }

        listen =  false;
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
        listen = true;

        // Calculate and set TextViews
        calculateAll();
    }

    /*
     * This method adds another "register in" field for the user it can be
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
     * This method adds another "register out" field for the user it can be
     * passed a String when loading content from SharedPrefs on boot for further
     * commenting see the "register in" method above
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
        int childs = llInPlaceholder.getChildCount();
        if (childs > 0) {
            llInPlaceholder.removeViewAt(childs - 1);
            int size = listEditTextIn.size();
            listEditTextIn.remove(size - 1);
            calculateAll();
        }
    }

    public void removeFieldOut() {
        int childs = llOutPlaceholder.getChildCount();
        if (llOutPlaceholder.getChildCount() > 0) {
            llOutPlaceholder.removeViewAt(childs - 1);
            int size = listEditTextOut.size();
            listEditTextOut.remove(size - 1);
            calculateAll();
        }
    }

    /*
     * This method removes all dynamically created views from their placeholders
     */
    public static void clearListsAndViews() {
        listEditTextOut.clear();
        listEditTextIn.clear();
        llInPlaceholder.removeAllViews();
        llOutPlaceholder.removeAllViews();
    }

    /*
     * This method clears all EditTexts and remove all dynamically created views
     */
    public static void clearAll() {
        // clear EditTexts
        listen = false;
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
        listen = true;
        // clear extra fields
        clearListsAndViews();
        // set focus back to start
        setFocus();
        // re calculate and reset all TextViews
        calculateAll();
    }

    /*
     * This method sets focus to the top most EditText and sets the cursor to
     * the end of the text
     */
    public static void setFocus() {
        etStartAmount.requestFocus();
        int position = etStartAmount.getText().length();
        etStartAmount.setSelection(position);
    }

    /*
     * This method will calculate everything and set the TextViews accordingly
     */
    public static void calculateAll() {
        // Parse every EditText to a double
        dStart = parseTextToDouble(etStartAmount.getText().toString());
        dExtra = parseTextToDouble(etIn.getText().toString());
        dExtraTwo = parseTextToDouble(etInTwo.getText().toString());
        dTurnOver = parseTextToDouble(etTurnOver.getText().toString());
        dTab = parseTextToDouble(etTab.getText().toString());
        dTabPaid = parseTextToDouble(etTabPaid.getText().toString());
        dOut = parseTextToDouble(etOut.getText().toString());
        dOutTwo = parseTextToDouble(etOutTwo.getText().toString());
        dPin = parseTextToDouble(etPin.getText().toString());
        dCounted = parseTextToDouble(etCounted.getText().toString());
        dEnvelope = parseTextToDouble(etEnvelope.getText().toString());

        // Extra cash in the register, extra + dynamic extra
        dTotalExtra = dExtra + dExtraTwo + dDynamicExtra();
        // The report, turn over - tabbed + tabs paid
        dReport = dTurnOver - dTab + dTabPaid;
        // Total amount of cash in register, start amount + extra + registered
        dTotalIn = dStart + dTotalExtra + dReport;
        // Total amount taken out of the register
        dTotalOut = dOut + dOutTwo + dDynamicOut();
        // We also subtract PIN transactions, because they have been entered
        // into the register, but have of course not been added into the drawer
        // as cash.
        dTotalOutInclPin = dTotalOut + dPin;
        // The amount of cash left in the register
        dResult = dTotalIn - dTotalOutInclPin;
        // The difference between what the user counted and what is actually in
        // the register
        dDifference = dCounted - dResult;
        // The new start amount for the next day, counted money minus envelope
        dNewRegister = dCounted - dEnvelope;

        // Update TextViews
        tvTotalExtra.setText(String.format(EURO_FORMAT, dTotalExtra));
        tvReport.setText(String.format(EURO_FORMAT, dReport));
        tvTotalIn.setText(String.format(EURO_FORMAT, dTotalIn));
        tvTotalOut.setText(String.format(EURO_FORMAT, dTotalOutInclPin));
        tvResult.setText(String.format(EURO_FORMAT, dResult));
        tvDifference.setText(String.format(EURO_FORMAT, dDifference));
        tvNew.setText(String.format(EURO_FORMAT, dNewRegister));

        // Also update OverviewFragment TextViews
        OverviewFragment.update();
    }

    /*
     * This method returns a double value of all the dynamically created "in"
     * EditTexts
     */
    public static double dDynamicExtra() {
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
    public static double dDynamicOut() {
        double result = 0;
        for (EditText et : listEditTextOut) {
            result += parseTextToDouble(et.getText().toString());
        }
        return result;
    }

    /*
     * This method converts a String to a double value, checking if the String
     * passed is workable, if it is not it returns an obviously incorrect number (for this application)
     */
    public static double parseTextToDouble(String str) {
        double result;
        if (str.matches("") || str == null) {
            result = 0;
        } else {
            try {
                result = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                result = -999999;
            }
        }
        return result;
    }

    /*
     * After text in an EditText has changed we calculate everything and update
     * all TextViews
     */
    @Override
    public void afterTextChanged(Editable arg0) {
        // Only calculate when we want it to
        if(listen){
            calculateAll();
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
