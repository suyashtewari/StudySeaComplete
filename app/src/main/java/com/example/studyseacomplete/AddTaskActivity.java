package com.example.studyseacomplete;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.text.DateFormat.getDateTimeInstance;

public class AddTaskActivity extends AppCompatActivity implements Serializable {
    //static final TypedArray reminderTimes = HomeworkPlanner.getContext().getResources().obtainTypedArray(R.array.ReminderTimes);
    //static final TypedArray reminderHours = HomeworkPlanner.getContext().getResources().obtainTypedArray(R.array.ReminderHours);
    //static final TypedArray reminderDays = HomeworkPlanner.getContext().getResources().obtainTypedArray(R.array.ReminderDays);
    static final String durationTimes[] = { // TODO: move to strings.xml
            "0:05 minutes",
            "0:10 minutes",
            "0:15 minutes",
            "0:20 minutes",
            "0:30 minutes",
            "0:45 minutes",
            "1:00 hour",
            "1:15 hours",
            "1:30 hours",
            "1:45 hours",
            "2:00 hours",
            "2:30 hours",
            "3:00 hours",
            "3:30 hours",
            "4:00 hours",
            "5:00 hours",
            "6:00 hours",
            "7:00 hours",
            "8:00 hours",
            "9:00 hours",
            "10:00 hours"
    };
    Task task;
    EditText taskEdit;
    EditText classEdit;
    EditText millisecondsDue;
    EditText due;
    SeekBar timeItTakes;
    SeekBar importanceLevel;
    TextView viewTimeLeft;
    Spinner reminderOptions;
    TextView viewDays;
    TextView hoursLeft;
    Spinner viewDayOptions;
    Spinner viewHourOptions;
    String reminder = "no reminder";
    ArrayAdapter<CharSequence> hourAdapter;
    ArrayAdapter<CharSequence> dayAdapter;
    ArrayAdapter<CharSequence> reminderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        long id = getIntent().getLongExtra("task", -1);
        if (id != -1)
            task = new Task(id);

        taskEdit = (EditText) findViewById(R.id.nameLine);
        classEdit = (EditText) findViewById(R.id.classNameLine);
        timeItTakes = (SeekBar) findViewById(R.id.timeDurationTwo);
        millisecondsDue = (EditText) findViewById(R.id.etDueMillis);
        due = (EditText) findViewById(R.id.selectDayandTime);
        viewTimeLeft = (TextView) findViewById(R.id.timeDurationOne);
        importanceLevel = (SeekBar) findViewById(R.id.importanceScroll);
        reminderOptions = (Spinner) findViewById(R.id.sReminder);
        viewDays = (TextView) findViewById(R.id.tvReminderDays);
        hoursLeft = (TextView) findViewById(R.id.tvReminderHours);
        viewDayOptions = (Spinner) findViewById(R.id.sReminderDays);
        viewHourOptions = (Spinner) findViewById(R.id.sReminderHours);

        hoursLeft.setVisibility(View.INVISIBLE);
        viewDays.setVisibility(View.INVISIBLE);
        viewHourOptions.setVisibility(View.INVISIBLE);
        viewDayOptions.setVisibility(View.INVISIBLE);

        hourAdapter = ArrayAdapter.createFromResource(this, R.array.ReminderHours, android.R.layout.simple_spinner_item);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewHourOptions.setAdapter(hourAdapter);

