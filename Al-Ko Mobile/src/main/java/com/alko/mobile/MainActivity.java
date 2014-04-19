package com.alko.mobile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks
{
    ListView prListView;
    ListAdapter payperiodsListAdapter;
    String[] payPeriodIndexes;


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //new HttpRequestTask().execute();

        String verifiedUserEmployeeID;
        String verifiedUserFullName;

        TextView employeeIDValueTextView = (TextView) findViewById(R.id.id_value);
        TextView employeeFullNameTextView = (TextView) findViewById(R.id.content_value);

        verifiedUserEmployeeID = MyApplication.getVerifiedUserEmployeeID();
        verifiedUserFullName = MyApplication.getVerifiedUserFullName();

        employeeIDValueTextView.setText(verifiedUserEmployeeID);
        employeeFullNameTextView.setText(verifiedUserFullName);

        if(prListView != null)
        {

        }

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_refresh)
        {
            new HttpRequestTask().execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

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

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, PayPeriods[]>
    {
        @Override
        protected PayPeriods[] doInBackground(Void... params)
        {
            try
            {
                final String url = "http://192.168.1.7:8000/RestfulServices/api/payperiods";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());


                //List<PayPeriods> payperiods = (List) restTemplate.getForObject(url, PayPeriods.class);
                PayPeriods[] payperiods = restTemplate.getForObject(url, PayPeriods[].class);
                return payperiods;
            }
            catch (Exception e)
            {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(PayPeriods[] payperiods)
        {
            try
            {
                prListView = (ListView) findViewById(R.id.prListView);
                String[] payPeriodValues = new String[payperiods.length];
                payPeriodIndexes = new String[payperiods.length];

                SimpleDateFormat dateFormatOld = new SimpleDateFormat("yyyy-mm-dd'T'HH:MM:SS");
                SimpleDateFormat dateFormatNew = new SimpleDateFormat("yyyy-mm-dd");
                Date startDate;
                Date endDate;
                String startDateStr;
                String endDateStr;

                for(int i = 0; i < payperiods.length; i++)
                {
                    //Change the complex date format to an easily readable format
                    startDate = dateFormatOld.parse(payperiods[i].getPayPeriodStartDate());
                    endDate = dateFormatOld.parse(payperiods[i].getPayPeriodEndDate());
                    startDateStr = dateFormatNew.format(startDate);
                    endDateStr = dateFormatNew.format(endDate);

                    //Format Start & End Dates with Slashes instead of Dashes
                    startDateStr = startDateStr.replaceAll("-", "/");
                    endDateStr = endDateStr.replaceAll("-", "/");

                    //Get the current PayPeriodId (Not currently used for anything)
                    payPeriodIndexes[i] = payperiods[i].getPayPeriodId();

                    //Create a string value for each PayPeriod in the array according to the date range. To be displayed in each row of the ListView
                    payPeriodValues[i] = startDateStr + "-" + endDateStr;
                }

                //Load all of the found PayPeriods into the ListView
                payperiodsListAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, payPeriodValues);
                prListView.setAdapter(payperiodsListAdapter);

                setUpPayPeriodClickListener();
            }
            catch (Exception e)
            {
                Log.e("MainActivity", e.getMessage(), e);
            }

        }
    }
    private void setUpPayPeriodClickListener()
    {
        prListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                MyApplication.setCurrentPayPeriodID(payPeriodIndexes[position]);
                Intent myIntent = new Intent(MainActivity.this, DaysActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }

}
