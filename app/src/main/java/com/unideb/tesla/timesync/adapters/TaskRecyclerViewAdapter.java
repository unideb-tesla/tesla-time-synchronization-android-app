package com.unideb.tesla.timesync.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unideb.tesla.timesync.R;

import java.util.ArrayList;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> taskNames;
    private ArrayList<Boolean> taskResults;
    private Context context;

    public TaskRecyclerViewAdapter(Context context) {
        taskNames = new ArrayList<>();
        taskResults = new ArrayList<>();
        this.context = context;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if(taskResults.get(i)) {
                viewHolder.taskResultImage.setImageDrawable(context.getDrawable(R.drawable.ic_check_green_24dp));
            }else{
                viewHolder.taskResultImage.setImageDrawable(context.getDrawable(R.drawable.ic_close_red_24dp));
            }

        }

    }

    @Override
    public int getItemCount() {
        return taskNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout taskListitemParentLayout;
        TextView taskName;
        ImageView taskResultImage;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            taskListitemParentLayout = itemView.findViewById(R.id.taskListitemParentLayout);
            taskName = itemView.findViewById(R.id.taskName);
            taskResultImage = itemView.findViewById(R.id.taskResultImage);

        }

    }

    public void addNewTaskResult(String taskName, boolean taskResult){

        taskNames.add(taskName);
        taskResults.add(taskResult);

        notifyItemInserted(taskNames.size() - 1);

    }

}
