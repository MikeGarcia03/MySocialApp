package models;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PostItems {

    private String userId, userName, title, imageUrl, description;

    public PostItems() {
    }

    public PostItems(String userId, String userName, String title, String imageUrl, String description) {
        this.userId = userId;
        this.userName = userName;
        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
