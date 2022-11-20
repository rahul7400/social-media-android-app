package in.macro.codes.Kncok.DiscoverElements;

public class Post {
    String name;
    String message;
    String type;
    String caption;
    String from;
    String push_id;

    public String getPush_id() {
        return push_id;
    }

    public void setPush_id(String push_id) {
        this.push_id = push_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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



    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Post(String name, String message, String type, String caption, String from,String push_id) {
        this.name = name;
        this.push_id=push_id;
        this.message = message;
        this.type = type;
        this.caption = caption;
        this.from = from;
    }

    public Post(){

    }

}
