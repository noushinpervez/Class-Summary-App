package edu.ewubd.cse489_sec2_2020160189;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etUserId, etPW;
    private CheckBox cbRemUserId, cbRemPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        decideNavigation();

        setContentView(R.layout.activity_login);

        etUserId = findViewById(R.id.etUserId);
        etPW = findViewById(R.id.etPW);
        cbRemUserId = findViewById(R.id.cbRemUserId);
        cbRemPass = findViewById(R.id.cbRemPass);

        // retrieve shared preferences data
        loadStoredCredentials();

        findViewById(R.id.btnExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btnSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                i.putExtra("FROM_LOGIN", true);
                startActivity(i);
                finish();
            }
        });

        findViewById(R.id.btnGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processLogin();
            }
        });
    }

    private void loadStoredCredentials() {
        SharedPreferences sp = this.getSharedPreferences("user_info", MODE_PRIVATE);
        String storedUserId = sp.getString("USER_ID", "");
        String storedPW = sp.getString("PASSWORD", "");
        boolean remUserIdChecked = sp.getBoolean("REM_USER", false);
        boolean remPassChecked = sp.getBoolean("REM_PASS", false);

        // fill the remembered fields
        if (remUserIdChecked) {
            etUserId.setText(storedUserId);
            cbRemUserId.setChecked(remUserIdChecked);
        }

        if (remPassChecked) {
            etPW.setText(storedPW);
            cbRemPass.setChecked(remPassChecked);
        }
    }

    private void processLogin() {
        String userId = etUserId.getText().toString().trim();
        String userPW = etPW.getText().toString().trim();
        String errMsg = "";

        // check for empty fields and display error message if found
        if (userId.isEmpty() || userPW.isEmpty()) {
            errMsg = "Please enter both User ID and Password.";
            showErrorDialog(errMsg);
            return;
        }

        // retrieve stored credentials from SharedPreferences
        SharedPreferences sp = this.getSharedPreferences("user_info", MODE_PRIVATE);
        String storedUserId = sp.getString("USER_ID", "");
        String storedPW = sp.getString("PASSWORD", "");

        // credentials validation
        if (!userId.equals(storedUserId) || !userPW.equals(storedPW)) {
            // show the error message
            errMsg = "Invalid User ID or Password";
            showErrorDialog(errMsg);
            return;
        }

        SharedPreferences.Editor e = sp.edit();
        e.putBoolean("REM_USER", cbRemUserId.isChecked());
        e.putBoolean("REM_PASS", cbRemPass.isChecked());
        e.putBoolean("LOGGED_IN", true);
        e.apply();

        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void showErrorDialog(String errMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setMessage(errMessage);
        builder.setTitle("Error");
        builder.setCancelable(true);

        builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void decideNavigation() {
        SharedPreferences sp = this.getSharedPreferences("user_info", MODE_PRIVATE);
        boolean isLoggedIn = sp.getBoolean("LOGGED_IN", false);

        if (isLoggedIn) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}