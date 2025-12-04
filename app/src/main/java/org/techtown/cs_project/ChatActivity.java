package org.techtown.cs_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import org.techtown.cs_project.adapter.ChatRecyclerAdapter;
import org.techtown.cs_project.model.ChatMessageModel;
import org.techtown.cs_project.model.ChatroomModel;
import org.techtown.cs_project.model.UserModel;
import org.techtown.cs_project.utils.AndroidUtil;
import org.techtown.cs_project.utils.FirebaseUtil;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;

    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton picturebtn;
    Button confirmDepositBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;

    boolean isDepositConfirmedState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUser.getUserId());

        // 뷰 연결
        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        picturebtn = findViewById(R.id.picture_btn);
        confirmDepositBtn = findViewById(R.id.confirm_deposit_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.otehr_username);
        recyclerView = findViewById(R.id.chat_recycle_view);

        picturebtn.setOnClickListener((v) -> {
            Intent intent = new Intent(ChatActivity.this, Show_Picture.class);
            intent.putExtra("chatroomId", chatroomId);
            AndroidUtil.passUserModelAsIntent(intent, otherUser);
            startActivity(intent);
        });

        confirmDepositBtn.setOnClickListener(v -> {
            FirebaseUtil.getChatroomReference(chatroomId).update("depositConfirmed", true)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Toast.makeText(this, "입금 확인 처리되었습니다.", Toast.LENGTH_SHORT).show();
                            confirmDepositBtn.setVisibility(View.GONE);
                        }
                    });
        });

        backBtn.setOnClickListener((v) -> {
            onBackPressed();
        });
        otherUsername.setText(otherUser.getUsername());

        sendMessageBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if (message.isEmpty()) {
                return;
            }
            sendMessageToUser(message);
        }));

        getOnCreateChatroomModel();
        setupChatRecyclerView();
    }

    void setupChatRecyclerView() {
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }


    void sendMessageToUser(String  message) {
        if(chatroomModel != null) {
            chatroomModel.setLastMessageTimestamp(Timestamp.now());
            chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
            chatroomModel.setLastMessage(message);
            FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
        }

        ChatMessageModel chatMessageModel = new ChatMessageModel(message,FirebaseUtil.currentUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()) {
                            messageInput.setText("");
                        }
                    }
                });
    }

    void getOnCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null || value == null) return;

                chatroomModel = value.toObject(ChatroomModel.class);

                if (chatroomModel == null) {
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                            Timestamp.now(),
                            "",
                            FirebaseUtil.currentUserId()
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                } else {
                    isDepositConfirmedState = chatroomModel.isDepositConfirmed();

                    String myUid = FirebaseUtil.currentUserId();
                    String buyerId = chatroomModel.getBuyerId();

                    if (myUid.equals(buyerId)) {
                        confirmDepositBtn.setVisibility(View.GONE);
                        picturebtn.setVisibility(View.VISIBLE);
                    } else {
                        picturebtn.setVisibility(View.GONE);

                        if (isDepositConfirmedState) {
                            confirmDepositBtn.setVisibility(View.GONE);
                        } else {
                            confirmDepositBtn.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }
}