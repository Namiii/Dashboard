package Models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by nami on 1/22/16.
 */
public class ProjectModel extends RealmObject {
    @PrimaryKey
    private String id;

    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
