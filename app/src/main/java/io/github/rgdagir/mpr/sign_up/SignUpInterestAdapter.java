package io.github.rgdagir.mpr.sign_up;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.List;

import io.github.rgdagir.mpr.R;
import io.github.rgdagir.mpr.models.Interest;

public class SignUpInterestAdapter extends RecyclerView.Adapter<SignUpInterestAdapter.ViewHolder> {

    List<Interest> mInterests;
    Context context;
    private SparseBooleanArray itemStateArray= new SparseBooleanArray();

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
    public void onBindViewHolder(@NonNull SignUpInterestAdapter.ViewHolder holder, int position) {
        holder.bind(position);
        final Interest interest = mInterests.get(position);
        holder.interestCheckBox.setText(interest.getName());

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
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                int adapterPosition = getAdapterPosition();
                if (!itemStateArray.get(adapterPosition, false)) {
                    interestCheckBox.setChecked(true);
                    itemStateArray.put(adapterPosition, true);
                }
                else  {
                    interestCheckBox.setChecked(false);
                    itemStateArray.put(adapterPosition, false);
                }
                Interest interest = mInterests.get(pos);
            }
        }

        void bind(int position) {
            // use the sparse boolean array to check
            if (!itemStateArray.get(position, false)) {
                interestCheckBox.setChecked(false);}
            else {
                interestCheckBox.setChecked(true);
            }
        }
    }
}
