package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.applift.mobile.dashboard.R;

import java.util.ArrayList;

import Models.TaskModel;
import io.realm.RealmResults;

/**
 * Created by nami on 1/22/16.
 */
public class TasksAdapter extends BaseAdapter {
    private ArrayList<TaskModel> tasks = new ArrayList<>();
    private ViewHolder holder;

    public TasksAdapter() {
    }
    static class ViewHolder{
        TextView title;
    }
    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {

        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        holder = new ViewHolder();
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.
                    inflate(R.layout.list_row_layout, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.list_row_title);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(tasks.get(position).getTitle());
        return convertView;
    }

    public void setTasks(RealmResults<TaskModel> tasks){
        if(this.tasks.size() > 0)
            this.tasks.clear();

        this.tasks.addAll(tasks);
        notifyDataSetChanged();
    }
}
