package com.yellowtwig.ewscalandar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

import ews.config.ConnectionConfig;
import ews.message.FindItemResponse;
import ews.operation.FindItemOperation;
import ews.transport.RequestHandler;
import ews.types.CalendarItem;


public class MainActivity extends Activity {
    private ArrayList<String> myStringArray = new ArrayList<>();
    private ArrayAdapter<String> myAdapter;
    private ConnectionConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initList();

        loadPreferences();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    private void setupListener() {
        Button  button=
                (Button)findViewById(R.id.button);
        View.OnClickListener Listener=
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        myStringArray.add("Clicked");
                        getCalendarItems();
                    }
                };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, MyPreferencesActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initList(){

       myAdapter =
                new ArrayAdapter<String>(
                                        this,
                                        android.R.layout.simple_list_item_1,
                                        myStringArray);

        ListView myList = (ListView) findViewById(R.id.listView);
        myList.setAdapter(myAdapter);
        //getCalendarItems();
    }

    public void getCalendarItems(){
        RequestHandler handler = new RequestHandler(config);
        FindItemOperation operation = new FindItemOperation(handler);

        try {
            FindItemResponse response = operation.execute();
            for (CalendarItem calendarItem : response.getItems()) {
                //SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm Z", Locale.forLanguageTag("nl_NL"));
                //format.setTimeZone(TimeZone.getTimeZone("Europe/Amsterdam") );
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm Z", new Locale("nl", "NL"));
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Amsterdam"));
                String date = simpleDateFormat.format(calendarItem.getStart());
                String listItem = calendarItem.getSubject() + " : " + date ;
                listItem += " : " + calendarItem.getLocation();
                myStringArray.add(listItem);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(View view) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getCalendarItems();
        myAdapter.notifyDataSetChanged();
    }

    private void loadPreferences(){
        config = new ConnectionConfig();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String username = sharedPref.getString(MyPreferencesActivity.USERNAME, "");
        String password = sharedPref.getString(MyPreferencesActivity.PASSWORD,"");
        String domain = sharedPref.getString(MyPreferencesActivity.DOMAIN,"");
        String URL = sharedPref.getString(MyPreferencesActivity.URL,"");

        config.setDomain(domain);
        config.setUserName(username);
        config.setPassword(password);
        config.setServiceURL(URL);

    }

}
