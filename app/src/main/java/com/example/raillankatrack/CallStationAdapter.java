package com.example.raillankatrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CallStationAdapter extends RecyclerView.Adapter<CallStationAdapter.ViewHolder> {
    private ArrayList<CallStationItem> callStationItemArrayList;
    private OnItemClickListener listener;

    public interface OnItemClickListener{ //public interface fro on item click listener
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        listener=clickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView stationNameView;
        public TextView phoneNoView;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener clickListener) {
            super(itemView);
            stationNameView = itemView.findViewById(R.id.stationName);
            phoneNoView = itemView.findViewById(R.id.phoneNo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickListener != null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            clickListener.onItemClick(position);
                        }
                    }
                }
            });
        }

    }

    public CallStationAdapter(ArrayList<CallStationItem> callStationItemArrayList){
        this.callStationItemArrayList=callStationItemArrayList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_tem, parent, false);
        ViewHolder viewHolder =  new ViewHolder(v, listener);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CallStationItem currentCallStationItem = callStationItemArrayList.get(position);
        holder.stationNameView.setText(currentCallStationItem.getStationName());
        holder.phoneNoView.setText(currentCallStationItem.getPhone());
    }

    @Override
    public int getItemCount() {
        return callStationItemArrayList.size();
    }


}
