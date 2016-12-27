package koki_kambic.emp_project;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyViewHolder extends RecyclerView.ViewHolder{

    public TextView titleTextView;

    public TextView daysWorked;
    public TextView hoursWorked;

    public LinearLayout card;
    public Button start;
    public Button stop;

    public MyViewHolder(View v) {
        super(v);
        card = (LinearLayout) v.findViewById(R.id.coverView);
        titleTextView = (TextView) v.findViewById(R.id.titleTextView);
        daysWorked = (TextView) v.findViewById(R.id.daysWorked);
        hoursWorked = (TextView) v.findViewById(R.id.hoursWorked);
        start =  (Button) v.findViewById(R.id.btn_start);
        stop =  (Button) v.findViewById(R.id.btn_stop);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card.setBackgroundColor(Color.parseColor("#689f38"));
                start.setEnabled(false); stop.setEnabled(true);
            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card.setBackgroundColor(Color.parseColor("#C5E1A5"));
                start.setEnabled(true); stop.setEnabled(false);
            }
        });
    }

}
