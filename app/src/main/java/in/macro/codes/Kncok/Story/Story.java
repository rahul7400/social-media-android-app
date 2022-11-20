package in.macro.codes.Kncok.Story;

import java.io.Serializable;

public class Story implements Serializable {

    private String imageurl;
    private long timestart;
    private long timend;
    private String storyId;
    private String caption;
    private String userId;
    private String type;
    private Float size;
    private Float x;
    private String background;

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    private Float y;

    public Float getSize() {
        return size;
    }

    public void setSize(Float size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Story(String imageurl, long timestart, long timend, String storyId, String userId ,String caption,String type,Float size,Float x,Float y,String background) {
        this.imageurl = imageurl;
        this.timestart = timestart;
        this.timend = timend;
        this.storyId = storyId;
        this.caption=caption;
        this.userId = userId;
        this.background =background;
        this.type=type;
        this.size=size;
        this.x=x;
        this.y=y;
    }

    public Story(){

    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public long getTimestart() {
        return timestart;
    }

    public void setTimestart(long timestart) {
        this.timestart = timestart;
    }

    public long getTimend() {
        return timend;
    }

    public void setTimend(long timend) {
        this.timend = timend;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
