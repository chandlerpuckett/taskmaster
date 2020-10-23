package com.chandlerpuckett.taskmaster;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskViewAdapter extends RecyclerView.Adapter<TaskViewAdapter.TaskViewHolder> {

    public ArrayList<Task> tasks;
    public OnInteractWithTaskListener listener;

    public TaskViewAdapter(ArrayList<Task> tasks, OnInteractWithTaskListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public Task task;
        public View itemView;


        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }


    /* ---------- Configured files ----------- */

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_task, parent, false);

        TaskViewHolder taskViewHolder = new TaskViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println(taskViewHolder.task.title);
                listener.taskListener(taskViewHolder.task);

            }
        });

        return taskViewHolder;
    }

//    ----- custom interface -----
    public static interface OnInteractWithTaskListener {
        public void taskListener(Task task);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.task = tasks.get(position);

        TextView titleTextView = holder.itemView.findViewById(R.id.recTitle);
        TextView bodyTextView = holder.itemView.findViewById(R.id.recBody);
        TextView stateTextView = holder.itemView.findViewById(R.id.recState);

        titleTextView.setText(holder.task.title);
        bodyTextView.setText(holder.task.body);
        stateTextView.setText(holder.task.state);
    }

    @Override
    public int getItemCount() {
        return tasks.size();      // make this return the list length
    }



}
