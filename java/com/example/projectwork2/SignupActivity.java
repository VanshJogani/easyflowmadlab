package com.example.projectwork2;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignupActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private EditText email;
    private EditText upi;
    private Button register;
    private EditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        username=findViewById(R.id.editusername);
        password=findViewById(R.id.editpassword);
        register=findViewById(R.id.register);
        email=findViewById(R.id.editemail);
        upi=findViewById(R.id.editupi);
        phone = findViewById(R.id.editphone);

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(SignupActivity.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty() || email.getText().toString().isEmpty() || upi.getText().toString().isEmpty() || phone.getText().toString().isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Enter all details", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignupActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                    String name = username.getText().toString();
                    String pass_hash = password.getText().toString();
                    String email_id = email.getText().toString();
                    String upi_id = upi.getText().toString();
                    String phone_no = phone.getText().toString();

                    ContentValues values = new ContentValues();
                    values.put("name", name);
                    values.put("email", email_id);
                    values.put("phone", phone_no);
                    values.put("pass_hash", pass_hash);
                    values.put("upi_id", upi_id);

                    long newRowId = db.insert("Users", null, values);
                    Toast.makeText(SignupActivity.this, "Please go to Login page", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(SignupActivity.this, "newRowId: " + newRowId, Toast.LENGTH_SHORT).show();
                }
            }

        });


    }
}