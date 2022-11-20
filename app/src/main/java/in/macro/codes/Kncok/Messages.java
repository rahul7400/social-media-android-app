package in.macro.codes.Kncok;


public class Messages {

    private String message, type;
    private long  time;
    private boolean seen;
    private String name;
    private String from;
    private String to;
    private String uri;
    private String caption;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    private String filepath;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Messages(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Messages(String message, String type, long time, boolean seen,String name,String filepath,String to,String uri,String caption) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
        this.name=name;
        this.filepath=filepath;
        this.to=to;
        this.uri=uri;
        this.caption = caption;

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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Messages(){

    }

}
