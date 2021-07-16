package com.example.raillankatrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {
    private ArrayList<AssignmentItem> assignmentItemList;
    private OnItemClickListener alistener;

    public  interface OnItemClickListener{
        void onItemClick(int position); //gets the position and the click event to the main activity
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.alistener=listener;
    }
    //created to hold view details
    public static class ViewHolder extends RecyclerView.ViewHolder{ //store details on the (view assignment item)
        public TextView trainIdView;
        public TextView trainNameView;
        public TextView dateView;
        public TextView srcStationView;
        public TextView destStationView;


        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) { //constructor initializes the view holder the layout of the current assignment is passed here
            super(itemView);
            //we assign data items we need to change by referencing them
            trainIdView=itemView.findViewById(R.id.trianIdCurrA);
            trainNameView=itemView.findViewById(R.id.trianNameCurrA);
            dateView=itemView.findViewById(R.id.dateCurrA);
            srcStationView=itemView.findViewById(R.id.srcCurrA);
            destStationView=itemView.findViewById(R.id.destCurrA);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public AssignmentAdapter(ArrayList<AssignmentItem> assignmentItemList){ //pass the array of assignment items via the constructor
        this.assignmentItemList=assignmentItemList;
    }


    //automatically created when creating the class
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //we pass the layout of the card(assignmnet item)
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.assignment_item,parent, false);
        ViewHolder viewHolder=new ViewHolder(v, alistener);  //iniiate the view holder
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {  //pass data from the array to the assignment item in the list based on position
        AssignmentItem currentassignmentItem=assignmentItemList.get(position); //get the currentItem out of the list
        holder.trainIdView.setText(currentassignmentItem.getTrainId()); //assign data to the view elements
        holder.trainNameView.setText(currentassignmentItem.getTrainName());
        holder.dateView.setText(currentassignmentItem.getDate());
        holder.srcStationView.setText(currentassignmentItem.getSrcStation());
        holder.destStationView.setText(currentassignmentItem.getDestStation());
    }

    @Override
    public int getItemCount() {
        return assignmentItemList.size();
    } //return the sizes
}
