package com.example.sin.projectone.main;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sin.projectone.Constant;
import com.example.sin.projectone.HttpUtilsAsync;
import com.example.sin.projectone.ImgManager;
import com.example.sin.projectone.OnBackPressedInterface;
import com.example.sin.projectone.ProductDBHelper;
import com.example.sin.projectone.R;
import com.example.sin.projectone.SignInActivity;
import com.example.sin.projectone.UserManager;
import com.example.sin.projectone.WebService;
import com.example.sin.projectone.item.MainItem;
import com.example.sin.projectone.payment.MainPayment;
import com.example.sin.projectone.setting.SettingsActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainItem.OnFragmentInteractionListener,MainPayment.OnFragmentInteractionListener{
    private boolean doubleBackToExitPressedOnce = false;
    private FragmentManager fragmentManager;
    private String userName= "";
    private Toolbar toolbar;
    private UserManager mManager;
    private ActionMenuView amvMenu;
    private MenuItem itemAction1, itemAction2;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private boolean flagFirstFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.deleteDatabase(ProductDBHelper.DATABASE_NAME); // debug
        checkAndReqPermission();
        mManager = new UserManager(this);
        loadProducts();
        boolean checkSession = mManager.checkSession();

        if(checkSession) {
            userName = mManager.getValue("username");
        }
        //setContentView(R.layout.content_main);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        amvMenu = (ActionMenuView) toolbar.findViewById(R.id.toolbarItem);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        fab.show();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MainActivity.this.onBackPressed();
            }
        });
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        TextView emailNavText = (TextView) headerView.findViewById(R.id.textView);
        emailNavText.setText(userName);
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container_main) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                flagFirstFragment = false;
                return;
            }
            flagFirstFragment = true;
