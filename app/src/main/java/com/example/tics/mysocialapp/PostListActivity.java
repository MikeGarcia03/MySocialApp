package com.example.tics.mysocialapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import adapters.PostListAdapter;
import models.PostItems;

public class PostListActivity extends AppCompatActivity {

    private RecyclerView recyclerPostsList;
    private RecyclerView.Adapter postAdapter;
    private FirebaseFirestore firebaseFirestore;
    private List<PostItems> postsUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resycle_post_list);

        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerPostsList = findViewById(R.id.ricyclerPostList);
        postsUsers = new ArrayList<>();

        firebaseFirestore.collection("post")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                PostItems postItems = new PostItems();
                                postItems.setTitle((String) document.get("title"));
                                postItems.setImageUrl((String) document.get("image"));
                                postItems.setDescription((String) document.get("description"));

                                postsUsers.add(postItems);
                            }
                            recyclerPostsList.setLayoutManager(new LinearLayoutManager(PostListActivity.this,
                                    LinearLayoutManager.VERTICAL, false));
                            postAdapter = new PostListAdapter(getApplicationContext(),postsUsers);
                            recyclerPostsList.setAdapter(postAdapter);
                        } else {

                        }
                    }
                });
    }


}
