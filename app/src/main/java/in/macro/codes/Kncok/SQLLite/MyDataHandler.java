package in.macro.codes.Kncok.SQLLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDataHandler extends SQLiteOpenHelper {
    private String userId;
    public MyDataHandler(Context context) {

        super(context, "mymessages.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table"+ userId +"(messagekey TEXT primary key,message TEXT,type TEXT,time TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop Table if exists Messages");
    }

    public Boolean insertuserData(String messagekey,String message,String type,String time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("messagekey",messagekey);
        contentValues.put("message",message);
        contentValues.put("type",type);
        contentValues.put("time",time);
        long result = db.insert(userId,null,contentValues);
        if (result ==-1)
            return false;
        else
            return true;
    }

    public Boolean updateuserData(String messagekey,String message,String type,String time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("messagekey",messagekey);
        contentValues.put("message",message);
        contentValues.put("type",type);
        contentValues.put("time",time);

        Cursor cursor = db.rawQuery("Select * from"+ userId +"where messagekey=?",new String[]{messagekey});
        if (cursor.getCount()>0){

            long result = db.update(userId,contentValues,"messagekey=?",new String[]{"messagekey"});
            if (result ==-1)
                return false;
            else
                return true;
        }else{
            return false;
        }

    }

    public Boolean deleteUserData(String messagekey){
        SQLiteDatabase db = this.getWritableDatabase();


        Cursor cursor = db.rawQuery("Select * from"+ userId +"where messagekey=?",new String[]{messagekey});
        if (cursor.getCount()>0){

            long result = db.delete(userId,"messagekey=?",new String[]{"messagekey"});
            if (result ==-1)
                return false;
            else
                return true;
        }else{
            return false;
        }

    }

    public Cursor getData(String userid){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from"+ userId +"where userid=?",new String[]{userid});
        return cursor;
        
    }
}
