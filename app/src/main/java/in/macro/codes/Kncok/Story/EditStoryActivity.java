package in.macro.codes.Kncok.Story;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;

import in.macro.codes.Kncok.PathUtilvideo;
import in.macro.codes.Kncok.R;
import in.macro.codes.Kncok.RotationGestureDetector;

public class EditStoryActivity extends AppCompatActivity implements View.OnTouchListener{

    final static float move =200;
    float ratio = 1.0f;
    int baseDist;
    float baseRatio;

    PointF mid = new PointF();
    static final int NONE = 0;
    static final int ZOOM = 1;
    int mode = NONE;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    static final int MIN_FONT_SIZE = 10;
    static final int MAX_FONT_SIZE = 50;

    FrameLayout test_img_layout;
    float oldDist = 1f;


    final static float STEP = 200;

    float mRatio = 1.0f;
    int mBaseDist;
    float mBaseRatio;
    float fontsize = 13;


    float scale;
    RelativeLayout layout, edit_layout,attach;
    EditText Edit_text;
    float dX;
    float dY;
    int lastAction;
    String path,type;
    Uri mImageUri;
    ImageView imageView;
    EditText edttext;
    int counter = 0;
    Button next;
    Float x,y;
    VideoView videoView;

    private  RotationGestureDetector mRotation;
    private ScaleGestureDetector mScaleGestureDetector;

