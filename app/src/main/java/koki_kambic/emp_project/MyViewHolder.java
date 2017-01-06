package koki_kambic.emp_project;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
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
    private NotificationManager notificationManager;

    public String UserId;
    public String TaskId;
    public String opis;
    public TextView titleTextView;
    public TextView daysWorked;
    public TextView hoursWorked;
    public LinearLayout card;
    public Button start;
    public Button stop;
    public Button izpis;
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
        izpis =  (Button) v.findViewById(R.id.btn_izpis);


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card.setBackgroundColor(Color.parseColor("#689f38"));
                start.setEnabled(false); stop.setEnabled(true);
                Calendar c = Calendar.getInstance();
                startTime = (int)TimeUnit.MILLISECONDS.toSeconds(c.getTimeInMillis());
                createNotification(titleTextView.getText().toString());
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card.setBackgroundColor(Color.parseColor("#C5E1A5"));
                start.setEnabled(true); stop.setEnabled(false);
                Calendar c = Calendar.getInstance();
                stopTime =(int) TimeUnit.MILLISECONDS.toSeconds(c.getTimeInMillis());
                final View view1 = LayoutInflater.from(context).inflate(R.layout.activity_alert,null);
                final EditText editText = (EditText) view1.findViewById(R.id.input_description);
                final Button button = (Button) view1.findViewById(R.id.btn_Add);
                final AlertDialog.Builder builder = new  AlertDialog.Builder(context);
                opis ="";
                builder.setMessage("Opis:");
                builder.setView(view1);
                builder.setCancelable(false);

                final AlertDialog ad = builder.show();
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDb = new DatabaseConnector(context);
                        opis = editText.getText().toString();
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        myDb.open();
                        myDb.AddTime(UserId,TaskId,(int)(stopTime-startTime),dateFormat.format(new Date()));
                        String[] time =myDb.getTime(TaskId,UserId);
                        if(opis.length()>=1)
                            myDb.addTaskDescription(Integer.parseInt(TaskId),UserId,opis);
                        hoursWorked.setText("Hours: "+time[1]);
                        daysWorked.setText("Days: "+time[0]);
                        myDb.close();
                        ad.dismiss();
                        notificationManager.cancel(1);
                    }
                });
            }
        });

        izpis.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {


            }


        });
    }

    public void createNotification(String work) {
        // Prepare intent which is triggered if the
        // notification is selected
        //Intent intent = new Intent(this, NotificationReceiverActivity.class);
        //PendingIntent pIntent = PendingIntent.getActivity(getActivity(), (int) System.currentTimeMillis(), intent, 0);

        // Build notification
        // Actions are just fake
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("You are working on:")
                .setContentText(work);

        /*
        Intent notificationIntent = new Intent(context, null);

        PendingIntent contentIntent = getActivity(getActivity(), 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        */
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());



    }



}
