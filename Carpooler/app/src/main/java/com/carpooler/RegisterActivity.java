package com.carpooler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.VehicleRestService;
import com.carpooler.trips.Vehicle;
import com.carpooler.users.CarpoolHost;
import com.carpooler.users.User;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.InputStream;


public class RegisterActivity extends GoogleActivity implements OnClickListener, GoogleApiClient.ConnectionCallbacks, AdapterView.OnItemLongClickListener {

    /* Database connection */
    DatabaseService.Connection conn;

    /* Intent messages */
    private static final String YEAR_MESSAGE = "com.carpooler.RegisterActivity.YEAR_MESSAGE";
    private static final String MAKE_MESSAGE = "com.carpooler.RegisterActivity.MAKE_MESSAGE";
    private static final String MODEL_MESSAGE = "com.carpooler.RegisterActivity.MODEL_MESSAGE";

    /* Dialog IDs */
    private static final int NEW_VEHICLE_DIALOG = 0;
    private static final int REMOVE_VEHICLE_DIALOG = 1;

    /* Profile pic image size in pixels */
    private static final int PROFILE_PIC_SIZE = 400;

    /* UI Components */
    private ImageView imgProfilePic;
    private TextView txtName, txtEmail;
    private LinearLayout llProfileLayout;
    private SignInButton btnSignIn;
    private Button btnAddVehicle;
    private Button btnRegister;
    private Spinner ddYear;
    private Spinner ddMake;
    private Spinner ddModel;
    private ListView lvVehicles;

    /* Dropdown adapters */
    ArrayAdapter<String> mYearAdapter;
    ArrayAdapter<String> mMakeAdapter;
    ArrayAdapter<String> mModelAdapter;

    /* Flag to see if sign in button clicked */
    private boolean mSignInClicked = false;

    /* Newly registered user */
    User newUser;

    /* Newly registered host */
    CarpoolHost newHost;

    /* VehicleData menus populated through web services */
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
        btnAddVehicle = (Button) findViewById(R.id.btnAddVehicle);
        btnRegister = (Button) findViewById(R.id.btn_register);
        lvVehicles = (ListView) findViewById(R.id.lv_vehicles);

        //Set click listeners
        btnSignIn.setOnClickListener(this);
        btnAddVehicle.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        lvVehicles.setOnItemLongClickListener(this);
    }

    /**
     * Create method for more complicated, custom dialogs
     * @param id
     * @return
     */
    @Override
    public Dialog onCreateDialog(int id) {
        Dialog dlg = null;
        switch (id) {
            case NEW_VEHICLE_DIALOG:
                dlg = createVehicleDialog(null);
                break;
        }
        return dlg;
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

        //Bind to database
        conn = new DatabaseService.Connection();
        Intent intent = new Intent(this, DatabaseService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
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
            case R.id.btnAddVehicle:
                //Bring up new vehicle dialog on click
                showDialog(NEW_VEHICLE_DIALOG);
                break;
            case R.id.btn_register:
                //Attempt to save new user to database
                registerNewUser();
                break;
        }
    }

    /**
     * Long click event for removing vehicle list items
     * @param parent
     * @param view
     * @param position
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            //Get vehicle to remove
            final Vehicle toRemove = (Vehicle) lvVehicles.getAdapter().getItem(position);

            //Ask the user if they're sure
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.register_remove_vehicle);
            builder.setMessage("Are you sure you want to remove " + toRemove.toString() + "?");
            builder.setPositiveButton(R.string.register_remove_vehicle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Remove the vehicle and update list view
                    newHost.removeVehicle(toRemove);
                    ((BaseAdapter) lvVehicles.getAdapter()).notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), getString(R.string.register_remove_vehicle_toast), Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton(R.string.register_cancel, null);
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
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
        newUser = createNewUserFromProfile();
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
                newUser = new User(currentPerson.getId(),null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newUser;
    }

    /**
     * Added a new vehicle to the CarpoolHost, creates CarpoolHost if not yet created
     * @param pYear
     * @param pMake
     * @param pModel
     * @param pSeats
     * @param pPlate
     * @param pColor
     */
    private void addVehicle(int pYear, String pMake, String pModel, int pSeats, String pPlate, String pColor) {
        if (newHost == null) {
            newHost = new CarpoolHost();
            ArrayAdapter<Vehicle> adapter = new ArrayAdapter<Vehicle>(RegisterActivity.this,
                    android.R.layout.simple_list_item_1, newHost.getVehicles());
            lvVehicles.setAdapter(adapter);
        }

        Vehicle newVehicle = new Vehicle(pSeats, pPlate);
        newVehicle.setYear(pYear);
        newVehicle.setMake(pMake);
        newVehicle.setModel(pModel);
        newVehicle.setColor(pColor);

        newHost.addVechicle(newVehicle);
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
     * Save all the collected user and vehicle information to the database
     */
    private void registerNewUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //If not signed into google, stop
        if (newUser == null || newUser.getGoogleId() == null) {
            builder.setMessage(R.string.register_must_sign_in);
            builder.setPositiveButton("OK", null);
            builder.create().show();
            return;
        }

        //If not a host, persist regular user
        if (newHost == null || newHost.getVehicles() == null || newHost.getVehicles().size() <= 0) {
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(RegisterActivity.this, CarpoolerActivity.class);
                    startActivity(intent);
                }
            });
