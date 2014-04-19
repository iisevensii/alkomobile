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
import android.widget.TimePicker;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SingleDayActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_day);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        new HttpRequestTask().execute();
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(SingleDayActivity.this, DaysActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.single_day, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_single_day, container, false);
            return rootView;
        }
    }
    private class HttpRequestTask extends AsyncTask<Void, Void, Days>
    {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(SingleDayActivity.this);
            progressDialog.setMessage("Synchronizing...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected Days doInBackground(Void... params)
        {
            try
            {//Fix this current day is not yet known until item is clicked!
                String currentDayID = MyApplication.getCurrentDayID();
                final String url = "http://192.168.1.7:8000/restfulservices/api/days/singleday/" + currentDayID;

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                Days day = restTemplate.getForObject(url, Days.class);
                return day;
            }
            catch (Exception e)
            {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Days day)
        {
            try
            {
                Date curDayTimeIn, curDayTimeOut;

                String curDayTimeInStr, curDayTimeOutStr,
                       curDayHourInStr, curDayHourOutStr,
                    curDayMinutesInStr, curDayMintesOutStr;

                int curDayHourInInt, curDayHourOutInt,
                 curDayMinutesInInt, curDayMinutesOutInt;
                String[] splitHourMinutesIn, splitHourMinutesOut;

                SimpleDateFormat dateFormatOld = new SimpleDateFormat("yyyy-mm-dd'T'HH:MM:SS");
                SimpleDateFormat dateFormatNew = new SimpleDateFormat("HH:MM");

                TimePicker timePickerIn = (TimePicker) findViewById(R.id.timeInTimePicker);
                TimePicker timePickerOut = (TimePicker) findViewById(R.id.timeOutTimePicker);

                curDayTimeIn = dateFormatOld.parse(day.getStartTime());
                curDayTimeOut = dateFormatOld.parse(day.getEndTime());
                curDayTimeInStr = dateFormatNew.format(curDayTimeIn);
                curDayTimeOutStr = dateFormatNew.format(curDayTimeOut);
                splitHourMinutesIn = curDayTimeInStr.split(":");
                splitHourMinutesOut = curDayTimeOutStr.split(":");

                curDayHourInStr = splitHourMinutesIn[0];
                curDayMinutesInStr = splitHourMinutesIn[1];
                curDayHourOutStr = splitHourMinutesOut[0];
                curDayMintesOutStr = splitHourMinutesOut[1];

                curDayHourInInt = Integer.parseInt(curDayHourInStr);
                curDayMinutesInInt = Integer.parseInt(curDayMinutesInStr);
                curDayHourOutInt = Integer.parseInt(curDayHourOutStr);
                curDayMinutesOutInt = Integer.parseInt(curDayMintesOutStr);

                timePickerIn.setCurrentHour(curDayHourInInt);
                timePickerIn.setCurrentMinute(curDayMinutesInInt);
                timePickerOut.setCurrentHour(curDayHourOutInt);
                timePickerOut.setCurrentMinute(curDayMinutesOutInt);

            }
            catch (Exception e)
            {
                Log.e("SingleDayActivity", e.getMessage(), e);
            }
            progressDialog.dismiss();
        }
    }

}
