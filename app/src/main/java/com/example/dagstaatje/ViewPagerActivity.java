/*
 * 8-nov-2014
 * 
 * This is a pet project I have started a long time ago as a supplement
 * to a money counter I made. A lot of code was taken directly out of that app,
 * resulting in some really weird spaghetti code. 
 * The code has since been cleaned up, commented and rewritten at some points. 
 * And boy was that necessary! It was a mess. 
 * It is nice to see how much I have progressed since.
 * And to feel the contrast in searching and Googling between then and now.
 * Some variable names may still be in Dutch in XML here and there. 
 * But overall it is quite readable.
 * 
 * 9-nov-2014
 * 
 * Did some XML renaming and some Material Design styling.
 * Going to use this app to learn more about styling.
 * android:elevation does not seem to work for some reason.
 * Would love to make Android's new "floating button" the clear all button
 * and have the tabs in the action bar. 
 * actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS); is deprecated
 * so we need to use PagerTabStrip
 * http://developer.android.com/reference/android/support/v4/view/PagerTabStrip.html
 *
 * 25-nov-2015
 *
 * Holy crap! It has been more than a year since I have worked on this.
 * A lot has happened since then... Wow!
 * Okay, take a breath.
 * Let's clean this mess up a bit more and implement some logic to communicate with a mongoDb server
 */

