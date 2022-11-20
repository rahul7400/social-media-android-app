package in.macro.codes.Kncok.Story;

public class Comments {
    public String comment;
    public String userId;
    public long time;

    public Comments(String comment, String userId,long time) {
        this.comment = comment;
        this.userId = userId;
        this.time =time;

    }

    public Comments(){
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
