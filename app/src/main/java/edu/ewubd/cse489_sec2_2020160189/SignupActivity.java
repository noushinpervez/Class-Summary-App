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

public class SignupActivity extends AppCompatActivity {
    private EditText etUserName, etEmail, etPhone, etUserId, etPW, etRPW;
    private CheckBox cbRemUserId, cbRemPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        decideNavigation(getIntent().getBooleanExtra("FROM_LOGIN", false));

        setContentView(R.layout.activity_signup);

        etUserName = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etUserId = findViewById(R.id.etUserId);
        etPW = findViewById(R.id.etPW);
        etRPW = findViewById(R.id.etRPW);
        cbRemUserId = findViewById(R.id.cbRemUserId);
        cbRemPass = findViewById(R.id.cbRemPass);

        findViewById(R.id.btnExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        findViewById(R.id.btnGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSignup();
            }
        });
    }

    private void processSignup() {
        String userName = etUserName.getText().toString().trim();
        String userEmail = etEmail.getText().toString().trim();
        String userPhone = etPhone.getText().toString().trim();
        String userId = etUserId.getText().toString().trim();
        String userPW = etPW.getText().toString().trim();
        String userRPW = etRPW.getText().toString().trim();
        String errMsg = "";

        // check for empty fields and display error message if found
        if (userName.isEmpty() || userEmail.isEmpty() || userPhone.isEmpty() || userId.isEmpty() || userPW.isEmpty() || userRPW.isEmpty()) {
            errMsg = "Please provide information for all fields.";
            showErrorDialog(errMsg);
            return;
        }

        // user name validation
        if (userName.length() < 4 || userName.length() > 8) errMsg += "Invalid User Name, ";

        // email validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
            errMsg += "Invalid Email Address, ";

        // phone number validation
        if (!((userPhone.startsWith("+880") && userPhone.length() == 14) || (userPhone.startsWith("880") && userPhone.length() == 13) || (userPhone.startsWith("01") && userPhone.length() == 11)))
            errMsg += "Invalid Phone Number, ";

        // user id validation
        if (userId.length() < 4 || userId.length() > 6) errMsg += "Invalid User Id, ";

        // password validation
        if (userPW.length() != 4 || !userPW.equals(userRPW)) errMsg += "Invalid Password";

        if (!errMsg.isEmpty()) {
            // remove the trailing comma if it exists
            if (errMsg.endsWith(", ")) errMsg = errMsg.substring(0, errMsg.length() - 2);
            showErrorDialog(errMsg);
            return;
        }

        // store the data on shared preferences
        SharedPreferences sp = this.getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putString("USER_NAME", userName);
        e.putString("USER_EMAIL", userEmail);
        e.putString("USER_PHONE", userPhone);
        e.putString("USER_ID", userId);
        e.putString("PASSWORD", userPW);
        e.putBoolean("REM_USER", cbRemUserId.isChecked());
        e.putBoolean("REM_PASS", cbRemPass.isChecked());
        e.apply();

        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
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

    private void decideNavigation(boolean fromLogin) {
        SharedPreferences sp = this.getSharedPreferences("user_info", MODE_PRIVATE);
        String userName = sp.getString("USER_NAME", "NOT-CREATED");

        if (!userName.equals("NOT-CREATED") && !fromLogin) {
            Intent i = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }
}