package koki_kambic.emp_project;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


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


    public static final MediaType FORM_DATA_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    //URL
    public static final String URL="https://docs.google.com/forms/d/e/1FAIpQLSeB8pLRjXkKvx8koYkH5hQ4v1dBtzdErq187s_1O7Ab65vziA/formResponse";
    //input element ids found from the live form page
    public static final String TAKS_ENTRY = "entry.1731516864";
    public static final String DATE_ENTRY = "entry.853258547";
    public static final String TIME_ENTRY = "entry.1478802845";
    public static final String DESC_ENTRY = "entry.1811092066";



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
                        int startStopTime = (int)(stopTime-startTime);
                        int hours = startStopTime / 3600;
                        int minutes = (startStopTime % 3600) / 60;
                        int seconds = startStopTime % 60;
                        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                        myDb.AddTime(UserId,TaskId,startStopTime ,dateFormat.format(new Date()));
                        String[] time =myDb.getTime(TaskId,UserId);
                        if(opis.length()>=1) {
                            myDb.addTaskDescription(Integer.parseInt(TaskId), UserId, opis);
                        }

                        hoursWorked.setText("Hours: "+time[1]);
                        daysWorked.setText("Days: "+time[0]);
                        String name =myDb.getTaskNameById(TaskId);
                        String write = dateFormat.format(new Date()) + " " + (stopTime - startTime) + " sekund " + opis +"\n";
                        Log.i("test","1");
                        writeInFile(name, write);
                        Log.i("test","5");
                        myDb.close();
                        ad.dismiss();
                        notificationManager.cancel(1);


                        PostData saveToGoogle = new PostData();
                        saveToGoogle.execute(URL, titleTextView.getText().toString(),
                                dateFormat.format(new Date()), timeString, opis);

                    }
                });


            }
        });

        izpis.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PrintDescription.class);
                myDb = new DatabaseConnector(context);
                myDb.open();
                String name = myDb.getTaskNameById(TaskId);
                myDb.close();
                Bundle bundle = new Bundle();
                bundle.putString("taskName", name);
                bundle.putString("taskId", TaskId);

                intent.putExtras(bundle);
                context.startActivity(intent);
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
    void writeInFile(String taskName, String write){
        String filename = taskName;

        FileOutputStream outputStream;
        FileInputStream inputStream;
        String temp ="";
        try {
            inputStream = context.openFileInput(filename);
            int c;

            while( (c = inputStream.read()) != -1)
                temp = temp + Character.toString((char)c);
            inputStream.close();
        } catch (Exception e) {
            //Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show();
        }
        String string =temp + write;
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show();
        }
        try {
            inputStream = context.openFileInput(filename);
            int c;
            temp ="";
            while( (c = inputStream.read()) != -1){
                temp = temp + Character.toString((char)c);
            }
            inputStream.close();
            Toast.makeText(context,temp,Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show();
        }



    }


    private class PostData extends AsyncTask<String, Void, Boolean> {


        @Override
        protected Boolean doInBackground(String... data) {
            Boolean result = true;
            String url = data[0];
            String task = data[1];
            String date = data[2];
            String time = data[3];
            String desc = data[4];
            String userData="";


            try {
                //Strings must be URL encoded
                userData = TAKS_ENTRY+ "=" + URLEncoder.encode(task, "UTF-8") +
                        "&" + DATE_ENTRY + "=" + URLEncoder.encode(date, "UTF-8") +
                        "&" + TIME_ENTRY + "=" + URLEncoder.encode(time, "UTF-8") +
                        "&" + DESC_ENTRY + "=" + URLEncoder.encode(desc, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                result=false;
            }

            try{
                //Create OkHttpClient
                OkHttpClient client = new OkHttpClient();
                //Create the request
                RequestBody body = RequestBody.create(FORM_DATA_TYPE, userData);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                //Send the request
                Response response = client.newCall(request).execute();
            }catch (IOException exception){
                result=false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result){
            //Print Success or failure message accordingly
            Toast.makeText(context,result?"Task description saved!":"Oppss, something went wrong! SORRY",Toast.LENGTH_LONG).show();
        }
    }


}