//            boolean persistResult = newUser.persistUser(conn);
//            if (persistResult) {
//                builder.setMessage(R.string.register_user_success);
//            } else {
//                builder.setMessage(R.string.register_fail);
//            }
            builder.create().show();
            return;
        }

        //Set user information in host
        newHost.setUser(newUser);

        //If a host with cars, persist as host
        if (newHost != null && newHost.getVehicles().size() > 0) {
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(RegisterActivity.this, /*CarpoolerActivity.class*/MainActivity.class);
                    startActivity(intent);
                }
            });
            boolean persistResult = newHost.persistHost(conn);
            if (persistResult) {
                builder.setMessage(R.string.register_host_success);
            } else {
                builder.setMessage(R.string.register_fail);
            }
            builder.create().show();
            return;
        }
    }

    /**
     * Create a dialog as an empty dialog or using predefined vehicle
     * @param pVehicle
     * @return
     */
    private Dialog createVehicleDialog(Vehicle pVehicle) {
        //Determine if dialog should set up as vehicle
        boolean newVehicle = true;
        if (pVehicle != null) {
            newVehicle = false;
        }

        //Set the view of the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View addVehicleView = inflater.inflate(R.layout.dialog_add_vehicle, null);
        builder.setView(addVehicleView)
                .setTitle(getString(R.string.register_add_vehicle));

        //Get reference to UI elements
        ddYear = (Spinner) addVehicleView.findViewById(R.id.yearDropdown);
        ddMake = (Spinner) addVehicleView.findViewById(R.id.makeDropdown);
        ddModel = (Spinner) addVehicleView.findViewById(R.id.modelDropdown);
        final Spinner ddSeats = (Spinner) addVehicleView.findViewById(R.id.seatsDropdown);
        final EditText etPlate = (EditText) addVehicleView.findViewById(R.id.licensePlateBox);
        final EditText etColor = (EditText) addVehicleView.findViewById(R.id.colorBox);

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

        //Load vehicle information into dropdowns
        new LoadVehicleMenu().execute(VehicleMenuEnum.YEAR);
        Integer[] seats = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(RegisterActivity.this, android.R.layout.simple_spinner_item, seats);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        ddSeats.setAdapter(adapter);
        //if existing vehicle, match dropdown selections
        if (!newVehicle) {
            int yearPosit = mYearAdapter.getPosition(""+pVehicle.getYear()+"");
            ddYear.setSelection(yearPosit);
            int makePosit = mMakeAdapter.getPosition(pVehicle.getMake());
            ddMake.setSelection(makePosit);
            int modelPosit = mModelAdapter.getPosition(pVehicle.getModel());
            ddModel.setSelection(modelPosit);
            int seatsPosit = adapter.getPosition(pVehicle.getSeats());
            ddSeats.setSelection(seatsPosit);
        }

        //Set confirmation button listeners
        if (newVehicle) {
            builder.setPositiveButton(getString(R.string.register_add_vehicle_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {Dialog dlg = (Dialog) dialog;
                    addVehicle(Integer.parseInt((String) ddYear.getSelectedItem()),
                            (String) ddMake.getSelectedItem(),
                            (String) ddModel.getSelectedItem(),
                            (Integer) ddSeats.getSelectedItem(),
                            etPlate.getText().toString(),
                            etColor.getText().toString());
                    Toast.makeText(getApplicationContext(), getString(R.string.register_add_vehicle_ok_toast), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            builder.setPositiveButton(getString(R.string.register_update_vehicle_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {Dialog dlg = (Dialog) dialog;
                    addVehicle(Integer.parseInt((String) ddYear.getSelectedItem()),
                            (String) ddMake.getSelectedItem(),
                            (String) ddModel.getSelectedItem(),
                            (Integer) ddSeats.getSelectedItem(),
                            etPlate.getText().toString(),
                            etColor.getText().toString());
                    Toast.makeText(getApplicationContext(), getString(R.string.register_update_vehicle_ok_toast), Toast.LENGTH_LONG).show();
                }
            });
        }


        //Set negative button
        builder.setNegativeButton(getString(R.string.register_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), getString(R.string.register_add_vehicle_cancel_toast), Toast.LENGTH_LONG).show();
            }
        });

        return builder.create();
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

    /**
     * Background task that loads the dropdown boxes for year, make, and model from the web
     */
    private class LoadVehicleMenu extends AsyncTask<VehicleMenuEnum, Void, String[]> {
        private VehicleMenuEnum menuType = null;

        /**
         * Don't allow users to change anything while data is being fetched
         */
        @Override
        protected void onPreExecute() {
            ddYear.setEnabled(false);
            ddMake.setEnabled(false);
            ddModel.setEnabled(false);
        }

        /**
         * Fetch the data based on currently selected items
         * @param params
         * @return
         */
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

        /**
         * Set the dropdowns to show results and re-enable
         * @param result
         */
        @Override
        protected void onPostExecute(String[] result) {
            switch (menuType) {
                case YEAR:
                    mYearAdapter = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_item, result);
                    mYearAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    ddYear.setAdapter(mYearAdapter);
                    break;
                case MAKE:
                    mMakeAdapter = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_item, result);
                    mMakeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    ddMake.setAdapter(mMakeAdapter);
                    break;
                case MODEL:
                    mModelAdapter = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_item, result);
                    mModelAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    ddModel.setAdapter(mModelAdapter);
                    break;
            }
            ddYear.setEnabled(true);
            ddMake.setEnabled(true);
            ddModel.setEnabled(true);
        }
    }
}
