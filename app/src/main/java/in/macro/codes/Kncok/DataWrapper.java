package in.macro.codes.Kncok;

import java.io.Serializable;
import java.util.ArrayList;

public class DataWrapper implements Serializable {

    private ArrayList<Users> usersArrayList;

    public DataWrapper(ArrayList<Users> data) {
        this.usersArrayList = data;
    }

    public ArrayList<Users> getParliaments() {
        return this.usersArrayList;
    }

}