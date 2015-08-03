package com.carpooler.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.carpooler.R;
import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.dto.TripData;
import com.carpooler.trips.Trip;
import com.carpooler.trips.UserLoader;
import com.carpooler.users.Rating;
import com.carpooler.users.User;

/**
 * Fragment that provides users a way to review the trip and the trip's host.
 */
public class UserReviewFragment extends Fragment implements MenuItem.OnMenuItemClickListener {

    /* Fragment members */
    private static final String ARG_TRIP = "trip";
    private Trip mTrip;
    private String mTripId;
    private User mHost;
    private ServiceActivityCallback mCallback;

    /* UI Components */
    ImageView ivHost;
    TextView tvName;
    Spinner ddRating;
    EditText etReview;
    MenuItem miEdit;
    MenuItem miDelete;
    MenuItem miSave;
    ArrayAdapter<Rating> mRatingAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tripId Id of trip being reviewed.
     * @return A new instance of fragment UserReviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserReviewFragment newInstance(String tripId) {
        UserReviewFragment fragment = new UserReviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRIP, tripId);
        fragment.setArguments(args);
        return fragment;
    }

    public UserReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTripId = getArguments().getString(ARG_TRIP);
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
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_review, container, false);

        //Get references to UI inputs
        ivHost = (ImageView) rootView.findViewById(R.id.hostImage);
        tvName = (TextView) rootView.findViewById(R.id.tv_name);
        ddRating = (Spinner) rootView.findViewById(R.id.dd_rating);
        etReview = (EditText) rootView.findViewById(R.id.et_review);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Reverse rating values
        int length = Rating.values().length;
        Rating[] dropdownContent = Rating.values();
        for (int i = 0; i < length / 2; i++) {
            Rating temp = dropdownContent[i];
            dropdownContent[i] = dropdownContent[length - i - 1];
            dropdownContent[length - i - 1] = temp;
        }

        //Populate spinner
        mRatingAdapter = new ArrayAdapter<Rating>(getActivity(), android.R.layout.simple_spinner_item, dropdownContent);
        mRatingAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        ddRating.setAdapter(mRatingAdapter);

        //Get trip and user data
        try {
            mCallback.getTripDataService().getTripData(mTripId, getTripCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (ServiceActivityCallback) activity;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.mi_save_trip) {
            mHost.addRating(mCallback.getUser(),(Rating) ddRating.getSelectedItem(), etReview.getText().toString());
            mHost.saveUser();
            goHome();
        }
        return true;
    }

    public void goHome() {
        CarpoolerActivity activity = (CarpoolerActivity) getActivity();
        activity.goToHome();
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
            mTrip.loadUserData(new UserLoader.Callback() {
                @Override
                public void loadData(User user) {
                    try {
                        mHost = user;
                        mHost.loadUserImage(ivHost, 150);
                        tvName.setText(mHost.getName());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}
