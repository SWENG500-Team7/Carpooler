package com.carpooler.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.carpooler.R;

/**
 * Created by raymond on 7/13/15.
 */
class EmptyViewHolder extends RecyclerView.ViewHolder {
    private final TextView textView;

    public EmptyViewHolder(View itemView, int textId) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.notFound);
        textView.setText(itemView.getResources().getString(textId));
    }
}
