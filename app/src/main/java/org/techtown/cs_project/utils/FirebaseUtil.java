package org.techtown.cs_project.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {

    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();                     //유저 uid를 반환해라(uid는 파이어베이스 authentication에 뜨는 사용자 고유 번호
    }

    public static boolean isLoggedIn() {                                //로그인 되었는지 확인해서 로그인이 되었다면 인증하는 부분을 뛰어넘기 위해 만든 부분
        if(currentUserId() != null) {
            return true;
        }
        return false;
    }

    public static DocumentReference currentUserDetails() {              //파이어베이스의 users 컬렉션에 유저 데이터를 입력하는 부분
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }

}
