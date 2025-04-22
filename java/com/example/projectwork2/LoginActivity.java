package com.example.projectwork2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonSignup;
    private Button buttonLogin;


    String VALID_USERNAME = "";
    //private final String VALID_PASSWORD = "gray";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignup=findViewById(R.id.buttonSignup);

        buttonLogin.setOnClickListener(view -> attemptLogin());
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent1);

            }
        });
    }

    private void attemptLogin() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(LoginActivity.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(
                "Users",
                new String[]{"name","pass_hash","user_id"},
                "name = ?",
                new String[]{""+username+""},
                null, null, null
        );

        while (cursor.moveToNext())
        {
            if(cursor.getString(cursor.getColumnIndexOrThrow("pass_hash")).equals(password))
            {
                // If login is successful, open MainActivity
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                VALID_USERNAME = username;
                intent.putExtra("username", username);
                intent.putExtra("user_id", id);
                startActivity(intent);
                finish(); // Close LoginActivity after successful login
            } else {
                Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }

            //Toast.makeText(MainActivity.this, "Id: "+id+" Name: "+name, Toast.LENGTH_SHORT).show();
        }

        cursor.close();
/*
        if (username.equals(VALID_USERNAME) && password.equals(VALID_PASSWORD)) {
            // If login is successful, open MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close LoginActivity after successful login
        } else {
            Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
 */
    }

}
