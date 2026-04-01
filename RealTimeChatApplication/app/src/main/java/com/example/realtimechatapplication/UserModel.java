package com.example.realtimechatapplication;

public class UserModel {
    private String userId;
    private String userName;
    private String profilePic;
    private boolean isSelected = false;

    public UserModel() {}

    public UserModel(String userId, String userName, String profilePic) {
        this.userId = userId;
        this.userName = userName;
        this.profilePic = profilePic;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getProfilePic() { return profilePic; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}