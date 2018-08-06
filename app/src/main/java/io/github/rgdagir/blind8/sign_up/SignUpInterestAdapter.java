package io.github.rgdagir.blind8.sign_up;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.HashMap;
import java.util.List;

import io.github.rgdagir.blind8.R;
import io.github.rgdagir.blind8.models.Interest;
import io.github.rgdagir.mpr.R;

public class SignUpInterestAdapter extends RecyclerView.Adapter<SignUpInterestAdapter.ViewHolder> {

    List<Interest> mInterests;
    Context context;
    HashMap<Interest, Boolean> checked = new HashMap();

    public SignUpInterestAdapter(List<Interest> interests) {
        mInterests = interests;
    }

    @NonNull
    @Override
    public SignUpInterestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View conversationView = inflater.inflate(R.layout.item_signup_interest, parent, false);
        SignUpInterestAdapter.ViewHolder viewHolder = new SignUpInterestAdapter.ViewHolder(conversationView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SignUpInterestAdapter.ViewHolder holder, int position) {
        final Interest interest = mInterests.get(position);
        holder.interestCheckBox.setText(interest.getName());
        holder.interestCheckBox.setChecked(mInterests.get(position).getSelected());
        holder.interestCheckBox.setTag(position);
        holder.interestCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer pos = (Integer) holder.interestCheckBox.getTag();
                if (mInterests.get(pos).getSelected()) {
                    mInterests.get(pos).setSelected(false);
                    checked.put(interest, false);
                } else {
                    mInterests.get(pos).setSelected(true);
                        checked.put(interest, true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mInterests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CheckBox interestCheckBox;

        public ViewHolder(final View itemView) {
            super(itemView);
            interestCheckBox = itemView.findViewById(R.id.interestCheckBox);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }
    }
}
