package io.github.rgdagir.blind8;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.github.rgdagir.blind8.models.Interest;

public class InterestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final int VIEW_TYPE_GENERAL = 0;
    final int VIEW_TYPE_COMMON = 1;

    List<Interest> mInterests;
    List<Boolean> mIsInCommon;
    Context context;

    public InterestAdapter(List<Interest> interests, List<Boolean> isInCommon) {
        mInterests = interests;
        mIsInCommon = isInCommon;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == VIEW_TYPE_GENERAL) {
            View interestView = inflater.inflate(R.layout.item_interest_general, parent, false);
            ViewHolderGeneral viewHolder = new InterestAdapter.ViewHolderGeneral(interestView);
            return viewHolder;
        }
        if (viewType == VIEW_TYPE_COMMON) {
            View interestView = inflater.inflate(R.layout.item_interest_common, parent, false);
            ViewHolderCommon viewHolder = new InterestAdapter.ViewHolderCommon(interestView);
            return viewHolder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // get data according to position and display correct tag
        final Interest interest = mInterests.get(position);
        if(holder instanceof ViewHolderGeneral){
            ((ViewHolderGeneral) holder).tvInterest.setText(interest.getName());
        } else {
            ((ViewHolderCommon) holder).tvInterest.setText(interest.getName());
        }
    }

    @Override
    public int getItemCount() { return mInterests.size(); }

    @Override
    public int getItemViewType(int position) {
        if (mIsInCommon.get(position)) {
            return VIEW_TYPE_COMMON;
        } else {
            return VIEW_TYPE_GENERAL;
        }
    }

    public class ViewHolderGeneral extends RecyclerView.ViewHolder {
        public TextView tvInterest;

        public ViewHolderGeneral(final View itemView) {
            super(itemView);
            tvInterest = itemView.findViewById(R.id.tvInterestGeneral);
        }
    }

    public class ViewHolderCommon extends RecyclerView.ViewHolder {
        public TextView tvInterest;

        public ViewHolderCommon(final View itemView) {
            super(itemView);
            tvInterest = itemView.findViewById(R.id.tvInterestCommon);
        }
    }
}
