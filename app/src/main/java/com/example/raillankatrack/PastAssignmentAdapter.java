package com.example.raillankatrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class PastAssignmentAdapter extends RecyclerView.Adapter<PastAssignmentAdapter.ViewHolder> {
    private ArrayList<PastAssignmentItem> assignmentItems; //create a global array list var
    private OnItemClickListener alistener; //create a listener for clicks

    public interface OnItemClickListener{
        void onItemClick(int position); //provide interface to define what to do when clicked for outsiders
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.alistener=listener;  //assign listener to the member variable
    }


    public static  class ViewHolder extends RecyclerView.ViewHolder{
        public TextView trainIdView;  //view elements on pass assignment item
        public TextView stationView;
        public TextView dateView;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) { //constructor initializes layout of the view is passed here
            super(itemView);
            trainIdView=itemView.findViewById(R.id.trainIdPastA);
            stationView=itemView.findViewById(R.id.stationsPastA);
            dateView=itemView.findViewById(R.id.datePastA);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){  //if position not out of bound
                            listener.onItemClick(position);
                        }
                    }
                }
            });


        }

    }

    public PastAssignmentAdapter(ArrayList<PastAssignmentItem> assignmentItemList){
        this.assignmentItems=assignmentItemList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //we pass the layout of the pastassignment item
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.past_assignment_item,parent, false);
        ViewHolder viewHolder=new ViewHolder(v, alistener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PastAssignmentItem currentItem=assignmentItems.get(position); //foreach item on array pass the data into views
        holder.trainIdView.setText(currentItem.getTrainId());
        holder.stationView.setText(currentItem.getSrcStation()+"-"+currentItem.getDestStation());
        holder.dateView.setText(currentItem.getDate());

    }

    @Override
    public int getItemCount() {
        return assignmentItems.size();
    }



}
