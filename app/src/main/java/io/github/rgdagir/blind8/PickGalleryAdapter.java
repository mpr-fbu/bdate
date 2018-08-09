package io.github.rgdagir.blind8;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.parse.ParseImageView;

import java.util.List;

public class PickGalleryAdapter extends RecyclerView.Adapter<PickGalleryAdapter.ViewHolder>{

    private Context context;
    private Fragment mFragment;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ParseImageView imgContainer;
        public FloatingActionButton changeImageBtn;

        public ViewHolder(View itemView){
            super(itemView);
            imgContainer = itemView.findViewById(R.id.imgGallery);
            changeImageBtn = itemView.findViewById(R.id.changeImageBtn);
        }
    }

    public final static int PICK_PHOTO_CODE = 1046;
    private List<String> mCoverImageUrls;

    public PickGalleryAdapter(List<String> imageUrls, Fragment fragment){
        mCoverImageUrls = imageUrls;
        mFragment = fragment;
    }

    @Override
    public void onBindViewHolder(@NonNull final PickGalleryAdapter.ViewHolder holder, int position) {
        String fileUrl = mCoverImageUrls.get(position);

        // Set item views based on our views and data model
        final ParseImageView imgView = holder.imgContainer;
        Glide.with(context).load(fileUrl)
                .asBitmap().centerCrop().dontAnimate()
                .placeholder(R.color.babyWhite)
                .error(R.color.babyWhite)
                .into(new BitmapImageViewTarget(imgView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable roundedBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        roundedBitmapDrawable.setCornerRadius(30);
                        imgView.setImageDrawable(roundedBitmapDrawable);
                    }
                });

        holder.changeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(holder.getAdapterPosition());
            }
        });
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
        return mCoverImageUrls.size();
    }

    public void onPickPhoto(int position) {
        // Create intent for picking a photo from the gallery
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (i.resolveActivity(context.getPackageManager()) != null) {
            // Bring up gallery to select a photo
            mFragment.startActivityForResult(i, PICK_PHOTO_CODE + position + 1);
        }
    }
}
