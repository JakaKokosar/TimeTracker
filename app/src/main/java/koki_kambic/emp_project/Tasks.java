package koki_kambic.emp_project;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;


public class Tasks extends AppCompatActivity {
    DatabaseConnector myDb;
    Calendar c = Calendar.getInstance();
    ArrayList<TaskModel> items = new ArrayList<>();
    ArrayList <String[]> tasks = new ArrayList<String[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        myDb = new DatabaseConnector(this);
        // get user ID
        String UserId = "";
        Intent intent = getIntent();
        if (intent != null) {
            final Bundle bundle = intent.getExtras();
            if (bundle != null) {
                UserId = bundle.getString("UserID");
            }
        }
        //get tasks array({id, taskName})
        myDb.open();
        tasks = myDb.getTasksByUserId(UserId);
        myDb.close();

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
        // open activity AddTask on click FloatingActionButton
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final String finalUserId = UserId;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Tasks.this, AddTask.class);
                Bundle bundle = new Bundle();
                bundle.putString("UserID", finalUserId);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        //FragmentManager fm = getSupportFragmentManager();
        //Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        //fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();

    }

    public void initializeList() {
        items.clear();

        for(int i =0;i<tasks.size();i++){
            TaskModel tm = new TaskModel(tasks.get(i)[0],tasks.get(i)[1]);
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
            case R.id.logout_activity: {
                Intent intent=new Intent(this, Login.class);
                //close all the Activities(baje tak piše na stackoverflow :-) )
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
        return true;
    }



}
