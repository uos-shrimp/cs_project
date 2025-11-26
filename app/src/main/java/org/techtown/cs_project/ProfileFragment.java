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

// ★ 중요: PostModel 클래스가 같은 패키지 안에 만들어져 있어야 오류가 안 납니다!

public class ProfileFragment extends Fragment {

    // 1. 사용할 뷰(위젯) 변수 선언
    EditText titleEt, bookTitleEt, priceEt, contentEt;
    Spinner buildingSpinner;
    Button uploadBtn;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 레이아웃(XML)을 뷰 객체로 변환
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 2. XML의 ID와 변수 연결 (findViewById)
        titleEt = view.findViewById(R.id.new_title);
        bookTitleEt = view.findViewById(R.id.book_title);
        buildingSpinner = view.findViewById(R.id.buildingSpinner);
        priceEt = view.findViewById(R.id.price);
        contentEt = view.findViewById(R.id.main_text);
        uploadBtn = view.findViewById(R.id.upload);

        // 3. 업로드 버튼 클릭 이벤트 설정
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });

        return view;
    }

    // 데이터를 파이어베이스에 올리는 함수
    void uploadData() {
        // 입력된 내용 가져오기
        String title = titleEt.getText().toString();
        String bookTitle = bookTitleEt.getText().toString();
        String price = priceEt.getText().toString();
        String content = contentEt.getText().toString();
        String building = buildingSpinner.getSelectedItem().toString(); // 스피너 값 가져오기

        // 빈칸 확인
        if(title.isEmpty() || bookTitle.isEmpty() || price.isEmpty() || content.isEmpty()){
            Toast.makeText(getContext(), "모든 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 현재 로그인한 유저 정보 가져오기
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = currentUser.getUid();

        // 현재 시간
        Timestamp timestamp = Timestamp.now();

        // 4. PostModel 객체 생성 (이전에 만든 클래스 활용)
        PostModel post = new PostModel(title, bookTitle, building, price, content, uid, timestamp);

        // 5. 파이어베이스 Firestore에 저장
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("post") // 'post'라는 컬렉션 이름 사용
                .add(post)
                .addOnSuccessListener(documentReference -> {
                    // 성공 시
                    Toast.makeText(getContext(), "업로드 성공!", Toast.LENGTH_SHORT).show();

                    // 입력창 초기화
                    titleEt.setText("");
                    bookTitleEt.setText("");
                    priceEt.setText("");
                    contentEt.setText("");
                })
                .addOnFailureListener(e -> {
                    // 실패 시
                    Toast.makeText(getContext(), "업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}