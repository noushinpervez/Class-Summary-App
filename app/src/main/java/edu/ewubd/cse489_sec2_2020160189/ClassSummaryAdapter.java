package edu.ewubd.cse489_sec2_2020160189;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ClassSummaryAdapter extends ArrayAdapter<ClassSummary> {
    private final Context context;
    private final ArrayList<ClassSummary> summaryArrayList;
    LayoutInflater inflater;

    public ClassSummaryAdapter(@NonNull Context context, @NonNull ArrayList<ClassSummary> items) {
        super(context, -1, items);
        this.context = context;
        this.summaryArrayList = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.row_lecture_item, parent, false);

        TextView topic = rowView.findViewById(R.id.tvTopic);
        TextView date = rowView.findViewById(R.id.tvDate);
        TextView summary = rowView.findViewById(R.id.tvSummary);

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");

        ClassSummary e = summaryArrayList.get(position);

        topic.setText(e.topic);
        date.setText(sdf.format(e.date));
        summary.setText(e.summary);

        return rowView;
    }
}