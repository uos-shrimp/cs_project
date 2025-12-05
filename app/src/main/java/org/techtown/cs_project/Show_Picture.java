package org.techtown.cs_project;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import org.techtown.cs_project.model.ChatroomModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Show_Picture extends AppCompatActivity {

    ImageButton backBtn;
    Button confirmBtn;
    ImageView authImageView;
    String chatroomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);

        chatroomId = getIntent().getStringExtra("chatroomId");

        backBtn = findViewById(R.id.back_btn);
        confirmBtn = findViewById(R.id.confirm_btn);
        authImageView = findViewById(R.id.auth_image_view);

        backBtn.setOnClickListener(v -> finish());

        confirmBtn.setEnabled(false);
        confirmBtn.setText("판매자의 입금 확인 대기 중...");
        confirmBtn.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));

        if (chatroomId != null) {
            checkDepositStatus();
        }

        loadLatestFirebaseImage();

        confirmBtn.setOnClickListener(v -> {

            Random random = new Random();
            int randomNum = random.nextInt(10000);
            //String authCode = String.format("%04d", randomNum);
            String authCode = "1234";

            new AlertDialog.Builder(Show_Picture.this)
                    .setTitle("인증번호 생성 완료")
                    .setMessage("인증번호: [ " + authCode + " ]\n\n이 번호는 구매자님에게만 보입니다.")
                    .setPositiveButton("확인", null)
                    .show();
        });
    }

    private void checkDepositStatus() {
        FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null || value == null || !value.exists()) return;

                        ChatroomModel model = value.toObject(ChatroomModel.class);

                        if (model != null) {
                            if (model.isDepositConfirmed()) {
                                // 입금 확인됨 -> 버튼 활성화
                                confirmBtn.setEnabled(true);
                                confirmBtn.setText("사진 확인 및 인증번호 생성");
                                confirmBtn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.my_primary)));
                            } else {
                                confirmBtn.setEnabled(false);
                                confirmBtn.setText("판매자의 입금 확인 대기 중...");
                                confirmBtn.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                            }
                        }
                    }
                });
    }

    private void loadLatestFirebaseImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference listRef = storage.getReference();

        listRef.listAll()
                .addOnSuccessListener(listResult -> {
                    List<StorageReference> items = listResult.getItems();

                    if (items.isEmpty()) {
                        Toast.makeText(this, "서버에 이미지가 없습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Task<StorageMetadata>> tasks = new ArrayList<>();
                    for (StorageReference item : items) {
                        tasks.add(item.getMetadata());
                    }

                    Tasks.whenAllSuccess(tasks).addOnSuccessListener(objects -> {
                        List<StorageMetadata> metadataList = (List<StorageMetadata>) (List<?>) objects;

                        Collections.sort(metadataList, (o1, o2) ->
                                Long.compare(o1.getCreationTimeMillis(), o2.getCreationTimeMillis())
                        );

                        if (!metadataList.isEmpty()) {
                            StorageMetadata latestMetadata = metadataList.get(metadataList.size() - 1);

                            StorageReference latestRef = storage.getReference().child(latestMetadata.getName());

                            latestRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                Glide.with(Show_Picture.this)
                                        .load(uri)
                                        .into(authImageView);
                            }).addOnFailureListener(e -> {
                                Toast.makeText(this, "이미지 로드 실패", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "파일 목록을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                });
    }
}