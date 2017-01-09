package koki_kambic.emp_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class PrintDescription extends AppCompatActivity {
    TextView dateTxtView, timeTxtView,descriptionTxtView;
    TextView datePrint, timePrint, descriptionPrint;
    TableRow tr;
    TableLayout tableLayout;
    String TaskId, taskName, temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_description);

        TaskId = "";
        Intent intent = getIntent();
        if (intent != null) {
            final Bundle bundle = intent.getExtras();
            if (bundle != null) {
                TaskId = bundle.getString("taskId");
                taskName = bundle.getString("taskName");
            }
        }
        tableLayout =(TableLayout)findViewById(R.id.table);
        tableLayout.setColumnStretchable(0, true);
        tableLayout.setColumnStretchable(1, true);
        tableLayout.setColumnStretchable(2, true);
        dateTxtView =(TextView)findViewById(R.id.date_txtView);
        timeTxtView =(TextView) findViewById(R.id.time_txtView);
        descriptionTxtView =(TextView)findViewById(R.id.description_txtView);
        FileInputStream inputStream;





        try {
            inputStream = openFileInput(taskName);
            int c;
            temp ="";
            while( (c = inputStream.read()) != -1){
                temp = temp + Character.toString((char)c);
                if(c == '\n'){
                    tr = new TableRow(this);
                    String[] data = temp.split(" ");
                    String izpis="";
                    for (int i = 3; i<data.length; i++)
                        izpis+=data[i]+" ";
                    datePrint = new TextView(this);
                    datePrint.setText(data[0]);
                    datePrint.setGravity(Gravity.CENTER);
                    timePrint = new TextView(this);
                    timePrint.setText(calcTime(data[1]));
                    timePrint.setGravity(Gravity.CENTER);
                    descriptionPrint = new TextView(this);
                    descriptionPrint.setText(izpis);
                    descriptionPrint.setGravity(Gravity.CENTER);
                    tr.addView(datePrint);
                    tr.addView(timePrint);
                    tr.addView(descriptionPrint);
                    tableLayout.addView(tr);
                    temp ="";
                }
            }
            inputStream.close();
            //Toast.makeText(getApplicationContext(),TaskId +":"+taskName,Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            //Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }




    }

    public String calcTime(String inputTime){

        int startStopTime = Integer.parseInt(inputTime);
        int hours = startStopTime / 3600;
        int minutes = (startStopTime % 3600) / 60;
        int seconds = startStopTime % 60;
        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return timeString;
    }
}
