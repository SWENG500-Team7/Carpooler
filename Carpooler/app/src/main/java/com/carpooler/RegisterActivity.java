package com.carpooler;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.carpooler.dao.VehicleRestService;
import com.carpooler.users.CarpoolHost;
import com.carpooler.users.User;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.InputStream;


public class RegisterActivity extends GoogleActivity implements OnClickListener, GoogleApiClient.ConnectionCallbacks {

    /* Intent messages */
    private static final String YEAR_MESSAGE = "com.carpooler.RegisterActivity.YEAR_MESSAGE";
    private static final String MAKE_MESSAGE = "com.carpooler.RegisterActivity.MAKE_MESSAGE";
    private static final String MODEL_MESSAGE = "com.carpooler.RegisterActivity.MODEL_MESSAGE";

    /* Profile pic image size in pixels */
    private static final int PROFILE_PIC_SIZE = 400;

    /* UI Components */
    private ImageView imgProfilePic;
    private TextView txtName, txtEmail;
    private LinearLayout llProfileLayout;
    private SignInButton btnSignIn;
    private Spinner ddYear;
    private Spinner ddMake;
    private Spinner ddModel;

    /* Flag to see if sign in button clicked */
    private boolean mSignInClicked = false;

    /* Newly registered user */
    User newUser;

    /* Newly registered host */
    CarpoolHost newHost;

    /* Vehicle menus populated through web services */
    public enum VehicleMenuEnum {
        YEAR, MAKE, MODEL
    }

    /**
     * Initialize UI components and call GoogleActivity onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initialize UI refs
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        ddYear = (Spinner) findViewById(R.id.yearDropdown);
        ddMake = (Spinner) findViewById(R.id.makeDropdown);
        ddModel = (Spinner) findViewById(R.id.modelDropdown);

        //Set selection listeners for dropdowns
        ddYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new LoadVehicleMenu().execute(VehicleMenuEnum.MAKE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ddMake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new LoadVehicleMenu().execute(VehicleMenuEnum.MODEL);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Set click listeners
        btnSignIn.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
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

    /**
     * Registration screen doesn't connect to Google on startup
     */
    @Override
    public void onStart() {
        mConnectOnStart = false;
        super.onStart();

        //Load vehicle information into dropdowns
        new LoadVehicleMenu().execute(VehicleMenuEnum.YEAR);
    }

    /**
     * Click events
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                //Connect to google when user clicks sign in button
                mGoogleApiClient.connect();
                mSignInClicked = true;
                break;
        }
    }

    /**
     * Actions to complete once connected to Google
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        //Set visibility of UI components
        updateUI(true);

        //Get profile information
        updateUIWithProfile();

        //Create new Carpooler User object
        User newUser = createNewUserFromProfile();
    }

    /**
     * Set visibility of UI components based on sign in status
     * @param isSignedIn
     */
    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnSignIn.setVisibility(View.GONE);
            llProfileLayout.setVisibility(View.VISIBLE);
        } else {
            btnSignIn.setVisibility(View.VISIBLE);
            llProfileLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Create basic User object based off Google profile
     * @return
     */
    private User createNewUserFromProfile() {
        User newUser = null;
        try {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            if (currentPerson != null) {
                newUser = new User(currentPerson.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newUser;
    }

    /**
     * Update UI components with Google profile information
     */
    private void updateUIWithProfile() {
        try {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            if (currentPerson != null) {
                String name = currentPerson.getDisplayName();
                String photoUrl = currentPerson.getImage().getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                txtName.setText(name);
                txtEmail.setText(email);

                photoUrl = photoUrl.substring(0, photoUrl.length()-2) + PROFILE_PIC_SIZE;

                new LoadProfileImage(imgProfilePic).execute(photoUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Background Async task to load user profile picture from url
     * */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private class LoadVehicleMenu extends AsyncTask<VehicleMenuEnum, Void, String[]> {
        private VehicleMenuEnum menuType = null;

        @Override
        protected String[] doInBackground(VehicleMenuEnum... params) {
            VehicleMenuEnum menu = params[0];
            menuType = menu;
            String[] results = null;
            String year, make, model;

            switch (menu) {
                case YEAR:
                    results = VehicleRestService.getYears();
                    break;
                case MAKE:
                    year = (String) ddYear.getSelectedItem();
                    results = VehicleRestService.getMakes(year);
                    break;
                case MODEL:
                    year = (String) ddYear.getSelectedItem();
                    make = (String) ddMake.getSelectedItem();
                    results = VehicleRestService.getModels(year, make);
                    break;
            }

            return results;
        }

        @Override
        protected void onPostExecute(String[] result) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_item, result);
            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            switch (menuType) {
                case YEAR:
                    ddYear.setAdapter(adapter);
                    break;
                case MAKE:
                    ddMake.setAdapter(adapter);
                    break;
                case MODEL:
                    ddModel.setAdapter(adapter);
                    break;
            }
        }
    }
}
