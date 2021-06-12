package uz.alex.its.beverlee.model.news;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import uz.alex.its.beverlee.model.Link;

public class NewsDataModel {
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
    private final List<NewsData> newsData;

    public NewsDataModel(final long draw, final int recordsTotal, final int recordsFiltered, final List<NewsData> newsData) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.newsData = newsData;
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

    public List<NewsData> getNewsData() {
        return newsData;
    }

    @NonNull
    @Override
    public String toString() {
        return "NewsResponse{" +
                "draw=" + draw +
                ", recordsTotal=" + recordsTotal +
                ", recordsFiltered=" + recordsFiltered +
                ", newsData=" + newsData +
                '}';
    }

    public static class NewsData {
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
        @SerializedName("content")
        private final String content;

        @Expose
        @SerializedName("photo_url")
        private final String photoUrl;

        @Expose
        @SerializedName("created_at")
        private final long createdAt;

        @Expose
        @SerializedName("links")
        private final List<Link> linkList;

        public NewsData(final long id,
                        final String title,
                        final String description,
                        final String content,
                        final String photoUrl,
                        final long createdAt,
                        final List<Link> linkList) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.content = content;
            this.photoUrl = photoUrl;
            this.createdAt = createdAt;
            this.linkList = linkList;
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

        public String getContent() {
            return content;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        public List<Link> getLinkList() {
            return linkList;
        }

        @NonNull
        @Override
        public String toString() {
            return "NewsData{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", content='" + content + '\'' +
                    ", photoUrl='" + photoUrl + '\'' +
                    ", createdAt=" + createdAt +
                    ", linkList=" + linkList +
                    '}';
        }
    }
}