    private float mScaleFactor = 1.0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_story);
        layout = findViewById(R.id.layout_story);
        attach = findViewById(R.id.attach);
        imageView = findViewById(R.id.testImage);
        videoView = (VideoView) findViewById(R.id.VideoView);

        next = findViewById(R.id.nxt_story);
       // imageView.setOnTouchListener(this);
        videoView.setOnTouchListener(this);
        edttext = new EditText(EditStoryActivity.this);
        test_img_layout = (FrameLayout) findViewById(R.id.test_img_layout);
        test_img_layout.setOnTouchListener(this);
        edit_layout = findViewById(R.id.edit_layout);
        Edit_text = findViewById(R.id.edit_text);

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        mImageUri = Uri.parse(getIntent().getStringExtra("uri"));
        path = getIntent().getStringExtra("path");
        type = getIntent().getStringExtra("type");

        assert type != null;
        if (type.equals("image")){
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            Glide.with(getApplicationContext()).load(path).into(imageView);
        }else {
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            videoView.setVideoURI(mImageUri);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                    videoView.start();
                }
            });
        }








        edit_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = Edit_text.getText().toString();
                hideKeyboard(EditStoryActivity.this);
                edit_layout.setVisibility(View.GONE);



                if (!text.equals("")){
                    TextView textView = new TextView(EditStoryActivity.this);
                    textView.setText(text);
                    textView.setTextColor(Color.parseColor("#ffffff"));
                    textView.setTextSize(ratio+30);
                    Typeface face= Typeface.defaultFromStyle(Typeface.BOLD);
                    textView.setTypeface(face);
                    textView.setTextAppearance(EditStoryActivity.this, R.style.fontForNotificationLandingPage);
                    layout.addView(textView);



                    textView.setOnTouchListener(new View.OnTouchListener() {
                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public boolean onTouch(View view, MotionEvent event) {




                            if (event.getPointerCount()==2){
                                int action = event.getAction();
                                int mainaction = action&MotionEvent.ACTION_MASK;
                                if (mainaction == MotionEvent.ACTION_POINTER_DOWN){
                                    baseDist = getDistance(event);
                                    baseRatio = ratio;
                                }else {
                                    float scale=  (getDistance(event) - baseDist)/move;
                                    float factor = (float) Math.pow(2,scale);
                                    ratio = Math.min(1024.0f,Math.max(0.1f,baseRatio*factor));
                                    textView.setTextSize(ratio+30);

                                }
                            }

                            switch (event.getActionMasked()) {
                                case MotionEvent.ACTION_DOWN:
                                    dX = view.getX() - event.getRawX();
                                    dY = view.getY() - event.getRawY();
                                    lastAction = MotionEvent.ACTION_DOWN;
                                    break;

                                case MotionEvent.ACTION_MOVE:

                                    view.setY(event.getRawY() + dY);
                                    view.setX(event.getRawX() + dX);
                                    lastAction = MotionEvent.ACTION_MOVE;


                                    break;

                                case MotionEvent.ACTION_UP:
                                    if (lastAction == MotionEvent.ACTION_DOWN) {
                                        edit_layout.setVisibility(View.VISIBLE);
                                        attach.removeView(view);
                                        Edit_text.setText(text);
                                    }


                                    break;


                                default:
                                    return false;
                            }
                            return true;
                        }
                    });



                }





            }
        });





        if (type.equals("image")){
            Palette.from(BitmapFactory.decodeFile(path)).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    layout.setBackground(getGradientDrawable(getTopColor(palette), getCenterLightColor(palette), getBottomDarkColor(palette)));
                }
            });
        }
        else {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getApplicationContext().getContentResolver().query(mImageUri, filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(picturePath, MediaStore.Video.Thumbnails.MICRO_KIND);

            assert bitmap != null;
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    layout.setBackground(getGradientDrawable(getTopColor(palette), getCenterLightColor(palette), getBottomDarkColor(palette)));
                }
            });
        }


        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Edit_text.setText("");
                edit_layout.setVisibility(View.VISIBLE);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (type.equals("image")) {
                    Bitmap bm = screenShot(layout);
                    String path2 = null;
                    Uri imageUri = getImageUri(EditStoryActivity.this, bm);
                    try {
                        path2 =PathUtilvideo.getPath(EditStoryActivity.this,imageUri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }


                    Intent intent = new Intent(EditStoryActivity.this, AddStoryFinal.class);
                    intent.putExtra("uri", imageUri.toString());
                    intent.putExtra("type", type);
                    Bundle b = new Bundle();
                    b.putFloat("size", mScaleFactor);
                    intent.putExtras(b);
                    intent.putExtra("path",path2);


                    startActivity(intent);
                    layout.removeAllViews();
                    finish();
                }else{
                    if (x == null )
                    {
                        x=0f;
                    }
                    if (y ==null){
                        y = 0f;
                    }
                    Bitmap bm = screenShot(layout);
                    Uri imageUri = getImageUri(EditStoryActivity.this, bm);
                    String path2=null;
                    try {
                        path2 =PathUtilvideo.getPath(EditStoryActivity.this,imageUri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    Bitmap bm2 = screenShot(attach);
                    Uri imageUri2 = getImageUri(EditStoryActivity.this, bm);
                    String path22=null;
                    try {
                        path22 =PathUtilvideo.getPath(EditStoryActivity.this,imageUri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(EditStoryActivity.this, AddStoryFinal.class);
                    intent.putExtra("uri", imageUri.toString());
                    intent.putExtra("videopath",path );
                    intent.putExtra("video", mImageUri.toString());
                    intent.putExtra("path", path2);
                    intent.putExtra("path2", path22);
                    intent.putExtra("type", type);
                    Bundle b = new Bundle();
                    b.putFloat("size", mScaleFactor);
                    b.putFloat("x",x);
                    b.putFloat("y",y);
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }

    int getDistance(MotionEvent event) {
        int dx = (int)(event.getX(0) - event.getX(1));
        int dy = (int)(event.getY(0) - event.getY(1));
        return (int)(Math.sqrt(dx * dx + dy * dy));
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);


        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:

                     y = event.getRawY() + dY;
                     x = event.getRawX() + dX;

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                float height = displayMetrics.heightPixels - y;
                float width = displayMetrics.widthPixels - x;

                    view.setY(y);
                    view.setX(x);
                    lastAction = MotionEvent.ACTION_MOVE;


                break;

            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_DOWN)
                    edit_layout.setVisibility(View.VISIBLE);
                     Edit_text.setText("");
                    break;


            default:
                return false;
        }
        return true;
    }



    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
    @Override
    public void onBackPressed() {

        if (edit_layout.getVisibility() == View.VISIBLE) {
            edit_layout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }




    private GradientDrawable getGradientDrawable(int topColor, int centerColor, int bottomColor) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColors(new int[]{
                topColor,
                centerColor,
                bottomColor
        });
        return gradientDrawable;
    }

    /**
     * @param palette generated palette from image
     * @return return top color for gradient either muted or vibrant whatever is available
     */
    private int getTopColor(Palette palette) {
        if (palette.getMutedSwatch() != null || palette.getVibrantSwatch() != null)
            return palette.getMutedSwatch() != null ? palette.getMutedSwatch().getRgb() : palette.getVibrantSwatch().getRgb();
        else return Color.RED;
    }

    /**
     * @param palette generated palette from image
     * @return return center light color for gradient either muted or vibrant whatever is available
     */
    private int getCenterLightColor(Palette palette) {
        if (palette.getLightMutedSwatch() != null || palette.getLightVibrantSwatch() != null)
            return palette.getLightMutedSwatch() != null ? palette.getLightMutedSwatch().getRgb() : palette.getLightVibrantSwatch().getRgb();
        else return Color.GREEN;
    }

    /**
     * @param palette generated palette from image
     * @return return bottom dark color for gradient either muted or vibrant whatever is available
     */
    private int getBottomDarkColor(Palette palette) {
        if (palette.getDarkMutedSwatch() != null || palette.getDarkVibrantSwatch() != null)
            return palette.getDarkMutedSwatch() != null ? palette.getDarkMutedSwatch().getRgb() : palette.getDarkVibrantSwatch().getRgb();
        else return Color.BLUE;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f));


            if (type.equals("image")) {
                test_img_layout.setScaleX(mScaleFactor);
                test_img_layout.setScaleY(mScaleFactor);
            }else {
                videoView.setScaleX(mScaleFactor);
                videoView.setScaleY(mScaleFactor);
            }

            return true;
        }


    }
    public static void hideKeyboard(AppCompatActivity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        layout.removeAllViews();
    }
}


