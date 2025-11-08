package org.techtown.cs_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginOtpActivity extends AppCompatActivity {

    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        phoneNumber = getIntent().getExtras().getString("phone");   //전 액비티비티에서 phone이라는 키로 넘겨준 extra 정보를 읽어와라

        Toast.makeText(getApplicationContext(),  phoneNumber, Toast.LENGTH_LONG).show();

        Map<String, String> data = new HashMap<>();
        FirebaseFirestore.getInstance().collection("test").add(data);

    }
}