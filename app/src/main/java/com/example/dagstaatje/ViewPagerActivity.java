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
 */

package com.example.dagstaatje;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.stofstik.dagstaatje.R;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ViewPagerActivity extends FragmentActivity implements
        ServerAddressDialogFragment.NoticeDialogListener {

    static String separator = ",";
    static String data = "";
    static DateFormat formatter = new SimpleDateFormat("d-M-yyyy", Locale.US);

    static Socket socket = null;
    private static SharedPreferences sharedPreferences;
    public static final String KEY_SERVER_ADDRESS = "serverAddress";
    public static final String KEY_SERVER_PORT = "serverPort";

    ViewPager mViewPager;
    SlidingTabLayout mSLidingTabLayout;
    public static String serverAddress = "";
    public static int serverPort = 1337;

    static Context context;

    public static ArrayList<String> tabNames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pager);

        tabNames.add(getString(R.string.fragment_count));
        tabNames.add( getString(R.string.fragment_input));
        tabNames.add(getString(R.string.fragment_overview));

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(2);

        mSLidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSLidingTabLayout.setViewPager(mViewPager);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        serverAddress = sharedPreferences.getString(KEY_SERVER_ADDRESS, "");
        serverPort = sharedPreferences.getInt(KEY_SERVER_PORT, 1337);
        context = getApplicationContext();

    }

    private class SendCSVTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result) {
                case 0:
                    Toast.makeText(context, "Verzonden", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(context, "Niet verbonden", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case 2:
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // date is today minus one day because we use this almost always
            // ater midnight
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            String strDate = formatter.format(cal.getTime());
            StringBuilder sb = new StringBuilder();
            sb.append(strDate);
            sb.append(separator);
            sb.append(InputFragment.dStart);
            sb.append(separator);
            sb.append(InputFragment.dTotalExtra);
            sb.append(separator);
            sb.append(InputFragment.dTurnOver);
            sb.append(separator);
            sb.append(InputFragment.dTab);
            sb.append(separator);
            sb.append(InputFragment.dTabPaid);
            sb.append(separator);
            sb.append(InputFragment.dTotalOut);
            sb.append(separator);
            sb.append(InputFragment.dPin);
            sb.append(separator);
            sb.append(InputFragment.dCounted);
            sb.append(separator);
            sb.append(InputFragment.dEnvelope);
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
                InputFragment.clearAll();
                CountFragment.clearAll();
                return true;
            case R.id.action_set_server_address:
                // show dialog for server
                DialogFragment newFragment = new ServerAddressDialogFragment();
                newFragment.show(getSupportFragmentManager(), "ServerDialog");
                return true;
            case R.id.action_send:
                // send to server
                new SendCSVTask().execute();
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
                case 0:
                    return CountFragment.newInstance(position);
                case 1:
                    return InputFragment.newInstance(position);
                case 2:
                    return OverviewFragment.newInstance(position);
                default:
                    return InputFragment.newInstance(position);
            }
        }



        @Override
        public CharSequence getPageTitle(int position) {
            return tabNames.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // Save to SharesPrefs
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SERVER_ADDRESS, serverAddress);
        editor.putInt(KEY_SERVER_PORT, serverPort);
        editor.commit();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // Do nothing
    }

}
