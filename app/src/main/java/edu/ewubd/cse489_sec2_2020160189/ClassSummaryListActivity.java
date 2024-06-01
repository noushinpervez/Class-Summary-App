package edu.ewubd.cse489_sec2_2020160189;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ClassSummaryListActivity extends AppCompatActivity {
    String course = "";
    private ArrayList<ClassSummary> lectures;
    private ClassSummaryAdapter csAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_summary_list);

        // retrieve course name from intent
        if (getIntent().hasExtra("course")) course = getIntent().getStringExtra("course");

        // find and set class name to TextView
        TextView tvCourse = findViewById(R.id.tvCourse);
        ListView lvLectureList = findViewById(R.id.lvClassSummary);

        tvCourse.setText(course + ": Lectures");

        lectures = new ArrayList<>();
        csAdapter = new ClassSummaryAdapter(this, lectures);
        lvLectureList.setAdapter(csAdapter);

        loadClassSummary();

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ClassSummaryListActivity.this, ClassSummaryActivity.class);
                i.putExtra("course", course);
                startActivity(i);
            }
        });

        lvLectureList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = lectures.get(i).getId();
                Intent intent = new Intent(ClassSummaryListActivity.this, ClassSummaryActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        lvLectureList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                deleteDialog(i);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClassSummary();
    }

    private void loadClassSummary() {
        String q = "SELECT * FROM lectures WHERE course = '" + course + "' ORDER BY date DESC;";

        ClassSummaryDB db = new ClassSummaryDB(this);
        Cursor cur = db.selectLectures(q);
        lectures.clear();

        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(0);
                String course = cur.getString(1);
                String type = cur.getString(2);
                long date = cur.getLong(3);
                int lecture = cur.getInt(4);
                String topic = cur.getString(5);
                String summary = cur.getString(6);

                ClassSummary cs = new ClassSummary(id, course, type, date, lecture, topic, summary);
                lectures.add(cs);
            }

            csAdapter.notifyDataSetInvalidated();
            csAdapter.notifyDataSetChanged();
        }
    }

    private void deleteRemoteLecture(String id) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String response = RemoteAccess.getInstance().remove("https://www.muthosoft.com/univ/cse489/index.php", "2020-1-60-189", "2024-1", id);
                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            protected void onPostExecute(String data) {
                if (data != null)
                    Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private void deleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClassSummaryListActivity.this, R.style.DialogTheme);
        builder.setTitle("Delete Lecture");
        builder.setMessage("Are you sure you want to delete this lecture summary?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClassSummaryDB db = new ClassSummaryDB(ClassSummaryListActivity.this);
                String id = lectures.get(position).getId();
                deleteRemoteLecture(id);
                db.deleteLecture(lectures.get(position).getId());
                lectures.remove(position);
                csAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }
}