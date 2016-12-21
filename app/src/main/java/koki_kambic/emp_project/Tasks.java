package koki_kambic.emp_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;


public class Tasks extends AppCompatActivity {

    ArrayList<TaskModel> items = new ArrayList<>();
    String testItems[] = {"Work","School","Sport"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        initializeList();

        RecyclerView cardView = (RecyclerView) findViewById(R.id.cardView);
        if (items.size() > 0 & cardView != null) {
            cardView.setAdapter(new TaskAdapter(items));
        }
        SpacesItemDecoration spaces = new SpacesItemDecoration(36);

        if (cardView != null){
            cardView.setLayoutManager(new LinearLayoutManager(this));
            cardView.addItemDecoration(spaces);
        }

        //FragmentManager fm = getSupportFragmentManager();
        //Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        //fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();



        /*
        String myString = "";
        final Intent intent = getIntent();
        if (intent != null) {
            final Bundle bundle = intent.getExtras();
            if (bundle != null) {
                myString = bundle.getString("UserID");
            }
        }
        //TextView textView = (TextView) findViewById(R.id.textView);
        //textView.setText(myString);
        */

    }

    public void initializeList() {
        items.clear();

        for(int i =0;i<3;i++){
            TaskModel tm = new TaskModel();
            tm.setTaskName(testItems[i]);
            items.add(tm);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout_activity:
                finish();
        }
        return true;
    }



}
