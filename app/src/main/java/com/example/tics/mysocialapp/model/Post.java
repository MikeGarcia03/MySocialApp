package com.example.tics.mysocialapp.model;

import java.util.HashMap;
import java.util.Map;

public class Post {
    public String uid;
    public String image;
    public String title;
    public String description;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public Post() {
    }

    public Post(String uid, String image, String title, String body) {
        this.uid = uid;
        this.image = image;
        this.title = title;
        this.description = body;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("image", image);
        result.put("title", title);
        result.put("body", description);
        //result.put("starCount", starCount);
        result.put("stars", stars);

        return result;
    }
}
