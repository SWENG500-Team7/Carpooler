package com.carpooler.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carpooler.R;

/**
 * Created by raymond on 7/12/15.
 */
public abstract class EmptyAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int EMPTY_VIEW = 100;
    @Override
    public int getItemViewType(int position) {
        if (isEmptyData()){
            return EMPTY_VIEW;
        }else {
            return super.getItemViewType(position);
        }
    }

    protected boolean isEmptyData(){
        return getRealSize()==0;
    }
    protected abstract int getRealSize();
    protected abstract int getEmptyStringId();
    protected abstract T getRealViewHolder(ViewGroup parent, int viewType);
    protected abstract void onBindViewHolderReal(T holder, int position);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==EMPTY_VIEW){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_row, null);
            EmptyViewHolder emptyViewHolder = new EmptyViewHolder(v,getEmptyStringId());
            return emptyViewHolder;
        }else {
            return getRealViewHolder(parent,viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!(holder instanceof EmptyViewHolder)){
            onBindViewHolderReal((T) holder,position);
        }
    }

    @Override
    public int getItemCount() {
        return isEmptyData()?1:getRealSize();
    }

}
