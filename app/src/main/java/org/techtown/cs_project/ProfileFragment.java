package org.techtown.cs_project;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.techtown.cs_project.model.PostModel;


public class ProfileFragment extends Fragment {

    EditText titleEt, bookTitleEt, priceEt, contentEt;
    Spinner buildingSpinner;
    Button uploadBtn;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        titleEt = view.findViewById(R.id.new_title);
        bookTitleEt = view.findViewById(R.id.book_title);
        buildingSpinner = view.findViewById(R.id.buildingSpinner);
        priceEt = view.findViewById(R.id.price);
        contentEt = view.findViewById(R.id.main_text);
        uploadBtn = view.findViewById(R.id.upload);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });

        return view;
    }


    void uploadData() {
        String title = titleEt.getText().toString();
        String bookTitle = bookTitleEt.getText().toString();
        String price = priceEt.getText().toString();
        String content = contentEt.getText().toString();
        String building = buildingSpinner.getSelectedItem().toString(); // 스피너 값 가져오기

        if(title.isEmpty() || bookTitle.isEmpty() || price.isEmpty() || content.isEmpty()){
            Toast.makeText(getContext(), "모든 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = currentUser.getUid();


        Timestamp timestamp = Timestamp.now();


        PostModel post = new PostModel(title, bookTitle, building, price, content, uid, timestamp);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("post")
                .add(post)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(getContext(), "업로드 성공!", Toast.LENGTH_SHORT).show();


                    titleEt.setText("");
                    bookTitleEt.setText("");
                    priceEt.setText("");
                    contentEt.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}