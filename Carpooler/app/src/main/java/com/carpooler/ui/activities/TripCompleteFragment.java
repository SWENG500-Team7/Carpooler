package com.carpooler.ui.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carpooler.R;
import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.dto.TripData;
import com.carpooler.payment.PayPalResultHandler;
import com.carpooler.trips.Trip;
import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPalActivity;

/**
 * Fragment that shows a summary of the trip.
 * Has two types:
 *  - HOST type - Shows summary to host and allows toll entering
 *  - USER type - Shows summary to user and allows payment
 */
public class TripCompleteFragment extends Fragment implements MenuItem.OnMenuItemClickListener, View.OnClickListener {

    /* Fragment Types */
    public enum TripCompleteTypeEnum {
        HOST, USER
    }

    /* Args for fragment */
    private static final String ARG_TRIP_ID = "tripId";
    private static final String ARG_FRAG_TYPE = "fragType";

    /* Fragment members */
    private TripCompleteTypeEnum mFragType;
    private String mTripId;
    private Trip mTrip;
    private ServiceActivityCallback mCallback;

    /* UI Components */
    private EditText etTolls;
    private TextView tvTolls;
    private TextView tvFuel;
    private TextView tvDistance;
    private TextView tvUserTotal;
    private LinearLayout llUserTotal;
    private MenuItem miSave;
    private MenuItem miEdit;
    private MenuItem miDelete;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tripId ID of completed Trip.
     * @param fragmentType Host or User fragment view.
     * @return A new instance of fragment TripCompleteFragment.
     */
    public static TripCompleteFragment newInstance(String tripId, int fragmentType) {
        TripCompleteFragment fragment = new TripCompleteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRIP_ID, tripId);
        args.putInt(ARG_FRAG_TYPE, fragmentType);
        fragment.setArguments(args);
        return fragment;
    }

    public TripCompleteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mFragType = TripCompleteTypeEnum.values()[args.getInt(ARG_FRAG_TYPE)];
            mTripId = args.getString(ARG_TRIP_ID);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        miEdit = (MenuItem) menu.findItem(R.id.mi_edit_trip);
        miDelete = (MenuItem) menu.findItem(R.id.mi_cancel_trip);
        miSave = (MenuItem) menu.findItem(R.id.mi_save_trip);
        miEdit.setVisible(false);
        miDelete.setVisible(false);
        miSave.setOnMenuItemClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_trip_detail, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Signal to system that this fragment has it's own actionbar items
        if (mFragType.equals(TripCompleteTypeEnum.HOST)) {
            setHasOptionsMenu(true);
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_trip_complete, container, false);

        //Get references to UI inputs
        etTolls = (EditText) rootView.findViewById(R.id.et_toll);
        tvTolls = (TextView) rootView.findViewById(R.id.tv_toll);
        tvFuel = (TextView) rootView.findViewById(R.id.tv_cost);
        tvDistance = (TextView) rootView.findViewById(R.id.tv_distance);
        tvUserTotal = (TextView) rootView.findViewById(R.id.tv_total);
        llUserTotal = (LinearLayout) rootView.findViewById(R.id.llTotal);

        //Set visibility depending on fragment type
        switch (mFragType) {
            case HOST:
                etTolls.setVisibility(View.VISIBLE);
                tvTolls.setVisibility(View.GONE);
                llUserTotal.setVisibility(View.GONE);
                break;
            case USER:
                etTolls.setVisibility(View.GONE);
                tvTolls.setVisibility(View.VISIBLE);
                llUserTotal.setVisibility(View.VISIBLE);

                //Insert the paypal button
                LinearLayout ppLayout = (LinearLayout) rootView.findViewById(R.id.llPayPal);
                CheckoutButton ppButton = mCallback.getPaymentService().getPayPalButton();
                ppButton.setId(0);
                ppButton.setOnClickListener(this);
                ppLayout.addView(ppButton);
                break;
        }

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
        try {
            mCallback.getTripDataService().getTripData(mTripId, getTripCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.mi_save_trip) {
            //TODO save the toll information in the Trip
            mTrip.completeTrip();
            goBack();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == 0) {
            //TODO insert actual user cost amount

            startActivityForResult(mCallback.getPaymentService().payToUser("host@carpooler.com",
                    3.50, new PayPalResultHandler()), 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        switch (resultCode) {
            case PayPalActivity.RESULT_OK:
                String payKey = data.getStringExtra(PayPalActivity.EXTRA_PAY_KEY);
                //TODO set CarpoolUser as paid in Trip

                goBack();
                break;
            case PayPalActivity.RESULT_CANCELED:
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Your payment through PayPal was canceled. You still owe the host money.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                break;
            case PayPalActivity.RESULT_FAILURE:
                String errorMessage = data.getStringExtra(PayPalActivity.EXTRA_ERROR_MESSAGE);
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Your payment failed to process through PayPal. You still owe the host money. ERROR: "
                        + errorMessage);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                break;
        }
    }

    public void goBack() {
        CarpoolerActivity activity = (CarpoolerActivity) getActivity();
        activity.goBack(getString(R.string.title_trips));
    }

    private DatabaseService.GetCallback<TripData> getTripCallback = new DatabaseService.GetCallback<TripData>() {
        @Override
        public void doError(String message) {
            mTrip = null;
        }

        @Override
        public void doException(Exception exception) {
            mTrip = null;
        }

        @Override
        public void doSuccess(TripData data) {
            mTrip = new Trip(data, mCallback);

            //TODO Populate UI with actual Trip info
            //TODO Make sure info is different depending on if host or user is looking

            tvDistance.setText("117");
            tvFuel.setText("67.89");
            if (mFragType.equals(TripCompleteTypeEnum.USER)) {
                tvTolls.setText("3.50");
            }
        }
    };
}
