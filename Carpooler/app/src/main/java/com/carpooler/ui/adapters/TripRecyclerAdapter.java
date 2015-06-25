package com.carpooler.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carpooler.R;
import com.carpooler.dao.dto.TripData;
import com.carpooler.ui.activities.TripDetailCallback;

import java.util.List;

/**
 * Created by raymond on 6/21/15.
 */
public class TripRecyclerAdapter extends RecyclerView.Adapter<TripRecyclerAdapter.TripRowHolder> {
    private List<TripData> tripDatas;
    private TripDetailCallback callback;

    public TripRecyclerAdapter(List<TripData> tripDatas, TripDetailCallback callback) {
        this.tripDatas = tripDatas;
        this.callback = callback;
    }

    @Override
    public TripRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trip_list_row, null);
        TripRowHolder tripRowHolder = new TripRowHolder(v);
        return tripRowHolder;
    }

    @Override
    public void onBindViewHolder(TripRowHolder tripRowHolder, int i) {
        TripData data = tripDatas.get(i);
        tripRowHolder.startTime.setText(data.getStartTime().toString());
        tripRowHolder.status.setText(data.getStatus().toString());
        tripRowHolder.fromZip.setText(data.getStartLocation().getZip());
        tripRowHolder.toZip.setText(data.getEndLocation().getZip());
    }

    @Override
    public int getItemCount() {
        return null == tripDatas ? 0 : tripDatas.size();
    }

    class TripRowHolder extends RecyclerView.ViewHolder {
        TextView startTime;
        TextView status;
        TextView toZip;
        TextView fromZip;

        public TripRowHolder(View itemView) {
            super(itemView);
            startTime = (TextView) itemView.findViewById(R.id.startTime);
            status = (TextView) itemView.findViewById(R.id.status);
            toZip = (TextView) itemView.findViewById(R.id.toZip);
            fromZip = (TextView) itemView.findViewById(R.id.fromZip);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    TripData data = tripDatas.get(pos);
                    callback.onTripSelected(data.get_id());
                }
            });
        }

    }
}
