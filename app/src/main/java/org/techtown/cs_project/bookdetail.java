package org.techtown.cs_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

// ★ UserModel과 Util 클래스를 사용하기 위해 import
import org.techtown.cs_project.model.UserModel;
import org.techtown.cs_project.utils.AndroidUtil;
import org.techtown.cs_project.utils.FirebaseUtil;

public class bookdetail extends AppCompatActivity {

    TextView toolbarTitleTv, bookTitleTv, priceTv, sellerTv, buildingTv, contentTv;
    ImageButton backBtn;
    Button chatBtn;

    String sellerName = "(알 수 없음)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bookdetail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. 뷰 연결
        toolbarTitleTv = findViewById(R.id.detail_toolbar_title);
        bookTitleTv = findViewById(R.id.detail_book_title);
        priceTv = findViewById(R.id.detail_price);
        sellerTv = findViewById(R.id.detail_seller);
        buildingTv = findViewById(R.id.detail_building);
        contentTv = findViewById(R.id.detail_content);
        backBtn = findViewById(R.id.back_btn);
        chatBtn = findViewById(R.id.chat_btn);

        // 2. Intent 데이터 받기 (책 정보)
        String title = getIntent().getStringExtra("title");
        String bookTitle = getIntent().getStringExtra("bookTitle");
        String price = getIntent().getStringExtra("price");
        String building = getIntent().getStringExtra("building");
        String content = getIntent().getStringExtra("content");
        String userId = getIntent().getStringExtra("userId");

        // 3. 화면 표시
        if(title != null) toolbarTitleTv.setText(title);
        if(bookTitle != null) bookTitleTv.setText(bookTitle);
        if(price != null) priceTv.setText(price + "원");
        if(building != null) buildingTv.setText("수강 장소 : " + building);
        if(content != null) contentTv.setText(content);

        if (userId != null) {
            FirebaseFirestore.getInstance().collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            sellerName = documentSnapshot.getString("username");
                            sellerTv.setText("판매자 : " + sellerName);
                        } else {
                            sellerTv.setText("판매자 : " + userId);
                        }
                    });
        }

        backBtn.setOnClickListener(v -> finish());

        chatBtn.setOnClickListener(v -> {
            String myUid = FirebaseUtil.currentUserId();

            if (userId != null && userId.equals(myUid)) {
                AndroidUtil.showToast(this, "본인과는 채팅할 수 없습니다.");
                return;
            }

            UserModel otherUser = new UserModel();
            otherUser.setUserId(userId);
            otherUser.setUsername(sellerName);
            otherUser.setPhone("");            // 전화번호는 없으면 빈 값 처리

            Intent intent = new Intent(this, ChatActivity.class);


            AndroidUtil.passUserModelAsIntent(intent, otherUser);


            startActivity(intent);
        });
    }
}