package in.macro.codes.Kncok.Groups;

public class Groups {
    public String gname;
    public String gimage;
    public String gthumb_image;
    public String gstatus;
    public String owner;
    public String gcover_image;
    public String gid;
    public String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGrp_mode() {
        return grp_mode;
    }

    public void setGrp_mode(String grp_mode) {
        this.grp_mode = grp_mode;
    }

    public String grp_mode;

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }

    public String getGimage() {
        return gimage;
    }

    public void setGimage(String gimage) {
        this.gimage = gimage;
    }

    public String getGthumb_image() {
        return gthumb_image;
    }

    public void setGthumb_image(String gthumb_image) {
        this.gthumb_image = gthumb_image;
    }

    public String getGstatus() {
        return gstatus;
    }

    public void setGstatus(String gstatus) {
        this.gstatus = gstatus;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getGcover_image() {
        return gcover_image;
    }

    public void setGcover_image(String gcover_image) {
        this.gcover_image = gcover_image;
    }

    public Groups(String gname, String gimage, String gthumb_image, String gstatus, String owner, String gcover_image,String gid,String grp_mode,String category) {
        this.gname = gname;
        this.gimage = gimage;
        this.gthumb_image = gthumb_image;
        this.gstatus = gstatus;
        this.owner = owner;
        this.gcover_image = gcover_image;
        this.gid=gid;
        this.grp_mode=grp_mode;
        this.category=category;
    }

    public Groups(){

    }
}
