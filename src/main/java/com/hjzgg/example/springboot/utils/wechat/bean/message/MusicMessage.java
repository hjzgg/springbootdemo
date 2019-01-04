package com.hjzgg.example.springboot.utils.wechat.bean.message;

public class MusicMessage
        extends Message {
    private Music music;

    public MusicMessage(String touser, Music music) {
        super(touser, "music");
        this.music = music;
    }

    public Music getMusic() {
        return this.music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public static class Music {
        private String title;
        private String description;
        private String musicurl;
        private String hqmusicurl;
        private String thumb_media_id;

        public Music(String title, String description, String musicurl, String hqmusicurl, String thumb_media_id) {
            this.title = title;
            this.description = description;
            this.musicurl = musicurl;
            this.hqmusicurl = hqmusicurl;
            this.thumb_media_id = thumb_media_id;
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

        public String getMusicurl() {
            return this.musicurl;
        }

        public void setMusicurl(String musicurl) {
            this.musicurl = musicurl;
        }

        public String getHqmusicurl() {
            return this.hqmusicurl;
        }

        public void setHqmusicurl(String hqmusicurl) {
            this.hqmusicurl = hqmusicurl;
        }

        public String getThumb_media_id() {
            return this.thumb_media_id;
        }

        public void setThumb_media_id(String thumbMediaId) {
            this.thumb_media_id = thumbMediaId;
        }
    }
}
