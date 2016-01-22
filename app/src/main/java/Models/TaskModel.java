package Models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by nami on 1/22/16.
 */
public class TaskModel extends RealmObject {
    @PrimaryKey
    private String id;

    private String projectId;
    private Date creationDate;
    private String title;
    private String description;
    private boolean isInReview;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isInReview() {
        return isInReview;
    }

    public void setIsInReview(boolean isInReview) {
        this.isInReview = isInReview;
    }
}
