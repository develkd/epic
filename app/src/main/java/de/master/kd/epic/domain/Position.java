package de.master.kd.epic.domain;

import android.support.annotation.Nullable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

import java.io.Serializable;
import java.util.Date;
/**
 * Created by pentax on 25.06.17.
 */

@Entity(indexes = {
        @Index(value = "title, date DESC")
})

public class Position implements  Serializable {
    private static final long serialVersionUID = 4988199900863565459L;

    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String title;

    private String description;

    @NotNull
    private Long latitude;

    @NotNull
    private Long longitude;

    private Long refIdMap;

    private Long refIdPicture;

    @NotNull
    private Date createDate;

    private Date updateDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getLatitude() {
        return latitude;
    }

    public void setLatitude(@NotNull Long latitude) {
        this.latitude = latitude;
    }

    public Long getLongitude() {
        return longitude;
    }

    public void setLongitude(@NotNull Long longitude) {
        this.longitude = longitude;
    }

    public Long getRefIdMap() {
        return refIdMap;
    }

    public void setRefIdMap(Long refIdMap) {
        this.refIdMap = refIdMap;
    }

    public Long getRefIdPicture() {
        return refIdPicture;
    }

    public void setRefIdPicture(Long refIdPicture) {
        this.refIdPicture = refIdPicture;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(@NotNull Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (!id.equals(position.id)) return false;
        if (!title.equals(position.title)) return false;
        if (description != null ? !description.equals(position.description) : position.description != null)
            return false;
        if (!latitude.equals(position.latitude)) return false;
        if (!longitude.equals(position.longitude)) return false;
        if (refIdMap != null ? !refIdMap.equals(position.refIdMap) : position.refIdMap != null)
            return false;
        if (refIdPicture != null ? !refIdPicture.equals(position.refIdPicture) : position.refIdPicture != null)
            return false;
        if (!createDate.equals(position.createDate)) return false;
        return updateDate != null ? updateDate.equals(position.updateDate) : position.updateDate == null;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + latitude.hashCode();
        result = 31 * result + longitude.hashCode();
        result = 31 * result + (refIdMap != null ? refIdMap.hashCode() : 0);
        result = 31 * result + (refIdPicture != null ? refIdPicture.hashCode() : 0);
        result = 31 * result + createDate.hashCode();
        result = 31 * result + (updateDate != null ? updateDate.hashCode() : 0);
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
        sb.append(", refIdMap=").append(refIdMap);
        sb.append(", refIdPicture=").append(refIdPicture);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append('}');
        return sb.toString();
    }
}
