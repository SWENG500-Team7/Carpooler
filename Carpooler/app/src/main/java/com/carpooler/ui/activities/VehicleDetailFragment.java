package com.carpooler.ui.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.carpooler.R;
import com.carpooler.dao.VehicleRestService;
import com.carpooler.trips.Vehicle;
import com.carpooler.users.User;

public class VehicleDetailFragment extends Fragment implements MenuItem.OnMenuItemClickListener {

    /* Args for fragment */
    public static final String ARG_PLATE_NUM = "plateNumber";
    private static final String IS_NEW = "new";

    /* This fragment's vehicle */
    private Vehicle mVehicle;

    /* Flag to signal first time loading spinners */
    private boolean mInitSpinners = true;

    /* UI Components */
    private ServiceActivityCallback mCallback;
    private Spinner ddYear;
    private Spinner ddMake;
    private Spinner ddModel;
    private Spinner ddSeats;
    private EditText etPlateNumber;
    private EditText etColor;
    private MenuItem miEdit;
    private MenuItem miDelete;
    private MenuItem miSave;

    /* Dropdown adapters */
    ArrayAdapter<String> mYearAdapter;
    ArrayAdapter<String> mMakeAdapter;
    ArrayAdapter<String> mModelAdapter;
    ArrayAdapter<Integer> mSeatsAdapter;

    /* VehicleData menus populated through web services */
    public enum VehicleMenuEnum {
        YEAR, MAKE, MODEL
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment to show an existing Vehicle.
     *
     * @param plateNumber Plate number of detailed vehicle.
     * @return A new instance of fragment VehicleDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VehicleDetailFragment newInstance(String plateNumber) {
        VehicleDetailFragment fragment = new VehicleDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLATE_NUM, plateNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment to add a new Vehicle
     *
     * @return A new instance of fragment VehicleDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VehicleDetailFragment newInstance() {
        VehicleDetailFragment fragment = new VehicleDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLATE_NUM, IS_NEW);
        fragment.setArguments(args);
        return fragment;
    }

    public VehicleDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String plateNumber = getArguments().getString(ARG_PLATE_NUM);

            //Get vehicle info if not a new vehicle
            if (!plateNumber.equals(IS_NEW)) {
                mVehicle = mCallback.getUser().getVehicle(plateNumber);
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        miEdit = (MenuItem) menu.findItem(R.id.mi_edit_vehicle);
        miDelete = (MenuItem) menu.findItem(R.id.mi_delete_vehicle);
        miSave = (MenuItem) menu.findItem(R.id.mi_save_vehicle);
        miEdit.setOnMenuItemClickListener(this);
        miDelete.setOnMenuItemClickListener(this);
        miSave.setOnMenuItemClickListener(this);

        //Start in mode depending on if vehicle is new
        setFormEnabled(mVehicle == null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_vehicle_detail, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Signal to system that this fragment has it's own actionbar items
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_vehicle_detail, container, false);

        //Get references to UI inputs
        ddYear = (Spinner) rootView.findViewById(R.id.dd_year);
        ddMake = (Spinner) rootView.findViewById(R.id.dd_make);
        ddModel = (Spinner) rootView.findViewById(R.id.dd_model);
        ddSeats = (Spinner) rootView.findViewById(R.id.dd_seats);
        etPlateNumber = (EditText) rootView.findViewById(R.id.et_plate_number);
        etColor = (EditText) rootView.findViewById(R.id.et_color);

        //Set selection listeners for dropdowns
        ddYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new LoadVehicleSpinners().execute(VehicleMenuEnum.MAKE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ddMake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new LoadVehicleSpinners().execute(VehicleMenuEnum.MODEL);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (ServiceActivityCallback) activity;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Initialize dropdowns and text boxes
        initSpinners();
        if (mVehicle != null) {
            etPlateNumber.setText(mVehicle.getPlateNumber());
            etColor.setText(mVehicle.getColor());
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_edit_vehicle:
                setFormEnabled(true);
                break;
            case R.id.mi_delete_vehicle:
                deleteVehicle();
                goBack();
                break;
            case R.id.mi_save_vehicle:
                saveVehicle();
                setFormEnabled(false);
                goBack();
                break;
            default:
                break;
        }
        return true;
    }

    private void setFormEnabled(boolean enabled) {
        ddYear.setEnabled(enabled);
        ddMake.setEnabled(enabled);
        ddModel.setEnabled(enabled);
        ddSeats.setEnabled(enabled);
        etPlateNumber.setEnabled(enabled);
        etColor.setEnabled(enabled);

        miSave.setVisible(enabled);
        miEdit.setVisible(!enabled);
        miDelete.setVisible(!enabled);
    }

    private void initSpinners() {
        new LoadVehicleSpinners().execute(VehicleMenuEnum.YEAR);
        Integer[] seats = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        mSeatsAdapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, seats);
        mSeatsAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        ddSeats.setAdapter(mSeatsAdapter);
    }

