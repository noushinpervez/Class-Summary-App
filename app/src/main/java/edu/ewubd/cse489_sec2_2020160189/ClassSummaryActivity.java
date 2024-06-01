package edu.ewubd.cse489_sec2_2020160189;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClassSummaryActivity extends AppCompatActivity {
    private DatePickerDialog datePickerDialog;
    private Calendar getSelectedDate;
    private ClassSummaryDB summaryDB;
    private EditText etCourse, etDate, etLecture, etTopic, etSummary;
    private RadioButton rbTheory, rbLab;
    private String id = "";
    private String course = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_summary);

        if (getIntent().hasExtra("course")) course = getIntent().getStringExtra("course");

        summaryDB = new ClassSummaryDB(this);

        etCourse = findViewById(R.id.etCourse);
        etDate = findViewById(R.id.etDate);
        etLecture = findViewById(R.id.etLecture);
        etTopic = findViewById(R.id.etTopic);
        etSummary = findViewById(R.id.etSummary);

        rbTheory = findViewById(R.id.rbTheory);
        rbLab = findViewById(R.id.rbLab);

        etCourse.setText(course);

        dateSelection();

        if (getIntent().hasExtra("id")) {
            String selectedId = getIntent().getStringExtra("id");
            updateFields(selectedId);
        }

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveClassSummary();
            }
        });
    }

    private void dateSelection() {
        String currentDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date());
        etDate.setHint(currentDate);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        getSelectedDate = calendar;

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(ClassSummaryActivity.this, R.style.CalendarDialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        getSelectedDate.set(year, month, day);
                        String dateText = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(getSelectedDate.getTime());
                        etDate.setText(dateText);
                    }
                }, year, month, day);

                datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());

                if (getSelectedDate != null)
                    datePickerDialog.getDatePicker().updateDate(getSelectedDate.get(Calendar.YEAR), getSelectedDate.get(Calendar.MONTH), getSelectedDate.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });
    }

    private void saveClassSummary() {
        String errMsg = "";

        course = etCourse.getText().toString().trim();
        long date = getSelectedDate.getTimeInMillis();
        String lectureNum = etLecture.getText().toString().trim();
        String topic = etTopic.getText().toString().trim();
        String summary = etSummary.getText().toString().trim();
        String type = rbTheory.isChecked() ? rbTheory.getText().toString() : rbLab.getText().toString();

        if (course.isEmpty() || type.isEmpty() || date == 0 || lectureNum.isEmpty() || topic.isEmpty() || summary.isEmpty()) {
            errMsg = "Please provide information for all fields.";
            showMsgDialog(errMsg);
            return;
        }

        // lecture number validation
        int lecture = Integer.parseInt(lectureNum);
        if (lecture > 50 || lecture < 1) errMsg += "Lecture number must be between 1 and 50, ";

        // topic title length validation
        if (topic.length() < 3 || topic.length() > 50 || !topic.matches("^[a-zA-Z0-9 ]+$"))
            errMsg += "Topic title should be 3 to 50 characters long and only alpha-numeric, ";

        // summary length validation
        if (summary.length() < 3 || summary.length() > 500 || !summary.matches("^[a-zA-Z0-9, ]+$"))
            errMsg += "Summary should be 3 to 500 characters long and only alpha-numeric";

        if (!errMsg.isEmpty()) {
            // remove the trailing comma if it exists
            if (errMsg.endsWith(", ")) errMsg = errMsg.substring(0, errMsg.length() - 2);
            showMsgDialog(errMsg);
            return;
        }

        if (id.isEmpty()) {
            id = topic + System.currentTimeMillis();
            summaryDB.insertLecture(id, course, type, date, lecture, topic, summary);
            Toast.makeText(this, "New lecture information is inserted", Toast.LENGTH_LONG).show();
        } else {
            summaryDB.updateLecture(id, course, type, date, lecture, topic, summary);
            Toast.makeText(this, "Existing lecture information is updated", Toast.LENGTH_LONG).show();
        }

        String[] keys = {"action", "sid", "semester", "id", "course", "type", "topic", "date", "lecture", "summary"};
        String[] values = {"backup", "2020-1-60-189", "2024-1", id, course, type, topic, String.valueOf(date), String.valueOf(lecture), summary};

        httpRequest(keys, values);

        finish();
    }

    private void updateFields(String id) {
        ClassSummary selectedSummary = summaryDB.getLectureById(id);

        if (selectedSummary != null) {
            etCourse.setText(selectedSummary.course);

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            etDate.setText(dateFormat.format(new Date(selectedSummary.date)));
            getSelectedDate.setTimeInMillis(selectedSummary.date);

            etLecture.setText(String.valueOf(selectedSummary.lecture));
            etTopic.setText(selectedSummary.topic);
            etSummary.setText(selectedSummary.summary);

            if ("Theory".equals(selectedSummary.type)) rbTheory.setChecked(true);
            else if ("Lab".equals(selectedSummary.type)) rbLab.setChecked(true);

            this.id = selectedSummary.id;
        }
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
                if (data != null)
                    Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private void showMsgDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setMessage(message);
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
}