package com.example.dagstaatje;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.stofstik.dagstaatje.R;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ViewPagerActivity extends FragmentActivity implements
        ServerAddressDialogFragment.NoticeDialogListener,
        ViewPagerActivityInterface {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("d-M-yyyy", Locale.US);
    public static final DateFormat SERVER_DATE_FORMAT = new SimpleDateFormat("yyyy-M-d", Locale.US); // TODO check compatibility stuffs

    public static final int POSITION_COUNT_FRAGMENT = 0;
    public static final int POSITION_INPUT_FRAGMENT = 1;
    public static final int POSITION_OVERVIEW_FRAGMENT = 2;
    public static final int POSITION_HISTORY_FRAGMENT = 3;

    private SharedPreferences sharedPreferences;
    public static final String KEY_SERVER_ADDRESS = "serverAddress";
    public static final String KEY_SERVER_PORT = "serverPort";

    private ViewPager mViewPager;
    public static MyPagerAdapter mPagerAdapter;


    SlidingTabLayout mSLidingTabLayout;
    public static String serverAddress = "";
    public static int serverPort = 1337;

    public static Dagstaat mCurrentDagstaat;

    public final static String REMOTE_SERVER_ADDRESS = "http://192.168.0.11:3001"; // full address e.g http://bla.com:1337

    private static final int[] TAB_NAMES = {R.string.fragment_count, R.string.fragment_input, R.string.fragment_overview, R.string.fragment_history};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentDagstaat = new Dagstaat();

        setContentView(R.layout.activity_pager);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        mSLidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSLidingTabLayout.setViewPager(mViewPager);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        serverAddress = sharedPreferences.getString(KEY_SERVER_ADDRESS, "");
        serverPort = sharedPreferences.getInt(KEY_SERVER_PORT, 1337);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /*
    Updates the TextViews with the values from mCurrentDagstaat
     */
    @Override
    public void updateOverviewFragment() {
        OverviewFragment overviewFragment = (OverviewFragment)
                getSupportFragmentManager()
                        .findFragmentByTag(mPagerAdapter.getFragmentTag(mViewPager.getId(), POSITION_OVERVIEW_FRAGMENT));
        overviewFragment.update();
    }

    /*
    Updates the counted and envelope EditTexts with the values from mCurrentDagstaat
     */
    @Override
    public void updateInputFragmentFields() {
        InputFragment inputFragment = (InputFragment)
                getSupportFragmentManager()
                        .findFragmentByTag(mPagerAdapter.getFragmentTag(mViewPager.getId(), POSITION_INPUT_FRAGMENT));
        inputFragment.updateFields();
    }

    @Override
    public Dagstaat getCurrentDagstaat() {
        return mCurrentDagstaat;
    }

    private class SendCSVTask extends AsyncTask<Void, Void, Integer> {
        String separator = ",";
        String data = "";
        Socket socket = null;
        Dagstaat dagstaat;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result) {
                case 0:
                    Toast.makeText(getApplicationContext(), "Verzonden", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "Niet verbonden", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // date is today minus one day because we use this almost always after midnight
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            String strDate = DATE_FORMAT.format(cal.getTime());
            StringBuilder sb = new StringBuilder();
            sb.append(strDate);
            sb.append(separator);
            sb.append(dagstaat.getStart());
            sb.append(separator);
            sb.append(dagstaat.getExtra());
            sb.append(separator);
            sb.append(dagstaat.getTurnover());
            sb.append(separator);
            sb.append(dagstaat.getTab());
            sb.append(separator);
            sb.append(dagstaat.getTabPaid());
            sb.append(separator);
            sb.append(dagstaat.getOut());
            sb.append(separator);
            sb.append(dagstaat.getPin());
            sb.append(separator);
            sb.append(dagstaat.getCounted());
            sb.append(separator);
            sb.append(dagstaat.getEnvelope());
            data = sb.toString();

            Exception exception = null;
            // test connection
            try {
                socket = new Socket();
                socket.setSoTimeout(5000);
                socket.connect(new InetSocketAddress(serverAddress, serverPort));
            } catch (Exception e) {
                exception = e;
                return 1;
            }
            if (exception == null) {
                // connection is live, send data
                try {
                    byte[] bytes = data.getBytes("UTF-8");
                    BufferedOutputStream out = new BufferedOutputStream(
                            socket.getOutputStream());
                    out.write(bytes, 0, bytes.length);
                    out.flush();
                    out.close();
                    socket.close();
                    return 0;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return 2;
                } catch (IOException e) {
                    e.printStackTrace();
                    return 2;
                }
            } else {
                return 2;
            }
        }
    }

    private void saveToDatabase() {
        if (REMOTE_SERVER_ADDRESS.isEmpty()) return;
        Uri.Builder builder = Uri.parse(REMOTE_SERVER_ADDRESS).buildUpon()
                .appendPath("newEntry")
                .appendPath(mCurrentDagstaat.getShift())
                .appendPath("" + mCurrentDagstaat.getStart())
                .appendPath("" + mCurrentDagstaat.getExtra())
                .appendPath("" + mCurrentDagstaat.getTurnover())
                .appendPath("" + mCurrentDagstaat.getTab())
                .appendPath("" + mCurrentDagstaat.getTabPaid())
                .appendPath("" + mCurrentDagstaat.getOut())
                .appendPath("" + mCurrentDagstaat.getPin())
                .appendPath("" + mCurrentDagstaat.getCounted())
                .appendPath("" + mCurrentDagstaat.getEnvelope());
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, builder.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("saveToDatabase", "remote server responded with: " + response);
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show(); // TODO hide in production version
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.clear_all:
                InputFragment inputFragment = (InputFragment)
                        getSupportFragmentManager()
                                .findFragmentByTag(mPagerAdapter.getFragmentTag(mViewPager.getId(), POSITION_INPUT_FRAGMENT));
                inputFragment.clearAll();
                CountFragment countFragment = (CountFragment)
                        getSupportFragmentManager()
                                .findFragmentByTag(mPagerAdapter.getFragmentTag(mViewPager.getId(), POSITION_COUNT_FRAGMENT));
                countFragment.clearAll();
                return true;
            case R.id.action_set_server_address:
                // show dialog for server
                DialogFragment newFragment = new ServerAddressDialogFragment();
                newFragment.show(getSupportFragmentManager(), "ServerDialog");
                return true;
            case R.id.action_send:
                // send to local server
                new SendCSVTask().execute();
                // send to remote database server
                saveToDatabase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case POSITION_COUNT_FRAGMENT:
                    return CountFragment.newInstance(position);
                case POSITION_INPUT_FRAGMENT:
                    return InputFragment.newInstance(position);
                case POSITION_OVERVIEW_FRAGMENT:
                    return OverviewFragment.newInstance(position);
                case POSITION_HISTORY_FRAGMENT:
                    return HistoryFragment.newInstance(position);
                default:
                    return InputFragment.newInstance(position);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(TAB_NAMES[position]);
        }

        @Override
        public int getCount() {
            return TAB_NAMES.length;
        }

        private String getFragmentTag(int viewPagerId, int fragmentPos) {
            return "android:switcher:" + viewPagerId + ":" + fragmentPos;
        }

    }

    private boolean isEveningShift() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return hour > 0 && hour < 16; // after midnight and before 16:00 is evening shift
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // Save to SharesPrefs
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SERVER_ADDRESS, serverAddress);
        editor.putInt(KEY_SERVER_PORT, serverPort);
        editor.apply();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // Do nothing
    }

}
