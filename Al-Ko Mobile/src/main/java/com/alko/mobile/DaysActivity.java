package com.alko.mobile;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class DaysActivity extends Activity
{
    ListAdapter daysListAdapter;
    String[] currentDayIDIndexes;
    ListView daysListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    protected void onStart()
    {
        daysListView = (ListView) findViewById(R.id.daysListView);
        super.onStart();
        new HttpRequestTask().execute();


        TextView payperiodIDTextView = (TextView) findViewById(R.id.payperiodIDValue);
        payperiodIDTextView.setText("PayPeriodID: " + MyApplication.getCurrentPayPeriodID());
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(DaysActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.days, menu);
        return true;
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
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_days, container, false);
            return rootView;
        }
    }
    private class HttpRequestTask extends AsyncTask<Void, Void, Days[]>
    {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(DaysActivity.this);
            progressDialog.setMessage("Synchronizing...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Days[] doInBackground(Void... params)
        {
            try
            {
                String curEmployeeID = MyApplication.getVerifiedUserEmployeeID();
                String curPayPeriodID = MyApplication.getCurrentPayPeriodID();
                final String url = "http://192.168.1.7:8000/restfulservices/api/days/payperioddays/" + curEmployeeID + curPayPeriodID;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                Days[] days = restTemplate.getForObject(url, Days[].class);
                return days;
            }
            catch (Exception e)
            {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Days[] days)
        {
            try
            {


                Date curDate, curDayTimeIn, curDayTimeOut;

                String  curDateStr,
                        curDayTimeInStr, curDayTimeOutStr,
                        curDayHourInStr, curDayHourOutStr,
                        curDayMinutesInStr, curDayMinutesOutStr;
                String tempString;
                int curDayHourInInt, curDayHourOutInt,
                    curDayMinutesInInt, curDayMinutesOutInt;

                String[] splitHourMinutesIn = new String[2];
                String[] splitHourMinutesOut = new String[2];
                String[] daysHoursMinutesValues = new String[days.length];
                currentDayIDIndexes = new String[days.length];

                SimpleDateFormat dateFormatOld = new SimpleDateFormat("yyyy-mm-dd'T'HH:MM:SS");
                SimpleDateFormat dateFormatNew = new SimpleDateFormat("mm-dd-yyyy");
                SimpleDateFormat timeFormatNew = new SimpleDateFormat("HH:MM");
                for(int i = 0; i < days.length; i++)
                {

                    currentDayIDIndexes[i] = days[i].getDayID();

                    if(days[i].getDate() != null)
                    {
                        curDate = dateFormatOld.parse(days[i].getDate());

                        curDateStr = dateFormatNew.format(curDate);
                        curDateStr = curDateStr.replaceAll("-", "/");

                        if(days[i].getStartTime() != null && days[i].getEndTime() != null)
                        {
                            curDayTimeIn = dateFormatOld.parse(days[i].getStartTime());
                            curDayTimeOut = dateFormatOld.parse(days[i].getEndTime());

                            curDayTimeInStr = timeFormatNew.format(curDayTimeIn);
                            curDayTimeOutStr = timeFormatNew.format(curDayTimeOut);

                            splitHourMinutesIn = curDayTimeInStr.split(":");
                            splitHourMinutesOut = curDayTimeOutStr.split(":");

                            curDayHourInStr = splitHourMinutesIn[0];
                            curDayMinutesInStr = splitHourMinutesIn[1];
                            curDayHourOutStr = splitHourMinutesOut[0];
                            curDayMinutesOutStr = splitHourMinutesOut[1];

                            daysHoursMinutesValues[i] = curDateStr + " " +
                                    curDayHourInStr + ":" + curDayMinutesInStr
                                    + "-" +
                                    curDayHourOutStr + ":" + curDayMinutesOutStr;
                        }
                        else
                        {
                            daysHoursMinutesValues[i] = curDateStr + " No Hours Set";
                        }
                    }
                    else
                    {
                        daysHoursMinutesValues[i] = " No Information";
                    }
                }

                daysListAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, daysHoursMinutesValues);
                daysListView.setAdapter(daysListAdapter);

                setUpDaysClickListener();
            }
            catch (Exception e)
            {
                Log.e("DaysActivity", e.getMessage(), e);
            }
            progressDialog.dismiss();
        }
    }

    private void setUpDaysClickListener()
    {
        daysListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyApplication.setCurrentDayID(currentDayIDIndexes[position]);
                Intent myIntent = new Intent(DaysActivity.this, SingleDayActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
}