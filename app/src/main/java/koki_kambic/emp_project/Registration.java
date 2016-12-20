package koki_kambic.emp_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Registration extends AppCompatActivity  implements
        View.OnClickListener{
    DatabaseConnector myDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        myDb = new DatabaseConnector(this);
        findViewById(R.id.btn_Registration).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String user, password1, password2;
        EditText username = (EditText) findViewById(R.id.input_Username);
        EditText passwd1 = (EditText) findViewById(R.id.input_password1);
        EditText passwd2 = (EditText) findViewById(R.id.input_password2);
        TextView error = (TextView) findViewById(R.id.RegistrationError_TxtView);

        user = username.getText().toString();
        password1 = passwd1.getText().toString();
        password2 = passwd2.getText().toString();

        if((password1.length() == 0) || (password2.length() == 0) || (user.length()==0) )
            error.setText("vnesite vse podatke");
        else if(!password1.equals(password2))
            error.setText("gesli se ne ujemata");
        else{
            myDb.open();
            if(myDb.UserExist(user))
                error.setText("Uporabniško ime že obstaja");
            else{
                myDb.insertLocaluser(user, password1);
                String id = myDb.getLocalUserID(user);
                myDb.close();
                Intent intent = new Intent(Registration.this, Tasks.class);
                Bundle bundle = new Bundle();
                bundle.putString("UserID", id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
            myDb.close();
        }

    }
}
