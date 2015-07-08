package com.carpooler.ui.activities;

import android.app.Activity;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import com.carpooler.R;
import com.carpooler.dao.DatabaseService;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import android.widget.DatePicker;
import android.widget.TimePicker;
import java.util.Timer;
import java.util.TimerTask;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link //SearchTripsFragment.//OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchTripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchTripsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match


    private Address startAddress;
    private Address endAddress;
    private boolean startAddressReturned = false;
    private boolean endAddressReturned = false;
    private boolean noAddressReturn = false;
    private ServiceActivityCallback callback;

    public final String TAG = "Search Trips";


    private TripSearchCallback mCallback;
    private Button mSelDateButton;
    private Button mSelTimeButton;
    private Button mSearchButton;
    private TextView mDateDisplay;
    private TextView mTimeDisplay;
    private EditText startAddressEditText;
    private EditText endAddressEditText;
    private DatePickerDialog dp;
    private TimePickerDialog tp;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMin;
    private Date startDate;
    private double mStartLong;
    private double mStartLat;
    private double mEndLong;
    private double mEndLat;



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param //param1 Parameter 1.
     * @param //param2 Parameter 2.
     * @return A new instance of fragment SearchTripsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchTripsFragment newInstance() {
        SearchTripsFragment fragment = new SearchTripsFragment();
        return fragment;
    }


    public SearchTripsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_trips, container, false);

        mSelDateButton = (Button) rootView.findViewById(R.id.btn_dateSelect);
        mSelDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dp = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        updateDateDisplay(selectedYear, selectedMonth, selectedDay);
                    }
                }, mYear, mMonth, mDay);
                dp.setTitle("Select Date");
                dp.show();
            }
        });

        mSelTimeButton = (Button) rootView.findViewById(R.id.btn_timeSelect);
        mSelTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tp = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        updateTimeDisplay(selectedHour, selectedMinute);
                    }
                }, mHour, mMin, true);//Yes 24 hour time
                tp.setTitle("Select Time");
                tp.show();
            }
        });

        mSearchButton = (Button) rootView.findViewById(R.id.btn_search);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    callback.getLocationService().getLocationFromAddressName(startAddressEditText.getText().toString(), startGeocodeCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                try {
                    callback.getLocationService().getLocationFromAddressName(endAddressEditText.getText().toString(), endGeocodeCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                setSearchData();
                Log.i(TAG, "Search Data set.");
                mCallback.search(mStartLong, mStartLat, mEndLong, mEndLat, startDate, 10, 20);
            }
        });
        mDateDisplay = (TextView) rootView.findViewById(R.id.txt_date);
        mTimeDisplay = (TextView) rootView.findViewById(R.id.txt_time);

        startAddressEditText = (EditText) rootView.findViewById(R.id.txt_startLoc);
        /*startAddressEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    try {
                        callback.getLocationService().getLocationFromAddressName(startAddressEditText.getText().toString(), startGeocodeCallback);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });*/

        endAddressEditText = (EditText) rootView.findViewById(R.id.txt_destLoc);
        /*endAddressEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //mSearchButton.setEnabled(true);
                    try {
                        callback.getLocationService().getLocationFromAddressName(endAddressEditText.getText().toString(), endGeocodeCallback);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });*/

        setCurrentDateAndTime();
        updateDateDisplay(mYear, mMonth, mDay);
        updateTimeDisplay(mHour, mMin);
        // Inflate the layout for this fragment

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (ServiceActivityCallback) activity;
        mCallback = (TripSearchCallback) activity;
    }

   /*@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dateSelect:
                dp = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        updateDateDisplay(selectedYear, selectedMonth, selectedDay);
                    }
                }, mYear, mMonth, mDay);
                dp.setTitle("Select Date");
                dp.show();

                break;
            case R.id.btn_timeSelect:
                tp = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        updateTimeDisplay(selectedHour, selectedMinute);
                    }
                }, mHour, mMin, true);//Yes 24 hour time
                tp.setTitle("Select Time");
                tp.show();
                break;
            case R.id.btn_search:
                setSearchData();
                Log.i(TAG, "The activity is visible and about to be started.");
                mCallback.search(mStartLong, mStartLat, mEndLong, mEndLat, startDate, 10, 20);
                break;
            default:
                break;
        }
    }*/

    public void setCurrentDateAndTime() {
        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMin = calendar.get(Calendar.MINUTE);
    }



    /*@Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.txt_startLoc:
                if (!hasFocus){
                    try {
                        callback.getLocationService().getLocationFromAddressName(startAddressEditText.getText().toString(), startGeocodeCallback);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.txt_destLoc:
                if (!hasFocus) {
                    //mSearchButton.setEnabled(true);
                    try {
                        callback.getLocationService().getLocationFromAddressName(endAddressEditText.getText().toString(), endGeocodeCallback);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }*/
        /*new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                noAddressReturn = true;
            }
        }, 2000);
        while (!startAddressReturned && !endAddressReturned && !noAddressReturn) {
            // wait for start and end addresses to return
            // stop if noAddressReturn
        }
        startAddressReturned = false;
        endAddressReturned = false;
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
        mCallback = null;
    }



    // Update the date in the TextView
    public void updateDateDisplay(int year, int month, int day) {
        mYear = year;
        mMonth = month;
        mDay = day;
        mDateDisplay.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(mMonth + 1).append("-").append(mDay).append("-")
                .append(mYear).append(" "));
    }

    public void updateTimeDisplay(int hour, int min) {
        mHour = hour;
        mMin = min;
        mTimeDisplay.setText(new StringBuilder().append(mHour).append(":").append(mMin).append(" "));
    }



    private DatabaseService.GeocodeCallback startGeocodeCallback = new DatabaseService.GeocodeCallback() {
        @Override
        public void doError(String message) {
            startAddressReturned = true;
            Log.i(TAG, "The activity is visible and about to be started.");
        }

        @Override
        public void doException(Exception exception) {
            startAddressReturned = true;
            Log.i(TAG, "The activity is visible and about to be started.");
        }

        @Override
        public void doSuccess(Address data) {
            startAddress = data;
            startAddressReturned = true;
        }
    };

    private DatabaseService.GeocodeCallback endGeocodeCallback = new DatabaseService.GeocodeCallback() {
        @Override
        public void doError(String message) {
            endAddressReturned = true;
        }

        @Override
        public void doException(Exception exception) {
            endAddressReturned = true;
        }

        @Override
        public void doSuccess(Address data) {
            endAddress = data;
            endAddressReturned = true;
        }
    };

    public void setSearchData() {
        mStartLat = startAddress.getLatitude();
        mStartLong = startAddress.getLongitude();
        mEndLat = endAddress.getLatitude();
        mEndLong = endAddress.getLongitude();
        startDate = new Date(mYear, mMonth, mDay, mHour, mMin);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }*/

}
