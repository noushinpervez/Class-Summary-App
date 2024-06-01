package edu.ewubd.cse489_sec2_2020160189;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("user_info", MODE_PRIVATE);
        boolean remPass = sp.getBoolean("REM_PASS", false);

        setCardClickListener(R.id.cse489Card, "CSE489");
        setCardClickListener(R.id.cse495Card, "CSE495");
        setCardClickListener(R.id.cse438Card, "CSE438");
        setCardClickListener(R.id.cse475Card, "CSE475");

        findViewById(R.id.btnExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decideNavigation(remPass);
            }
        });

        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCourseDialog();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        String[] keys = {"action", "sid", "semester"};
        String[] values = {"restore", "2020-1-60-189", "2024-1"};
        httpRequest(keys, values);
    }

    private void httpRequest(final String[] keys, final String[] values) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                for (int i = 0; i < keys.length; i++)
                    params.add(new BasicNameValuePair(keys[i], values[i]));

                String url = "https://www.muthosoft.com/univ/cse489/index.php";

                try {
                    String data = RemoteAccess.getInstance().makeHttpRequest(url, "POST", params);
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String data) {
                if (data != null) updateLocalDBByServerData(data);
            }
        }.execute();
    }

    private void updateLocalDBByServerData(String data) {
        System.out.println("found");
        try {
            JSONObject jo = new JSONObject(data);
            ClassSummaryDB summaryDB = new ClassSummaryDB(MainActivity.this);

            if (jo.has("classes")) {
                JSONArray ja = jo.getJSONArray("classes");

                for (int i = 0; i < ja.length(); i++) {
                    JSONObject summary = ja.getJSONObject(i);

                    String id = summary.getString("id");
                    String course = summary.getString("course");
                    String topic = summary.getString("topic");
                    String type = summary.getString("type");
                    long date = summary.getLong("date");
                    int lecture = summary.getInt("lecture");
                    String sum = summary.getString("summary");

                    try {
                        summaryDB.insertLecture(id, course, type, date, lecture, topic, sum);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void setCardClickListener(int cardId, String course) {
        CardView cardView = findViewById(cardId);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCourseLectures(course);
            }
        });
    }

    private void openCourseLectures(String course) {
        Intent i = new Intent(MainActivity.this, ClassSummaryListActivity.class);
        i.putExtra("course", course);
        startActivity(i);
    }

    private void decideNavigation(boolean remPass) {
        if (!remPass) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
            builder.setTitle("Log Out");
            builder.setMessage("You will be logged out. Continue?");

            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor e = sp.edit();
                    e.remove("LOGGED_IN");
                    e.apply();

                    finish();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else finish();
    }

    private void showAddCourseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_course, null);
        dialogView.setBackgroundResource(R.drawable.rounded_corner);
        builder.setView(dialogView);
        builder.setTitle("Add Course");

        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etCode = dialogView.findViewById(R.id.etCode);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = etTitle.getText().toString();
                String code = etCode.getText().toString();

                if (!title.isEmpty() && !code.isEmpty()) dialog.dismiss();
                else
                    Toast.makeText(MainActivity.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}