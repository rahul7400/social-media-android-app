package in.macro.codes.Kncok.Gallery;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import in.macro.codes.Kncok.R;


public class GlideImageLoader {

    public static void loadImageWithTransition(Context mContext, String imageUrl, ImageView image, final ProgressBar progressBar) {
        Glide.with(mContext)
                .load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(new RequestOptions()
                .placeholder(R.drawable.default_send_image))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if(progressBar!=null)
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if(progressBar!=null)
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(image);
    }


    public static void loadImageWithOutTransition(Context mContext, String imageUrl, ImageView image) {
        Glide.with(mContext)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.default_send_image))
                .into(image);
    }


}
