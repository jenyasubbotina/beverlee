package uz.alex.its.beverlee.model.news;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsModel {
    @Expose
    @SerializedName("draw")
    private final long draw;

    @Expose
    @SerializedName("recordsTotal")
    private final int recordsTotal;

    @Expose
    @SerializedName("recordsFiltered")
    private final int recordsFiltered;

    @Expose
    @SerializedName("data")
    private final List<News> newsList;

    public NewsModel(final long draw, final int recordsTotal, final int recordsFiltered, final List<News> newsList) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.newsList = newsList;
    }

    public long getDraw() {
        return draw;
    }

    public int getRecordsTotal() {
        return recordsTotal;
    }

    public int getRecordsFiltered() {
        return recordsFiltered;
    }

    public List<News> getNewsList() {
        return newsList;
    }

    @NonNull
    @Override
    public String toString() {
        return "NewsResponse{" +
                "draw=" + draw +
                ", recordsTotal=" + recordsTotal +
                ", recordsFiltered=" + recordsFiltered +
                ", newsList=" + newsList +
                '}';
    }

    public static class News {
        @Expose
        @SerializedName("id")
        private final long id;

        @Expose
        @SerializedName("title")
        private final String title;

        @Expose
        @SerializedName("description")
        private final String description;

        @Expose
        @SerializedName("photo_url")
        private final String photoUrl;

        @Expose
        @SerializedName("created_at")
        private final long createdAt;

        public News(final long id, final String title, final String description, final String photoUrl, final long createdAt) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.photoUrl = photoUrl;
            this.createdAt = createdAt;
        }

        public long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        @NonNull
        @Override
        public String toString() {
            return "News{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", photoUrl='" + photoUrl + '\'' +
                    ", createdAt=" + createdAt +
                    '}';
        }
    }
}
