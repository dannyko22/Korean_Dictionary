package com.koreandictionary.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.*;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kobakei.ratethisapp.RateThisApp;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DataBaseHelper myDbHelper;
    ListView listView;
    ArrayList<DictionaryData> dictList;
    MyArrayAdapter myArrayAdapter;
    EditText searchTextBox;
    final Context context = this;
    private InterstitialAd interstitial;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Korean Dictionary");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.clipboard);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                Intent intent = new Intent(context, activity_notepadrecycler.class);
                startActivityForResult(intent, 1000);

            }});
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // Do whatever you want here
                //showSoftKeyboard(searchTextBox);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Do whatever you want here
                if (isKeyboardShown(searchTextBox.getRootView())) {
                    hideSoftKeyboard();
                }

            }
        };

        isStoragePermissionGranted();

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        myDbHelper = new DataBaseHelper(this);

        initializeAdNetwork();

        //Get ListView from content_main.xml
        listView = (ListView) findViewById(R.id.listView);
        dictList = new ArrayList<DictionaryData>();
        dictList.add(new DictionaryData(-1, "Search"));
        myArrayAdapter = new MyArrayAdapter(this, dictList, "", interstitial);

        //setListAdapter
        listView.setAdapter(myArrayAdapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener(){
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub

            }
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                if (isKeyboardShown(searchTextBox.getRootView())) {
                    hideSoftKeyboard();
                }

            }
        });



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


        // Monitor launch times and interval from installation
        RateThisApp.onStart(this);
        // If the criteria is satisfied, "Rate this app" dialog will be shown

        RateThisApp.Config config = new RateThisApp.Config(2,2);
        config.setUrl("market://details?id=com.torontotraffic.app");
        RateThisApp.init(config);
        RateThisApp.showRateDialogIfNeeded(this);
    }

    private void initializeAdNetwork() {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Prepare the Interstitial Ad
        interstitial = new InterstitialAd(this);
        // Insert the Ad Unit ID
        interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));

        interstitial.loadAd(adRequest);
        // Prepare an Interstitial Ad Listener
//        interstitial.setAdListener(new AdListener() {
//            public void onAdLoaded() {
//                // Call displayInterstitial() function
//                displayInterstitial();
//            }
//        });
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {



        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.notepad) {
            // Handle the notepad action
            Intent intent = new Intent(context, activity_notepadrecycler.class);
            startActivity(intent);
        } else if (id == R.id.homeapps) {
            final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/developer?id=Danny%20Ko&hl=en"));
            startActivity(intent);
        } else if (id == R.id.rateme) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            //Try Google play
            intent.setData(Uri.parse("market://details?id=com.koreandictionary.app"));
            if (!MyStartActivity(intent)) {
                //Market (Google play) app seems not installed, let's try to open a webbrowser
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.koreandictionary.app"));
                if (!MyStartActivity(intent)) {
                    //Well if this also fails, we have run out of options, inform the user.
                    Toast.makeText(this, "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
                }
            }


        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(context, about_me.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

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

            myArrayAdapter = new MyArrayAdapter(MainActivity.this, dictList, searchTextBox.getText().toString(), interstitial);
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
        else if (id == R.id.rate_me)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //Try Google play
            intent.setData(Uri.parse("market://details?id=com.koreandictionary.app"));
            if (!MyStartActivity(intent)) {
                //Market (Google play) app seems not installed, let's try to open a webbrowser
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.koreandictionary.app"));
                if (!MyStartActivity(intent)) {
                    //Well if this also fails, we have run out of options, inform the user.
                    Toast.makeText(this, "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
                }
            }
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            searchTextBox.requestFocus();
        } else {
            super.onBackPressed();
        }
    }

    private void aboutMenuItem() {


        startActivity(new Intent(this, about_me.class));

    }

    private boolean MyStartActivity(Intent aIntent) {
        try
        {
            startActivity(aIntent);
            return true;
        }
        catch (ActivityNotFoundException e)
        {
            return false;
        }
    }

    protected void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText())
        {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    protected void showSoftKeyboard(EditText input)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
        searchTextBox.requestFocus();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
        searchTextBox.requestFocus();
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                //requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }

            return false;
        }
        else { //permission is automatically granted on sdk<23 upon installation

            return true;
        }
    }

    private boolean isKeyboardShown(View rootView) {
    /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
        final int SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128;

        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
    /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
        int heightDiff = rootView.getBottom() - r.bottom;
    /* Threshold size: dp to pixels, multiply with display density */
        boolean isKeyboardShown = heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density;

        Log.d("test", "isKeyboardShown ? " + isKeyboardShown + ", heightDiff:" + heightDiff + ", density:" + dm.density
                + "root view height:" + rootView.getHeight() + ", rect:" + r);

        return isKeyboardShown;
    }

    public void displayInterstitial() {
        // If Ads are loaded, show Interstitial else show nothing.
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }
}
