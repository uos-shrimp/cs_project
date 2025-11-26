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

// ★ 본인 패키지명에 맞게 경로 확인!
import org.techtown.cs_project.adapter.HomeRecyclerAdapter;
import org.techtown.cs_project.model.PostModel;
// import org.techtown.cs_project.adapter.HomeRecyclerAdapter; // 필요시 주석 해제

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private HomeRecyclerAdapter adapter;
    private Spinner searchSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 1. 리사이클러뷰 연결
        recyclerView = view.findViewById(R.id.recycler_view_home);

        // 앱이 죽는 문제 방지를 위한 안전한 LayoutManager 적용
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(null); // 화면 깜빡임 및 잔상 제거에 도움됨

        // 2. 스피너 연결
        searchSpinner = view.findViewById(R.id.search_spinner);

        // 3. 스피너 선택 리스너 (앱 실행 시 자동으로 초기 데이터 로딩됨)
        searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBuilding = parent.getItemAtPosition(position).toString();
                // 선택된 건물로 데이터 검색 시작
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

        // ★ 핵심 수정: 기존 어댑터가 있다면 정지시킴 (메모리 누수 방지 및 충돌 방지)
        if (adapter != null) {
            adapter.stopListening();
        }

        // ★ 핵심 수정: updateOptions 대신 어댑터를 '새로 생성'해서 갈아끼움
        // 이렇게 해야 데이터가 0개일 때 이전 데이터 잔상이 남지 않고 깔끔하게 빈 화면이 됨
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

    // 앱 꺼짐 방지용 안전한 LayoutManager (삭제하지 마세요!)
    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                // 리사이클러뷰 갱신 중 발생하는 에러를 잡아내서 앱 종료 방지
            }
        }
    }
}