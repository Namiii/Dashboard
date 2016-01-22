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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applift.mobile.dashboard.R;

import java.util.Date;

import Models.CommentModel;
import Models.TaskModel;
import Utils.Constants;
import Utils.DateUtils;
import Utils.IdUtils;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by nami on 1/22/16.
 */
public class ProjectDetailsActivity extends AppCompatActivity {
    private Realm realm;
    private String taskId;
    private TaskModel taskModel;
    private EditText nameEt, descriptionEt;
    private DatePicker datePicker;
    private Date date;
    private Button reviewButton;
    private LinearLayout commentsLayout;
    private AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);
        Intent intent = getIntent();
        taskId = intent.getStringExtra("taskId");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameEt.getText().toString().trim().length() > 0){
                    saveData();
                } else {
                    Toast.makeText(ProjectDetailsActivity.this,
                            getResources().getString(R.string.task_name_not_chosen),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        nameEt = (EditText) findViewById(R.id.details_name_et);
        descriptionEt = (EditText) findViewById(R.id.details_description_et);
        datePicker = (DatePicker) findViewById(R.id.details_date_picker);
        reviewButton = (Button) findViewById(R.id.details_review_button);
        commentsLayout = (LinearLayout) findViewById(R.id.comments_section);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!taskModel.isInReview()) {
                setInReview();
                } else {
                    Toast.makeText(ProjectDetailsActivity.this, getResources().getString(R.string.in_review),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        realm = Realm.getDefaultInstance();
        getDetails();
    }
    private void getDetails(){
        taskModel = realm.where(TaskModel.class).equalTo("id", taskId).findFirst();
        nameEt.setText(taskModel.getTitle());
        descriptionEt.setText(taskModel.getDescription());
        date = taskModel.getCreationDate();
        datePicker.init(date.getYear(), date.getMonth(), date.getDate(), new CustomDateChangeListener());
        if(!taskModel.isInReview()){
            reviewButton.setText(getResources().getString(R.string.submit_q_a));
        }else{
            reviewButton.setText(getResources().getString(R.string.already_submit_q_a));
        }
        setComments();
    }
    private void setInReview(){
        realm.beginTransaction();
        taskModel.setIsInReview(true);
        realm.commitTransaction();
        reviewButton.setText(getResources().getString(R.string.already_submit_q_a));
    }

    private void saveData(){
        realm.beginTransaction();
        taskModel.setTitle(nameEt.getText().toString().trim());
        taskModel.setDescription(descriptionEt.getText().toString().trim());
        taskModel.setId(IdUtils.getRandomMessageId(Constants.TASK));
        taskModel.setCreationDate(date);
        realm.commitTransaction();
        Toast.makeText(ProjectDetailsActivity.this, getResources().getString(R.string.changes_done),
                Toast.LENGTH_SHORT).show();
    }

    private class CustomDateChangeListener implements DatePicker.OnDateChangedListener{
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            date = DateUtils.setDate(year, monthOfYear, dayOfMonth);
        }
    }

    private void setComments(){
        commentsLayout.removeAllViews();
        RealmResults<CommentModel> comments =
                realm.where(CommentModel.class).equalTo("taskId", taskId).findAll();
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View headerView = inflater.inflate(R.layout.comment_header_layout,
                null);
        commentsLayout.addView(headerView);
        for(CommentModel comment: comments){
            View commentView = inflater.inflate(R.layout.comment_row_layout,
                    null);
            TextView userText = (TextView) commentView.findViewById(R.id.comments_row_user);
            TextView commentText = (TextView) commentView.findViewById(R.id.comments_row_text);
            userText.setText(comment.getUser());
            commentText.setText(comment.getCommentText());
            commentsLayout.addView(commentView);
        }
        View addView = inflater.inflate(R.layout.comment_add_layout,
                null);
        Button addButton = (Button) addView.findViewById(R.id.comment_add_button);
        addButton.setOnClickListener(new AddCommentOnClickListener());
        commentsLayout.addView(addView);
    }

    private void addComment(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = (View) inflater.inflate(R.layout.create_comment_layout,
                null);
        Button okButton  = (Button) view.findViewById(R.id.create_comment_ok_button);
        Button cancelButton = (Button) view. findViewById(R.id.create_comment_cancel_button);
        final EditText nameET = (EditText) view.findViewById(R.id.create_comment_name_et);
        final EditText textET = (EditText) view.findViewById(R.id.create_comment_text_et);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameET.getText().toString().trim().length() > 0){
                    if(textET.getText().toString().trim().length() > 0) {
                        saveComment(nameET.getText().toString().trim(),
                                textET.getText().toString().trim());
                        alert.dismiss();
                    }else{
                        Toast.makeText(ProjectDetailsActivity.this,
                                getResources().getString(R.string.no_comment),
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ProjectDetailsActivity.this,
                            getResources().getString(R.string.no_name),
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

    private void saveComment(String name, String text){
        CommentModel commentModel = new CommentModel();
        commentModel.setId(IdUtils.getRandomMessageId(Constants.COMMENT));
        commentModel.setCreationDate(DateUtils.getTodaysDate());
        commentModel.setUser(name);
        commentModel.setTaskId(taskId);
        commentModel.setCommentText(text);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(commentModel);
        realm.commitTransaction();
        setComments();
    }

    private class AddCommentOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            addComment();
        }
    }
}
