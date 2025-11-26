package org.techtown.cs_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import org.techtown.cs_project.R;
import org.techtown.cs_project.model.PostModel;

public class HomeRecyclerAdapter extends FirestoreRecyclerAdapter<PostModel, HomeRecyclerAdapter.HomeViewHolder> {

    public HomeRecyclerAdapter(@NonNull FirestoreRecyclerOptions<PostModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull HomeViewHolder holder, int position, @NonNull PostModel model) {
        holder.titleTv.setText(model.getTitle());
        String infoText = "수강 장소 : " + model.getBuilding() + ", 가격 : " + model.getPrice() + "원";
        holder.placeTv.setText(infoText);
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.building_recycler_row, parent, false);
        return new HomeViewHolder(view);
    }

    class HomeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTv;
        TextView placeTv;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.sell_title);
            placeTv = itemView.findViewById(R.id.study_place);
        }
    }
}