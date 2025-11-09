package org.techtown.cs_project;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

public class SearchUserActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput = findViewById(R.id.search_username_input);
        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        searchInput.requestFocus();                                 //액티비티가 실행되었을 때 searchInput으로 포커스 맞추기

        backButton.setOnClickListener(v -> {                //뒤로 가기 버튼 눌렀을 때
            onBackPressed();
        });

        searchButton.setOnClickListener(v -> {
            String searchTerm = searchInput.getText().toString();
            if(searchTerm.isEmpty() || searchTerm.length() < 3) {
                searchInput.setError("Invalid Username");
                return;
            }
            setupSearchRecyclerView(searchTerm);
        });

    }

    void  setupSearchRecyclerView(String searchTerm) {

    }
}