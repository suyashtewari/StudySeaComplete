package com.example.studyseacomplete;

import android.content.ContentValues;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import static com.example.studyseacomplete.TaskDatabase.COLUMN_FINISHED;
import static com.example.studyseacomplete.TaskDatabase.COLUMN_ID;
import static com.example.studyseacomplete.TaskDatabase.TABLE_TASKS;
import static java.text.DateFormat.getDateTimeInstance;

/**
 * studySEA
 */

class Task {
    private HashMap<String, String> properties = new HashMap<>();

    Task(HashMap<String, String> properties) {
        this.properties = properties;
    }

    Task(Cursor cursor) {
        loadFromCursor(cursor);
    }

    Task(long id) {
        Cursor cursor = TaskDatabase.selectTaskById(id);
        switch (cursor.getCount()) {
            case 0:
                throw new InvalidParameterException("No rows returned");
            case 1:
                cursor.moveToFirst();
                break;
            default:
                throw new InvalidParameterException("Multiple rows returned");
        }
        loadFromCursor(cursor);
        cursor.close();
    }

    private void loadFromCursor(Cursor cursor) {
        for (int i = 0; i < TaskDatabase.TABLE_COLUMNS.length; i++)
            properties.put(TaskDatabase.TABLE_COLUMNS[i], cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabase.TABLE_COLUMNS[i])));
    }

    public HashMap<String, String> getProperties() {
        return properties;
    }

    // displays the task info in the view
    View addToView(View view) {
        TextView tvBody = (TextView) view.findViewById(R.id.tvBody);
        //TextView tvPriority = (TextView) view.findViewById(R.id.tvPriority);
        TextView tvClass = (TextView) view.findViewById(R.id.tvClass);
        TextView tvId = (TextView) view.findViewById(R.id.tvId);
        TextView tvDue = (TextView) view.findViewById(R.id.tvDue);

        tvBody.setText(properties.get(TaskDatabase.COLUMN_TASK));
        tvClass.setText(properties.get(TaskDatabase.COLUMN_CLASS));
        //tvPriority.setText(properties.get(TaskDatabase.COLUMN_PRIORITY));
        tvId.setText(properties.get(COLUMN_ID));
        tvDue.setText("Due on " + getDateTimeInstance().format(new Date(Long.parseLong(properties.get(TaskDatabase.COLUMN_DUE)))));

        return view;
    }

    // execute SQL query to INSERT task into db and return the task ID
    long insertTask() {
        ContentValues cv = new ContentValues();

        for (Map.Entry<String, String> entry : properties.entrySet())
            if (entry.getKey() != COLUMN_ID)
                cv.put(entry.getKey(), entry.getValue());

        return TaskDatabase.getDatabase().insert(TABLE_TASKS, null, cv);
    }

    void updateTask() {
        ContentValues cv = new ContentValues();

        for (Map.Entry<String, String> entry : properties.entrySet())
            if (entry.getKey() != COLUMN_ID)
                cv.put(entry.getKey(), entry.getValue());

        TaskDatabase.getDatabase().update(TABLE_TASKS, cv, COLUMN_ID + " = " + getId(), null);
    }

    void markFinished() {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FINISHED, "1");

        // TODO: delete alarms

        TaskDatabase.getDatabase().update(TABLE_TASKS, cv, COLUMN_ID + " = " + getId(), null);
    }

    void delete() {
        TaskDatabase.getDatabase().delete(TABLE_TASKS, COLUMN_ID + " = " + getId(), null);
    }

    String get(String column) {
        return properties.get(column);
    }

    public Long getId() {
        return Long.parseLong(properties.get(COLUMN_ID));
    }

    public void set(String column, String value) {
        properties.put(column, value);
    }
}
