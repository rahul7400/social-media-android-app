package in.macro.codes.Kncok.NotificationFragment;

public class RequestModel {
    public RequestModel(){

    }
    String type,follower;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public RequestModel(String type, String follower) {
        this.type = type;
        this.follower = follower;
    }
}
