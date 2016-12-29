package koki_kambic.emp_project;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyViewHolder extends RecyclerView.ViewHolder{
    DatabaseConnector myDb;
    Context context;
    public String UserId;
    public String TaskId;
    public String opis;
    public TextView titleTextView;
    public TextView daysWorked;
    public TextView hoursWorked;
    public LinearLayout card;
    public Button start;
    public Button stop;
    public int startTime;
    public int stopTime;

    public MyViewHolder(final View v) {
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
                Calendar c = Calendar.getInstance();
                startTime = (int)TimeUnit.MILLISECONDS.toSeconds(c.getTimeInMillis());
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card.setBackgroundColor(Color.parseColor("#C5E1A5"));
                start.setEnabled(true); stop.setEnabled(false);
                myDb = new DatabaseConnector(context);
                Calendar c = Calendar.getInstance();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                stopTime =(int) TimeUnit.MILLISECONDS.toSeconds(c.getTimeInMillis());
                Log.i("Vrednosti: ",String.valueOf(startTime)+" "+String.valueOf(stopTime) );
                myDb.open();
                myDb.AddTime(UserId,TaskId,(int)(stopTime-startTime),dateFormat.format(new Date()));
                String[] time =myDb.getTime(TaskId,UserId);
                hoursWorked.setText("Hours: "+time[1]);
                daysWorked.setText("Days: "+time[0]);

                View view1 = LayoutInflater.from(context).inflate(R.layout.activity_alert,null);
                final EditText editText = (EditText) view1.findViewById(R.id.input_description);
                AlertDialog.Builder builder = new  AlertDialog.Builder(context);
                opis ="";
                builder.setMessage("Description:")
                        .setView(view1)
                        .setPositiveButton("Add ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                opis = editText.getText().toString();
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .setCancelable(false);
                AlertDialog alert = builder.create();
                alert.show();
                if(opis.length()>=1){
                    myDb.addTaskDescription(TaskId,UserId,opis);
                }
                myDb.close();
            }
        });
    }



}
