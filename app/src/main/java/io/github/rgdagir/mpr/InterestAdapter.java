package io.github.rgdagir.mpr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.github.rgdagir.mpr.models.Interest;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.ViewHolder> {
    List<Interest> mInterests;
    Context context;

    public InterestAdapter(List<Interest> interests) {
        mInterests = interests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View interestView = inflater.inflate(R.layout.item_interest_general, parent, false);
        InterestAdapter.ViewHolder viewHolder = new InterestAdapter.ViewHolder(interestView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull InterestAdapter.ViewHolder holder, int position) {
        // get data according to position
        final Interest post = mInterests.get(position);
        holder.tvInterest.setText(post.getName());
    }

    @Override
    public int getItemCount() { return mInterests.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvInterest;

        public ViewHolder(final View itemView) {
            super(itemView);

            tvInterest = itemView.findViewById(R.id.tvInterestGeneral);
        }
    }
}
