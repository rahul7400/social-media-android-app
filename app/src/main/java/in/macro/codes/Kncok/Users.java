package in.macro.codes.Kncok;



public class Users {

    public String name;
    public String image;
    public String status;
    public String thumb_image;
    public String uid;
    public String latitude;
    public String longitude;
    public String cover_image;
    public String city;
    public String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Users(String cover_image) {
        this.cover_image = cover_image;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Users(){

    }

    public Users(String name, String image, String status, String thumb_image,String uid,String latitude,String longitude,String city,String email) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.thumb_image = thumb_image;
        this.uid=uid;
        this.latitude=latitude;
        this.longitude=longitude;
        this.city =city;
        this.email=email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

}
