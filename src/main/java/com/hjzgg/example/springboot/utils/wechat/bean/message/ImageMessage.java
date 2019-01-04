package com.hjzgg.example.springboot.utils.wechat.bean.message;

public class ImageMessage
        extends Message {
    private Image image;

    public ImageMessage(String touser, String mediaId) {
        super(touser, "image");
        this.image = new Image();
        this.image.setMedia_id(mediaId);
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public static class Image {
        private String media_id;

        public String getMedia_id() {
            return this.media_id;
        }

        public void setMedia_id(String mediaId) {
            this.media_id = mediaId;
        }
    }
}
