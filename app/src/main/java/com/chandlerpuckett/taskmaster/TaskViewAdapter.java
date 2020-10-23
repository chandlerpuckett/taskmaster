package com.chandlerpuckett.taskmaster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskViewAdapter extends RecyclerView.Adapter<TaskViewAdapter.TaskViewHolder> {


    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    /* ---------- Configured files ----------- */

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // TODO: start here after extending class
        // choose which fragment (list item) to build

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_task, parent, false);

        // TODO 2: Go make frags

        // TODO 3.5 instantiate viewHolder
        TaskViewHolder taskViewHolder = new TaskViewHolder(view);


        return taskViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;      // make this return the list length
    }



}
