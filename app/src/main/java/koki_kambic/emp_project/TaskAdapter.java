package koki_kambic.emp_project;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by koki on 21.12.2016.
 */

public class TaskAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private ArrayList<TaskModel> list;

    public TaskAdapter(ArrayList<TaskModel> Data) {
        list = Data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.titleTextView.setText(list.get(position).getTaskName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}



