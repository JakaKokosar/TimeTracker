package koki_kambic.emp_project;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class Login extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, OnClickListener{

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 4949;

    private static final String TAG = "SignInActivity";
    DatabaseConnector myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myDb = new DatabaseConnector(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        findViewById(R.id.btn_SignIn_google).setOnClickListener(this);
        findViewById(R.id.btn_SignIn).setOnClickListener(this);
        findViewById(R.id.link_signup).setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_SignIn_google:
                if (mGoogleApiClient.isConnected()) {
                    signOut();
                }
                signIn();
                break;
            case R.id.btn_SignIn:
                checkUser();
                break;
            case R.id.link_signup:
                regUser();
                break;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        switch(requestCode){
            case RC_SIGN_IN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
                break;
        }


    }
    // check if username and password for local user are correct
    private void checkUser(){
        TextView err = (TextView) findViewById(R.id.loginError_TxtView);
        EditText username_ET = (EditText) findViewById(R.id.input_user);
        EditText passwd_ET = (EditText) findViewById(R.id.input_password);
        String username = username_ET.getText().toString();
        String password=passwd_ET.getText().toString();
        myDb.open();
        boolean userExist = myDb.UserExist(username);
        if (userExist) {
            boolean checkPasswd =myDb.CheckPassword(username, password);
            if(checkPasswd){
                Intent intent = new Intent(Login.this, Tasks.class);
                Bundle bundle = new Bundle();
                bundle.putString("UserID", myDb.getLocalUserID(username));
                intent.putExtras(bundle);
                startActivity(intent);}
            else err.setText("Napačno uporabniško ime ali geslo");
        }
        else err.setText("Uporabnik ne obstaja");
        myDb.close();
    }

    private void regUser(){
        Intent intent = new Intent(Login.this, Registration.class);
        startActivity(intent);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        // ...
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            // TODO: POJDI NA NOV ACTIVITY, S SABO NESI VSE POTRENE INFO
            GoogleSignInAccount acct = result.getSignInAccount();
            assert acct != null;
            //Toast toast = Toast.makeText(Login.this, acct.getId(), Toast.LENGTH_SHORT);
            //toast.show();  String mFullName = acct.getDisplayName();
            String googleID = acct.getId();
            //save id in database
            //myDb.insertUserID(Integer.parseInt(googleID));

            //send id to task activity
            Intent intent = new Intent(Login.this, Tasks.class);
            Bundle bundle = new Bundle();
            bundle.putString("UserID", googleID);
            intent.putExtras(bundle);
            startActivity(intent);

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Connection failed, google api is not available
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


}


