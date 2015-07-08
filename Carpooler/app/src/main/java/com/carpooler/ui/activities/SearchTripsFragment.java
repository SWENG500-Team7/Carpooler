package com.carpooler.ui.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.location.Address;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.carpooler.R;
import com.carpooler.dao.DatabaseService;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link //SearchTripsFragment.//OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SearchTripsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match


    private Address startAddress;
    private Address endAddress;
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
                initiateSearch();
            }
        });
        mDateDisplay = (TextView) rootView.findViewById(R.id.txt_date);
        mTimeDisplay = (TextView) rootView.findViewById(R.id.txt_time);

        startAddressEditText = (EditText) rootView.findViewById(R.id.txt_startLoc);
        startAddressEditText.setOnEditorActionListener(new AddressSearchActionListener(true));

        endAddressEditText = (EditText) rootView.findViewById(R.id.txt_destLoc);
        endAddressEditText.setOnEditorActionListener(new AddressSearchActionListener(false));

        setCurrentDateAndTime();
        updateDateDisplay(mYear, mMonth, mDay);
        updateTimeDisplay(mHour, mMin);
        checkAddressesFound();
        // Inflate the layout for this fragment

        return rootView;
    }

    private void initiateSearch(){
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(mYear,mMonth,mDay,mHour,mMin);
        Date startDate = calendar.getTime();
        mCallback.search(startAddress.getLongitude(), startAddress.getLatitude(),
                endAddress.getLongitude(), endAddress.getLatitude(),
                startDate, 10, 20);
    }

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


    public void setCurrentDateAndTime() {
        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMin = calendar.get(Calendar.MINUTE);
    }




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

    private void checkAddressesFound(){
        if (startAddress !=null && endAddress!=null){
            mSearchButton.setEnabled(true);
        }
    }

    private class AddressSearchCallback implements DatabaseService.GeocodeCallback {
        private final TextView addressText;
        private final boolean startAddressField;
        private AddressSearchCallback(TextView addressText, boolean startAddressField) {
            this.addressText = addressText;
            this.startAddressField = startAddressField;
        }

        @Override
        public void doError(String message) {
            Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
            addressText.requestFocus();
        }

        @Override
        public void doException(Exception exception) {
            doError(exception.getMessage());
        }

        @Override
        public void doSuccess(Address data) {
            StringBuilder sb = new StringBuilder();
            for (int i=0; i<=data.getMaxAddressLineIndex();i++){
                sb.append(data.getAddressLine(i)).append(' ');
            }

            addressText.setText(sb.toString());
            if (startAddressField){
                startAddress =data;
            }else{
                endAddress = data;
            }
            checkAddressesFound();
        }

    }

    private class AddressSearchActionListener implements TextView.OnEditorActionListener{
        private final boolean startAddressField;

        private AddressSearchActionListener(boolean startAddressField) {
            this.startAddressField = startAddressField;
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            try {
                callback.getLocationService().getLocationFromAddressName(v.getText().toString(), new AddressSearchCallback(v,startAddressField));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
