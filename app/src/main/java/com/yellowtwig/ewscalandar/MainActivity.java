package com.yellowtwig.ewscalandar;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

import ews.message.FindItemResponse;
import ews.operation.FindItemOperation;
import ews.transport.RequestHandler;
import ews.types.CalendarItem;


public class MainActivity extends Activity {
    private ArrayList<String> myStringArray = new ArrayList<>();
    private ArrayAdapter<String> myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initList();
        //setupListener();
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
        RequestHandler handler = new RequestHandler();
        FindItemOperation operation = new FindItemOperation(handler);

        try {
            FindItemResponse response = operation.execute();
            for (CalendarItem calendarItem : response.getItems()) {
                String listItem = calendarItem.getSubject() + " : " + calendarItem.getStart();
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
}
