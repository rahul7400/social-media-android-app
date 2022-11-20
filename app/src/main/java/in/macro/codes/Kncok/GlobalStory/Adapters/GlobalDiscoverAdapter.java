package in.macro.codes.Kncok.GlobalStory.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import in.macro.codes.Kncok.R;
import in.macro.codes.Kncok.Story.Story;

public class GlobalDiscoverAdapter extends RecyclerView.Adapter<GlobalDiscoverAdapter.Viewholder> {
    private List<String> AllHashTags = new ArrayList<>();
    private List<Story> mGlobalStory = new ArrayList<>();
    private Context mContext;
    public GlobalDiscoverAdapter(Context context, List<String> allHashTags, List<Story> mGlobalStory) {

        this.AllHashTags=allHashTags;
        this.mContext = context;
        this.mGlobalStory = mGlobalStory;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_post_layout,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        Story story = mGlobalStory.get(position);
        Glide.with(mContext)
                .load(story.getImageurl())
                .format(DecodeFormat.PREFER_RGB_565)
                .thumbnail(Glide.with(mContext).load(story.getImageurl()))
                .into(holder.postPreview);


    }

    @Override
    public int getItemCount() {
        return mGlobalStory.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{

        private ImageView postPreview;
        private TextView like_count;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            postPreview = (ImageView) itemView.findViewById(R.id.postPreview);
            like_count = (TextView) itemView.findViewById(R.id.like_count);
        }
    }
}
