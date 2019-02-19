package com.unideb.tesla.timesync;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> taskNames;
    private ArrayList<String> taskResults;

    public TaskRecyclerViewAdapter() {
        taskNames = new ArrayList<>();
        taskResults = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_task_listitem, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.taskName.setText(taskNames.get(i));
        viewHolder.taskResult.setText(taskResults.get(i));

    }

    @Override
    public int getItemCount() {
        return taskNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout taskListitemParentLayout;
        TextView taskName;
        TextView taskResult;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            taskListitemParentLayout = itemView.findViewById(R.id.taskListitemParentLayout);
            taskName = itemView.findViewById(R.id.taskName);
            taskResult = itemView.findViewById(R.id.taskResult);

        }

    }

    public void addNewTaskResult(String taskName, String taskResult){

        taskNames.add(taskName);
        taskResults.add(taskResult);

        notifyItemInserted(taskNames.size() - 1);

    }

}
