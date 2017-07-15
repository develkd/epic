package de.master.kd.epic.domain.position;

import android.databinding.BaseObservable;
import android.provider.BaseColumns;


import java.io.Serializable;
import java.util.Date;

/**
 * Created by pentax on 25.06.17.
 */
public class Position extends BaseObservable implements BaseColumns, Serializable {
    private static final long serialVersionUID = 4988199900863565459L;


    private Long id;
    private String title;
    private String description;
    private Double latitude;
    private Double longitude;
    private String pathMap;
    private String pathPicture;
    private Date createDate;
    private Date updateDate;
    private Long markerId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPathMap() {
        return pathMap;
    }

    public void setPathMap(String pathMap) {
        this.pathMap = pathMap;
    }

    public String getPathPicture() {
        return pathPicture;
    }

    public void setPathPicture(String pathPicture) {
        this.pathPicture = pathPicture;
    }

    public Date getCreateDate() {
        return createDate;
    }


    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Long getMarkerId() {
        return markerId;
    }

    public void setMarkerId(Long markerId) {
        this.markerId = markerId;
    }

    @javax.annotation.PostConstruct
    private void setCreationTime() {
        setCreateDate(new Date());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (!id.equals(position.id)) return false;
        if (!title.equals(position.title)) return false;
        if (!latitude.equals(position.latitude)) return false;
        if (!longitude.equals(position.longitude)) return false;
        return createDate.equals(position.createDate);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + latitude.hashCode();
        result = 31 * result + longitude.hashCode();
        result = 31 * result + createDate.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Position{");
        sb.append("id=").append(id);
        sb.append(", title='").append(title).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append(", pathMap='").append(pathMap).append('\'');
        sb.append(", pathPicture='").append(pathPicture).append('\'');
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", markerId=").append(markerId);
        sb.append('}');
        return sb.toString();
    }
}
