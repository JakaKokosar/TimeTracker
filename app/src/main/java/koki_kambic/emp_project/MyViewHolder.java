package koki_kambic.emp_project;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView titleTextView;
    public TextView daysWorked;
    public TextView hoursWorked;
    public MyViewHolder(View v) {
        super(v);
        titleTextView = (TextView) v.findViewById(R.id.titleTextView);
        daysWorked = (TextView) v.findViewById(R.id.daysWorked);
        hoursWorked = (TextView) v.findViewById(R.id.hoursWorked);
    }
}
