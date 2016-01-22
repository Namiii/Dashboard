package com.applift.mobile.dashboard.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.applift.mobile.dashboard.R;

import java.util.Date;

import Adapters.TasksAdapter;
import Models.TaskModel;
import Utils.Constants;
import Utils.DateUtils;
import Utils.IdUtils;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by nami on 1/22/16.
 */
public class ProjectActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView tasksListview;
    private AlertDialog alert;
    private Realm realm;
    private TasksAdapter tasksdAdapter;
    private String projectId;
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_layout);
        Intent intent = getIntent();
        projectId = intent.getStringExtra("projectId");
        tasksListview = (ListView) findViewById(R.id.tasks_listview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeNewProjectDialog();
            }
        });
        realm = Realm.getDefaultInstance();
        realm.setAutoRefresh(true);
        realm.addChangeListener(realmCallback);
        tasksdAdapter = new TasksAdapter();
        tasksListview.setAdapter(tasksdAdapter);
        tasksListview.setOnItemClickListener(this);
        getTasks();
    }

    private void getTasks(){
        if(projectId != null) {
            RealmResults<TaskModel> tasks =
                    realm.where(TaskModel.class).equalTo("projectId", projectId).findAll();
            tasks.sort("title");
            tasksdAdapter.setTasks(tasks);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TaskModel taskModel = (TaskModel) tasksdAdapter.getItem(position);
        Intent intent = new Intent(ProjectActivity.this, ProjectDetailsActivity.class);
        intent.putExtra("taskId", taskModel.getId());
        startActivity(intent);
    }

    private RealmChangeListener realmCallback = new RealmChangeListener() {
        @Override
        public void onChange() {
            getTasks();
        }

    };

    private void makeNewProjectDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = (View) inflater.inflate(R.layout.create_task_layout,
                null);
        Button okButton  = (Button) view.findViewById(R.id.create_task_ok_button);
        Button cancelButton = (Button) view. findViewById(R.id.create_task_cancel_button);
        final EditText nameET = (EditText) view.findViewById(R.id.create_task_name_et);
        final EditText descriptionET = (EditText) view.findViewById(R.id.create_task_description_et);
        DatePicker datePicker = (DatePicker) view.findViewById(R.id.create_task_date_picker);
        date = DateUtils.getTodaysDate();
        datePicker.init(date.getYear() + 1900, date.getMonth(), date.getDate(), new CustomDateChangeListener());
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameET.getText().toString().trim().length() > 0){
                    createTask(nameET.getText().toString().trim(),
                            descriptionET.getText().toString().trim());
                    alert.dismiss();
                }else{
                    Toast.makeText(ProjectActivity.this, getResources().getString(R.string.task_name_not_chosen),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        builder.setView(view);
        builder.setCancelable(true);
        alert = builder.show();
    }
    private void createTask(String taskTitle,String description){
        TaskModel task = new TaskModel();
        task.setTitle(taskTitle);
        task.setDescription(description);
        task.setId(IdUtils.getRandomMessageId(Constants.TASK));
        task.setCreationDate(date);
        task.setIsInReview(false);
        task.setProjectId(projectId);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(task);
        realm.commitTransaction();
    }

    private class CustomDateChangeListener implements DatePicker.OnDateChangedListener{
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            date = DateUtils.setDate(year,monthOfYear,dayOfMonth);
        }
    }
}
