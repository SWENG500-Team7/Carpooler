package com.carpooler.ui.activities;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carpooler.R;
import com.carpooler.dao.dto.UserReviewData;
import com.carpooler.trips.UserLoader;
import com.carpooler.trips.Vehicle;
import com.carpooler.ui.adapters.CarpoolUserAdapter;
import com.carpooler.ui.adapters.TripRowHolder;
import com.carpooler.users.Rating;
import com.carpooler.users.User;

import java.util.List;

public class TripViewFragment extends AbstractTripView {

    /* UI Componenets */
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private CarpoolUserAdapter carpoolUserAdapter;
    private TripRowHolder tripRowHolder;
    private TextView tvColor;
    private TextView tvYearMakeModel;
    private TextView tvLicensePlate;
    private TextView tvRating;
    private Button btnReviews;
    private String reviews;

    @Override
    protected void setupData() {
        tripRowHolder.loadData(trip);
        carpoolUserAdapter = new CarpoolUserAdapter(trip, callback);
        recyclerView.setAdapter(carpoolUserAdapter);
    }

    @Override
    protected void preLoadData() {
        if (!refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(true);
        }
    }

    @Override
    protected void postLoadData() {
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
        trip.loadUserData(new UserLoader.Callback() {
            @Override
            public void loadData(User user) {
                setRating(user.getRating());
                setReviews(user.getReviews());
            }
        });
        trip.loadVehicleData(new UserLoader.VehicleCallback() {
            @Override
            public String getVehicleId() {
                return trip.getVehiclePlatNumber();
            }

            @Override
            public void loadData(Vehicle vehicle) {
                setVehicle(vehicle);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trip_view, container, false);
        setupArgs();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.trip_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.contentView);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        tripRowHolder = new TripRowHolder(rootView, callback, false);

        tvColor = (TextView) rootView.findViewById(R.id.tv_color);
        tvYearMakeModel = (TextView) rootView.findViewById(R.id.tv_year_make_model);
        tvLicensePlate = (TextView) rootView.findViewById(R.id.tv_license_plate);
        tvRating = (TextView) rootView.findViewById(R.id.tv_rating);
        btnReviews = (Button) rootView.findViewById(R.id.reviewsButton);

        btnReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Reviews");
                alertDialog.setMessage(reviews);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        return rootView;
    }

    private void setVehicle(Vehicle vehicle) {
        if (vehicle != null) {
            tvColor.setText(vehicle.getColor());
            tvYearMakeModel.setText(vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel());
            tvLicensePlate.setText(vehicle.getPlateNumber());
        }
    }

    private void setReviews(List<UserReviewData> data) {
        reviews = "";
        for (UserReviewData review : data) {
            reviews += "----User " + review.getUserId() + "----\n\n";
            reviews += review.getComment() + "\n\n";
        }
    }

    private void setRating(Rating rating) {
        if (rating != null) {
            switch (rating) {
                case A:
                    tvRating.setText("A");
                    tvRating.setBackgroundColor(Color.GREEN);
                    break;
                case B:
                    tvRating.setText("B");
                    tvRating.setBackgroundColor(Color.GREEN);
                    break;
                case C:
                    tvRating.setText("C");
                    tvRating.setBackgroundColor(Color.YELLOW);
                    break;
                case D:
                    tvRating.setText("D");
                    tvRating.setBackgroundColor(Color.YELLOW);
                    break;
                case E:
                    tvRating.setText("E");
                    tvRating.setBackgroundColor(Color.RED);
                    break;
                case F:
                    tvRating.setText("F");
                    tvRating.setBackgroundColor(Color.RED);
                    break;
            }
        }
    }
}
