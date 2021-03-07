package com.example.m_vote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {

    private EditText name, phoneNo, email, password, confirmPass;
    private Button sign_up_Btn;
    TextView Sign_InTextView;
    TextView sign_up_textView;
    private ProgressDialog progressBar;
    private String token = null;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "RegNoPassword";
    private Window.Callback mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText) findViewById(R.id.name);
        phoneNo = (EditText) findViewById(R.id.phoneNo);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        confirmPass = (EditText) findViewById(R.id.confirmPass);
        sign_up_Btn = (Button) findViewById(R.id.sign_up_Button);

        mAuth = FirebaseAuth.getInstance();
        mauthListener();
        registerEvents();
    }

    private void mauthListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    //User is signed out
                    Log.d(TAG, "onAuthStateChenged:signed_out");
                }

                //[START EXCLUDE]
                updateUI(user);
                //[END_EXCLUDE]
            }
        };
        //[END auth_state_listener]
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        Toast.makeText(getApplicationContext(), "Create account", Toast.LENGTH_LONG).show();

        if (!validateForm()) {
            return;
            ;
        }

        showProgressDialog();

        //START create user

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete" + task.isSuccessful());
                        Toast.makeText(getApplicationContext(), "Create user with Email:on complete:" + task.isSuccessful(), Toast.LENGTH_LONG).show();
                        if (task.isSuccessful()) {
                            if (token != null) {
                                Intent i = new Intent(getApplicationContext(), UserProfile.class);
                                i.putExtra("TOKEN", token);
                                startActivity(i);
                                SharedPreferences sharedPreferences = getSharedPreferences("mypre", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("TOKEN", token);
                                editor.commit();
                                finish();
                            }
                        }

                        /*if sign in fails, display a message to the user. If sign in succeeds the
                        auth state listener will be notified and logic to handle the signed in user
                        can be handle in the listener*/

                        if (!task.isSuccessful()) {
                            Toast.makeText(Register.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }

                        //START EXCLUDE
                        hideProgressDialog();
                        //END EXLUDE
                    }

                });
    }

    private void hideProgressDialog()
    {
        ProgressDialog progressBar = new ProgressDialog(this);

        if (progressBar != null && progressBar.isShowing())
        {
            progressBar.hide();
        }
    }

    private void showProgressDialog() {
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);//you can cancel it by pressing back button
        progressBar.setMessage("Signing up.Please wait ...");
        progressBar.show();//displays the progress bar
    }

    private void signOut()
    {
        mAuth.signOut();
        updateUI(null);
    }

    private boolean validateForm()
    {
        boolean valid = true;

        String Email = email.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches())
        {
            //Validation for invalid email address
            email.setError("Invalid Email");
        }

        if (TextUtils.isEmpty(Email))
        {
            email.setError("Email required!");
            valid = false;
        }

        String Password = password.getText().toString();
        if (TextUtils.isEmpty(Password))
        {
            password.setError("Password required!");
            valid = false;
        }

        String cpassword = confirmPass.getText().toString();
        if (TextUtils.isEmpty(cpassword))
        {
            confirmPass.setError("Password required!");
        }

        if (!cpassword.equals(Password))
        {
            password.setError("Password does not match");
            confirmPass.setError("Password does not match");
        }

        if (password.length()<=5)
        {
            confirmPass.setError("Password is too short!");
            Toast.makeText(getApplicationContext(),"Password should be atleast 8 characters..",Toast.LENGTH_LONG).show();
        }

        String Name = name.getText().toString();
        if (TextUtils.isEmpty(Name))
        {
            name.setError("Name required!");
        }

        String Phone = phoneNo.getText().toString();
        //int phoneNumber = Integer.parseInt(String.valueOf(phoneNo));

        if (TextUtils.isEmpty(Phone))
        {
            phoneNo.setError("Phone Number required!");
        }

        return valid;
    }

    private void updateUI(FirebaseUser user)
    {
        hideProgressDialog();
        if (user != null)
        {
            token = user.getUid();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
    //End on_stop_remove_listener

    private void registerEvents()
    {

        sign_up_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateForm();
                isInternetOn();

                final String Email = email.getText().toString().trim();
                final String Password = password.getText().toString().trim();
                final String PhoneNo = phoneNo.getText().toString().trim();
                final String Name = name.getText().toString().trim();

                if (password.getText().toString().equals(confirmPass.getText().toString()))
                {

                    createAccount(email, password);

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Password does not match", Toast.LENGTH_LONG).show();
                }


            }

        });

        Sign_InTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Login.class);
                startActivity(i);
                finish();
            }
        });

    }


    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            // if connected with internet
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //Uncomment the below code to Set the message and title from the strings.xml file
            builder.setMessage(R.string.dialog_message).setTitle(R.string.dialog_title);

            //Setting message manually and performing action on button click
            builder.setMessage("Make sure that your internet connection is on?")
                    .setCancelable(false)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Internet Connection Error");
            alert.show();
            return false;
        }
        return false;
    }

}