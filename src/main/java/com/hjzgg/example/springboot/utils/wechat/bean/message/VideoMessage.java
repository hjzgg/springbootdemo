package com.hjzgg.example.springboot.utils.wechat.bean.message;

public class VideoMessage
        extends Message {
    public Video video;

    public VideoMessage(String touser, Video video) {
        super(touser, "video");
        this.video = video;
    }

    public Video getVideo() {
        return this.video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public static class Video {
        private String media_id;
        private String title;
        private String description;

        public Video(String media_id, String title, String description) {
            this.media_id = media_id;
            this.title = title;
            this.description = description;
        }

        public String getMedia_id() {
            return this.media_id;
        }

        public void setMedia_id(String mediaId) {
            this.media_id = mediaId;
        }

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
