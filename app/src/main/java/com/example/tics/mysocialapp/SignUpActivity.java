package com.example.tics.mysocialapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private EditText firstNameEdiTxt,lastNameEdiTxt, emailEdiTxt, passEdiTxt;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirestore = FirebaseFirestore.getInstance();
        firstNameEdiTxt = findViewById(R.id.firstNameEditText);
        lastNameEdiTxt = findViewById(R.id.lastNameEditText);
        emailEdiTxt = findViewById(R.id.sEmailEditText);
        passEdiTxt = findViewById(R.id.sPasswordEditText);

        findViewById(R.id.sSignUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO validate if user and pass are not empty
                firebaseAuth.createUserWithEmailAndPassword(emailEdiTxt.getText().toString(),
                        passEdiTxt.getText().toString())
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this,
                                            "Process finished successful",
                                            Toast.LENGTH_LONG).show();
                                    writeNewRegister(firebaseAuth.getUid(),firstNameEdiTxt.getText().toString(),
                                            lastNameEdiTxt.getText().toString(),emailEdiTxt.getText().toString(),
                                            passEdiTxt.getText().toString());
                                } else {
                                    Toast.makeText(SignUpActivity.this,
                                            "Does not work", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                firebaseAuth.signInWithEmailAndPassword(emailEdiTxt.getText().toString(),
                        passEdiTxt.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    startActivity(new Intent(SignUpActivity.this,
                                            PostListActivity.class));
                                    Toast.makeText(SignUpActivity.this,
                                            "Process finished successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignUpActivity.this,
                                            "Pass o Usuario incorrecto", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    private void writeNewRegister(String userId, String firstName, String lastName, String email, String password) {

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("email", email);
        map.put("password", password);

        mFirestore.collection("register").document().set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Bien", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUpActivity.this, "Mal", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