    public void goBack()
    {
        CarpoolerActivity activity = (CarpoolerActivity) getActivity();
        activity.goBack(getString(R.string.title_vehicles));
    }

    private void saveVehicle() {
        User currentUser = mCallback.getUser();

        //Gather UI info
        if (mVehicle == null) {//If new vehicle
            mVehicle = currentUser.createVehicle();
        }
        mVehicle.setSeats((Integer) ddSeats.getSelectedItem());
        mVehicle.setPlateNumber(etPlateNumber.getText().toString());
        mVehicle.setYear(Integer.parseInt((String) ddYear.getSelectedItem()));
        mVehicle.setMake((String) ddMake.getSelectedItem());
        mVehicle.setModel((String) ddModel.getSelectedItem());
        mVehicle.setColor(etColor.getText().toString());

        currentUser.saveUser();
    }

    private void deleteVehicle() {
        User currentUser = mCallback.getUser();

        //Remove vehicle from user and save user to DB
        currentUser.removeVehicle(mVehicle);
        currentUser.saveUser();
    }

    /**
     * Background task that loads the dropdown boxes for year, make, and model from the web
     */
    private class LoadVehicleSpinners extends AsyncTask<VehicleMenuEnum, Void, String[]> {
        private VehicleMenuEnum menuType = null;

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
         * Set the dropdowns to show results
         * @param result
         */
        @Override
        protected void onPostExecute(String[] result) {
            switch (menuType) {
                case YEAR:
                    //Set up adapter with results
                    mYearAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, result);
                    mYearAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    ddYear.setAdapter(mYearAdapter);

                    //If loading existing vehicle, set selection
                    if (mInitSpinners && mVehicle != null) {
                        int pos = mYearAdapter.getPosition(Integer.toString(mVehicle.getYear()));
                        ddYear.setSelection(pos);
                    }
                    break;
                case MAKE:
                    //Set up adapater with results
                    mMakeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, result);
                    mMakeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    ddMake.setAdapter(mMakeAdapter);

                    //If loading existing vehicle, set selection
                    if (mInitSpinners && mVehicle != null) {
                        int pos = mMakeAdapter.getPosition(mVehicle.getMake());
                        ddMake.setSelection(pos);
                    }
                    break;
                case MODEL:
                    //Set up adapter with results
                    mModelAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, result);
                    mModelAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    ddModel.setAdapter(mModelAdapter);

                    //If loading existing vehicle, set selection
                    if (mInitSpinners && mVehicle != null) {
                        int pos = mModelAdapter.getPosition(mVehicle.getModel());
                        ddModel.setSelection(pos);
                        //Also set seats since this is the last in the chain
                        pos = mSeatsAdapter.getPosition(mVehicle.getSeats());
                        ddSeats.setSelection(pos);
                        //Set flag to false since we're done initializing
                        mInitSpinners = false;
                    }
                    break;
            }
        }
    }
}
