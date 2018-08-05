package io.github.rgdagir.blind8;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.parse.ParseFile;

import java.util.List;

public class PickGalleryAdapter extends RecyclerView.Adapter<PickGalleryAdapter.ViewHolder>{

    Context context;


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgContainer;
        public FloatingActionButton deleteBtn;



        public ViewHolder(View itemView){
            super(itemView);
            imgContainer = itemView.findViewById(R.id.imgGallery);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }

    private List<ParseFile> mCoverImages;

    public PickGalleryAdapter(List<ParseFile> images){
        mCoverImages = images;
    }

    @Override
    public void onBindViewHolder(@NonNull PickGalleryAdapter.ViewHolder holder, int position) {
        ParseFile file = mCoverImages.get(position);

        // Set item views based on our views and data model
        ImageView imgView = holder.imgContainer;
        Glide.with(context).load(file.getUrl())
                .asBitmap().centerCrop().dontAnimate()
                .placeholder(R.drawable.ic_action_name)
                .error(R.drawable.ic_action_name)
                .into(new BitmapImageViewTarget(imgView));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View imageView = inflater.inflate(R.layout.item_choose_gallery, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(imageView);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mCoverImages.size();
    }
}
