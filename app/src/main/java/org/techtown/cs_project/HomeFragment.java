package org.techtown.cs_project;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.techtown.cs_project.adapter.HomeRecyclerAdapter;
import org.techtown.cs_project.model.PostModel;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private HomeRecyclerAdapter adapter;
    private Spinner searchSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_home);

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(null);

        searchSpinner = view.findViewById(R.id.search_spinner);

        searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBuilding = parent.getItemAtPosition(position).toString();
                searchData(selectedBuilding);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                searchData("전체");
            }
        });

        return view;
    }

    private void searchData(String buildingName) {
        Query query;

        // "post" 컬렉션 사용
        if (buildingName.equals("전체") || buildingName.isEmpty()) {
            query = FirebaseFirestore.getInstance()
                    .collection("post")
                    .orderBy("timestamp", Query.Direction.DESCENDING);
        } else {
            query = FirebaseFirestore.getInstance()
                    .collection("post")
                    .whereEqualTo("building", buildingName)
                    .orderBy("timestamp", Query.Direction.DESCENDING);
        }

        FirestoreRecyclerOptions<PostModel> options = new FirestoreRecyclerOptions.Builder<PostModel>()
                .setQuery(query, PostModel.class)
                .build();


        if (adapter != null) {
            adapter.stopListening();
        }


        adapter = new HomeRecyclerAdapter(options);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }


    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {

            }
        }
    }
}