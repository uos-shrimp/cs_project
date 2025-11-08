package org.techtown.cs_project.model;

import com.google.firebase.Timestamp;
                                                //users 컬렉션에 들어갈 데이터를 정리해서 클래스로 만든 부분
public class UserModel {
    private String phone;
    private String username;
    private Timestamp createdTimestamp;

    public UserModel() {

    }

    public UserModel(String phone, String username, Timestamp createdTimestamp) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
