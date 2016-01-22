package com.applift.mobile.dashboard.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.applift.mobile.dashboard.R;

import Adapters.DashboardAdapter;
import Models.ProjectModel;
import Utils.Constants;
import Utils.IdUtils;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class DashBoardActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private ListView dashboardListview;
    private AlertDialog alert;
    private Realm realm;
    private DashboardAdapter dashboardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        dashboardListview = (ListView) findViewById(R.id.dashboard_listview);
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
        dashboardAdapter = new DashboardAdapter();
        dashboardListview.setAdapter(dashboardAdapter);
        dashboardListview.setOnItemClickListener(this);
        getProjects();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dash_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private  void makeNewProjectDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = (View) inflater.inflate(R.layout.create_project_layout,
                null);
        Button okButton  = (Button) view.findViewById(R.id.create_project_ok_button);
        Button cancelButton = (Button) view. findViewById(R.id.create_project_cancel_button);
        final EditText nameET = (EditText) view.findViewById(R.id.create_project_name_et);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameET.getText().toString().trim().length() > 0){
                    createProject(nameET.getText().toString().trim());
                    alert.dismiss();
                }else{
                    Toast.makeText(DashBoardActivity.this, getResources().getString(R.string.project_name_not_chosen),
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

    private void createProject(String name){
        // Server request could be performed here
        ProjectModel project  = new ProjectModel();
        project.setTitle(name);
        project.setId(IdUtils.getRandomMessageId(Constants.PROJECT));
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(project);
        realm.commitTransaction();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ProjectModel projectModel = (ProjectModel) dashboardAdapter.getItem(position);
        Intent intent = new Intent(DashBoardActivity.this, ProjectActivity.class);
        intent.putExtra("projectId", projectModel.getId());
        startActivity(intent);
    }

    private void getProjects(){
        // Server request could be performed here
        RealmResults<ProjectModel> projects =
                realm.where(ProjectModel.class).findAllSorted("title", Sort.ASCENDING);
        dashboardAdapter.setProjects(projects);
    }

    private RealmChangeListener realmCallback = new RealmChangeListener() {
        @Override
        public void onChange() {
            getProjects();
        }

    };
}
