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
import android.widget.Button;
import android.widget.EditText;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }


        final Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new HttpRequestTask().execute();

            }
        });

        final Button loadMainActivityButton = (Button) findViewById(R.id.buttonLoadMain);
        loadMainActivityButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
            return rootView;
        }
    }
    private class HttpRequestTask extends AsyncTask<Void, Void, Employees[]>
    {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Logging In...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            // Do nothing
        }
        @Override
        protected Employees[] doInBackground(Void... params)
        {
            try
            {
                final String url = "http://192.168.1.7:8000/restfulservices/api/employees";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                Employees[] employees = restTemplate.getForObject(url, Employees[].class);
                return employees;
            }
            catch (Exception e)
            {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Employees[] employees)
        {
            try
            {
                EditText loginEditText = (EditText) findViewById(R.id.loginEditText);
                EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
                String trimmedLogin, trimmedPassword, editTextLogin, editTextPassword;
                editTextLogin = loginEditText.getText().toString();
                editTextPassword = passwordEditText.getText().toString();
                for(int e = 0; e < employees.length; e++)
                {
                    trimmedLogin = employees[e].getLogin().trim();
                    trimmedPassword = employees[e].getPassword().trim();

                    if(trimmedLogin.equalsIgnoreCase(editTextLogin) && trimmedPassword.equals(editTextPassword))
                    {//Login & password is a match
                        String verifiedUserEmployeeID;
                        String verifiedUserFullName;

                        verifiedUserEmployeeID = employees[e].getEmployeeID().trim();
                        verifiedUserFullName = employees[e].getCombinedName().trim();

                        MyApplication.setVerifiedUserEmployeeID(verifiedUserEmployeeID);
                        MyApplication.setVerifiedUserFullName(verifiedUserFullName);

                        if(verifiedUserEmployeeID != null)
                        {
                            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Log.e("MainActivity", e.getMessage(), e);
            }
            progressDialog.dismiss();
        }

    }
}
