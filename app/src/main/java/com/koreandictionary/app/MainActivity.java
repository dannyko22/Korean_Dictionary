package com.koreandictionary.app;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;

public class MainActivity extends AppCompatActivity {

    DataBaseHelper myDbHelper;
    ListView listView;
    ArrayList<DictionaryData> dictList;
    MyArrayAdapter myArrayAdapter;
    EditText searchTextBox;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDbHelper = new DataBaseHelper(this);

        //Get ListView from activity_main.xml
        listView = (ListView) findViewById(R.id.listView);
        dictList = new ArrayList<DictionaryData>();
        dictList.add(new DictionaryData(-1, "Search"));
        myArrayAdapter = new MyArrayAdapter(this, dictList, "");

        //setListAdapter
        listView.setAdapter(myArrayAdapter);

        initializeAdNetwork();

        try {

            myDbHelper.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            myDbHelper.openDataBase();

        } catch (SQLException sqle) {

            throw sqle;
        }

        searchTextBox = (EditText) findViewById(R.id.searchTextBox);
        searchTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                new searchDictAsyncTask().execute("");
            }
        });
        searchTextBox.requestFocus();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initializeAdNetwork() {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.koreandictionary.app/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.koreandictionary.app/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    private class searchDictAsyncTask extends AsyncTask<String, Integer, String> {
        ArrayList<DictionaryData> dictData;

        //call to look up dictionary data
        //this method can't touch the UI
        @Override
        protected String doInBackground(String... s) {
            dictData = searchDictionary();
            final String searchText = searchTextBox.getText().toString();

            Collections.sort(dictList, new Comparator<DictionaryData>() {
                public int compare(DictionaryData s1, DictionaryData s2) {
                    return s1.getKoreanChars(searchText).length() - s2.getKoreanChars(searchText).length();
                }
            });

            return "";
        }

        //calling this method to modify UI
        @Override
        protected void onPostExecute(String s) {
            // textview for results;
            TextView textViewResults = (TextView) findViewById(R.id.textViewResults);
            if (dictList.get(0)._id != -1) {
                textViewResults.setText(dictList.size() + " results");
            } else {
                textViewResults.setText("0 results");
            }

            myArrayAdapter = new MyArrayAdapter(MainActivity.this, dictList, searchTextBox.getText().toString());
            listView.setAdapter(myArrayAdapter);
            myArrayAdapter.notifyDataSetChanged();
            listView.invalidateViews();
        }
    }

    public ArrayList<DictionaryData> searchDictionary() {

        String searchText = searchTextBox.getText().toString();


        //dictList.clear();
        dictList = myDbHelper.getDictionaryData(searchText);
        return dictList;


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            aboutMenuItem();

        }

        return super.onOptionsItemSelected(item);
    }

    private void aboutMenuItem() {


        startActivity(new Intent(this, about_me.class));

    }
}