        dayAdapter = ArrayAdapter.createFromResource(this, R.array.ReminderDays, android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewDayOptions.setAdapter(dayAdapter);

        reminderAdapter = ArrayAdapter.createFromResource(this, R.array.ReminderTimes, android.R.layout.simple_spinner_item);
        reminderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reminderOptions.setAdapter(reminderAdapter);
        reminderOptions.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (((String) parent.getItemAtPosition(pos)).compareTo("Custom") == 0) {
                    hoursLeft.setVisibility(View.VISIBLE);
                    viewDays.setVisibility(View.VISIBLE);
                    viewHourOptions.setVisibility(View.VISIBLE);
                    viewDayOptions.setVisibility(View.VISIBLE);
                } else {
                    hoursLeft.setVisibility(View.INVISIBLE);
                    viewDays.setVisibility(View.INVISIBLE);
                    viewHourOptions.setVisibility(View.INVISIBLE);
                    viewDayOptions.setVisibility(View.INVISIBLE);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        due.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus)
                    onDateClick(view);
            }
        });

        if (task == null) {
            setTitle("ADD A NEW ASSIGNMENT");
            millisecondsDue.setText(Long.toString(Calendar.getInstance().getTimeInMillis()));
            findViewById(R.id.bDelete).setVisibility(View.INVISIBLE);
            timeItTakes.setProgress(4);
            viewTimeLeft.setText(durationTimes[4]);
        } else {
            setTitle("EDIT THIS ASSIGNMENT");
            ((Button) findViewById(R.id.bSubmit)).setText("Save");

            // fill inputs with task properties
            taskEdit.setText(task.get(TaskDatabase.COLUMN_TASK));
            classEdit.setText(task.get(TaskDatabase.COLUMN_CLASS));
            setDateTime(Long.parseLong(task.get(TaskDatabase.COLUMN_DUE)));
            timeItTakes.setProgress(Integer.parseInt(task.get(TaskDatabase.COLUMN_DURATION_UI)));
            viewTimeLeft.setText(durationTimes[Integer.parseInt(task.get((TaskDatabase.COLUMN_DURATION_UI)))]);
            reminderOptions.setSelection(Integer.parseInt(task.get(TaskDatabase.COLUMN_REMINDER_UI)));
            viewHourOptions.setSelection(Integer.parseInt(task.get(TaskDatabase.COLUMN_REMINDER_HOURS)));
            viewDayOptions.setSelection(Integer.parseInt(task.get(TaskDatabase.COLUMN_REMINDER_DAYS)));
            importanceLevel.setProgress(Integer.parseInt(task.get(TaskDatabase.COLUMN_IMPORTANCE)));
        }

        timeItTakes.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                viewTimeLeft.setText(durationTimes[progress]);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    // callback for the datetime picker that's triggered by onDateClick
    public void setDateTime(int year, int month, int dayOfMonth, int hour, int minute) {
        Calendar datetime = new GregorianCalendar(year, month, dayOfMonth, hour, minute);

        millisecondsDue.setText(Long.toString(datetime.getTimeInMillis()));
        due.setText(getDateTimeInstance().format(datetime.getTime()));
    }

    public void setDateTime(long millis) {
        millisecondsDue.setText(Long.toString(millis));
        due.setText(getDateTimeInstance().format(new Date(millis)));
    }

    // display the date & time picker dialogs
    public void onDateClick(View v) {
        new DateTimePicker(this, Long.parseLong("" + millisecondsDue.getText())).show();
    }

    // convert string displayed above duration slider/seekbar to miliseconds (as string)
    private String getDuration() throws InvalidParameterException {
        Pattern p = Pattern.compile("^([0-9]+):([0-9]+)");
        Matcher match = p.matcher(durationTimes[timeItTakes.getProgress()]);
        match.find();

        Integer hours = new Integer(match.group(1));
        Integer minutes = new Integer(match.group(2));
        return new Integer(hours * 60 * 60 * 1000 + minutes * 60 * 1000).toString(); // calculate miliseconds and return
    }

    // return the amount of time before the task is due to display a reminder
    private String getReminderTime() throws InvalidParameterException {
        switch ((String) reminderOptions.getSelectedItem()) {
            case "Custom":
                Integer days = new Integer(viewDayOptions.getSelectedItem().toString());
                Integer hours = new Integer(viewHourOptions.getSelectedItem().toString());

                return new Integer(days * 24 * 60 * 60 * 1000 + days * 60 * 60 * 1000).toString();
            case "None":
                return "0";
            default:
                Pattern pattern = Pattern.compile("^(\\d+) (\\w+[^s])s?$");
                Matcher match = pattern.matcher((String) reminderOptions.getSelectedItem());
                match.find();

                Integer num = new Integer(match.group(1));
                switch (match.group(2)) {
                    case "minute":
                        return new Integer(num * 60 * 1000).toString();
                    case "hour":
                        return new Integer(num * 60 * 60 * 1000).toString();
                    case "day":
                        return new Integer(num * 24 * 60 * 60 * 1000).toString();
                    case "week":
                        return new Integer(num * 7 * 24 * 60 * 60 * 1000).toString();
                    default:
                        throw new InvalidParameterException();
                }
        }
    }

    // create the new task (called when "add task" button is pressed)
    public void submit(View v) throws Exception {
        // TODO: better error handling
        if (taskEdit.getText().length() == 0)
            Snackbar.make(findViewById(android.R.id.content), "Please enter a task name", Snackbar.LENGTH_LONG)
                    .show();

        else if (classEdit.getText().length() == 0)
            Snackbar.make(findViewById(android.R.id.content), "Please enter a class name", Snackbar.LENGTH_LONG)
                    .show();

        else {
            HashMap<String, String> properties = new HashMap<>();

            // load existing task id if editing a task
            if (task != null)
                properties.put(TaskDatabase.COLUMN_ID, task.getId().toString());

            properties.put(TaskDatabase.COLUMN_TASK, taskEdit.getText().toString());
            properties.put(TaskDatabase.COLUMN_CLASS, classEdit.getText().toString());
            properties.put(TaskDatabase.COLUMN_DUE, millisecondsDue.getText().toString());
            properties.put(TaskDatabase.COLUMN_DURATION, getDuration());
            properties.put(TaskDatabase.COLUMN_IMPORTANCE, Integer.toString(importanceLevel.getProgress()));
            properties.put(TaskDatabase.COLUMN_REMINDER, getReminderTime());
            properties.put(TaskDatabase.COLUMN_DURATION_UI, Integer.toString(timeItTakes.getProgress()));
            properties.put(TaskDatabase.COLUMN_REMINDER_UI, Long.toString(reminderOptions.getSelectedItemId()));
            properties.put(TaskDatabase.COLUMN_REMINDER_DAYS, viewDayOptions.getSelectedItem().toString());
            properties.put(TaskDatabase.COLUMN_REMINDER_HOURS, viewHourOptions.getSelectedItem().toString());

            if (task == null) {
                task = new Task(properties);

                // add new task to database
                Long id = new Long(task.insertTask());
                task.set(TaskDatabase.COLUMN_ID, id.toString());
            } else {
                task = new Task(properties);

                // cancel old alarms
                Alarm.cancelOverdueAlarm(task.getId());
                Alarm.cancelReminderAlarm(task.getId());

                // update task in database
                task.updateTask();
            }

            long now = Calendar.getInstance().getTimeInMillis();

            // create an overdue alarm for the task
            if (now < Long.parseLong(task.get(TaskDatabase.COLUMN_DUE)))
                Alarm.setOverdueAlarm(task);

            // create a reminder alarm for the task
            if (task.get(TaskDatabase.COLUMN_REMINDER) != "0")
                Alarm.setReminderAlarm(task);

            setResult(RESULT_OK, null);
            finish();
        }
    }

    // called when delete button is clicked
    public void delete(final View view) {
        new AlertDialog.Builder(view.getContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to permanently delete this task?")   // TODO: move to strings.xml
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        task.delete();
                        setResult(RESULT_OK, null);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