//            // Create a new Fragment to be placed in the activity layout
//            MainPayment firstFragment = MainPayment.newInstance("", "", getMenuItem(0));
//            // In case this activity was started with special instructions from an
//            // Intent, pass the Intent's extras to the fragment as arguments
//            firstFragment.setArguments(getIntent().getExtras());
//
//            // Add the fragment to the 'fragment_container' FrameLayout
//            fragmentManager = getFragmentManager();
//            fragmentManager.beginTransaction()
//                    .add(R.id.fragment_container_main, firstFragment).commit();

        }
    }



    @Override
    public void onBackPressed() {
        Fragment containerFragment = fragmentManager.findFragmentByTag(Constant.TAG_FRAGMENT_CONTAINER);
        int childFragmentStack = 0;
        int fragmentStack = getFragmentManager().getBackStackEntryCount();
        if(containerFragment instanceof OnBackPressedInterface){
            ((OnBackPressedInterface) containerFragment).onBackPressed();
            if(fragmentStack==1){
                showBackToolbar(false);
            }
            return;
        }
        if(containerFragment!=null){
            childFragmentStack = containerFragment.getChildFragmentManager().getBackStackEntryCount();
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(childFragmentStack>0){
            containerFragment.getChildFragmentManager().popBackStack();
        } else if(fragmentStack>0){
            getFragmentManager().popBackStack();
            if(fragmentStack==1){
                showBackToolbar(false);
            }
        }
        else if(doubleBackToExitPressedOnce){
            super.onBackPressed();
        }else{
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);

        // use amvMenu here
//        getMenuInflater().inflate(R.menu.toolbar, amvMenu.getMenu());
//        Menu menuToolbar = amvMenu.getMenu();

        itemAction1 = menu.findItem(R.id.item_action1);
        itemAction2 = menu.findItem(R.id.item_action2);
        itemAction2.setActionView(R.layout.action_button_menu);
        itemAction1.setActionView(R.layout.action_button_menu);

        if(flagFirstFragment){
            flagFirstFragment = false;
            // Create a new Fragment to be placed in the activity layout
            MainPayment firstFragment = MainPayment.newInstance("", "", getMenuItem(0));
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container_main, firstFragment).commitAllowingStateLoss();
            toolbar.setTitle(getString(R.string.payment));
        }


        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent = new Intent();
        Fragment newFragment = null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_payment) {
            toolbar.setTitle(getString(R.string.payment));
            newFragment = MainPayment.newInstance("", "", getMenuItem(0));
        } else if (id == R.id.nav_product) {
            newFragment = new MainItem();
            toolbar.setTitle(getString(R.string.items));
        } else if (id == R.id.nav_report) {
            newFragment = new com.example.sin.projectone.report.Container();
            toolbar.setTitle(getString(R.string.reports));
        } else if (id == R.id.nav_receipt) {
            newFragment = new com.example.sin.projectone.receipt.Container();
            toolbar.setTitle(getString(R.string.receipts));
        } else if (id == R.id.nav_contact) {
            newFragment = new com.example.sin.projectone.help.Main();
            toolbar.setTitle(getString(R.string.contact_us));
        } else if (id == R.id.nav_logout){
                openActivity(SignInActivity.class);
        } else if (id == R.id.nav_setting){
            intent.setClass(this, SettingsActivity.class);
            startActivity(intent);
        }
        if(newFragment!=null){
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            String tag = Constant.TAG_FRAGMENT_CONTAINER;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_main, newFragment ,tag);

            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private boolean loadProducts(){
        WebService.getAllProduct(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if(response.length()>0){
                        ProductDBHelper.getInstance(MainActivity.this.getApplicationContext()).LoadProduct(response.getJSONArray("Products"));
                        ImgManager.getInstance().recoveryLostImg();
                    }
                    else if(response.length()==0){
                        System.out.println("Empty");
                    }
                    loadTransaction();
                    System.out.println("finish");
                    //ProductDBHelper.getInstance(getApplicationContext()).ShowListProduct();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return true;
    }

    private boolean loadTransaction(){
        // debug
        UserManager userManager = new UserManager(getApplicationContext());
        HttpUtilsAsync.get(Constant.URL_SEND_TRANSACTION + userManager.getShopId() /*Constant.SHOP_ID*/, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if(response.length()>0){
                        ProductDBHelper.getInstance(MainActivity.this.getApplicationContext()).loadTransaction(response.getJSONArray("transaction"));
                        for(int i=0;i<response.getJSONArray("transaction").length();i++){
                            System.out.println(response.getJSONArray("transaction").length());
                            System.out.println(response.getJSONArray("transaction").getJSONObject(i));
                            JSONObject jsonObj = response.getJSONArray("transaction").getJSONObject(i);
                            String createDate = jsonObj.getString("createAt");
                            createDate = createDate.replace(' ','T');
                            System.out.println(jsonObj.getString("transactionID"));
                            System.out.println(jsonObj.getString("createAt"));
                            HttpUtilsAsync.get(Constant.URL_GET_TRANSACTION_DETAIL+jsonObj.getString("transactionID")+"/"+createDate, null, new JsonHttpResponseHandler(){
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    System.out.println(response);
                                    try {
                                        ProductDBHelper.getInstance(MainActivity.this.getApplicationContext()).loadTransactionDetail(response.getJSONArray("transactiondetail"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                    else if(response.length()==0){
                        System.out.println("Empty");
                    }
                    System.out.println("finish");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return true;
    }

    private void openActivity(final Class className){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        Intent mainIntent = new Intent(getApplicationContext(), className);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mManager.clearSession();
                        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("com.example.sin.projectone_preferences", getApplicationContext().MODE_PRIVATE);
                        SharedPreferences.Editor mEditor = mPrefs.edit();
                        Toast toast = Toast.makeText(getApplicationContext(), "logout successful", Toast.LENGTH_SHORT);
                        toast.show();
                        mEditor.clear().commit();
                        startActivity(mainIntent);
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }


    @Override
    public void onRepleceFragment(Fragment newFragment) {
        if(newFragment!=null){
            String tag = Constant.TAG_FRAGMENT_CONTAINER;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_main, newFragment ,tag);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            showBackToolbar(true);
        }
    }

    public boolean showBackToolbar(boolean bool){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar==null)return false;
        if(bool){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            toggle.setDrawerIndicatorEnabled(false);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }else{
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            toggle.setDrawerIndicatorEnabled(true);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        return true;
    }

    public MenuItem getMenuItem(int id){
        switch (id){
            case 0 :
                return itemAction1;
            case 1:
                return itemAction2;
            default:
                return null;
        }
    }

    private void clearBackStackFragment(){

    }

    private void checkAndReqPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        Constant.PERMISSIONS_REQUEST_CAMERA);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
                //INTERNET
            }
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        Constant.PERMISSIONS_REQUEST_INTERNET);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constant.PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
