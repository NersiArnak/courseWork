package org.fitmyss.coursework;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button buttonSignIn;
    private EditText editEmail;
    private EditText editPassword;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        buttonSignIn = findViewById(R.id.buttonSignIn);
        editEmail = findViewById(R.id.emailID);
        editPassword = findViewById(R.id.passwordID);

        TextView textSignUp = findViewById(R.id.textSignUp);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Введите почту и пароль", Toast.LENGTH_SHORT).show();
                } else {
                    if (dbHelper.checkUserByEmail(email)) {
                        if (dbHelper.checkUserPassword(email, password)) {
                            Intent intent = new Intent(MainActivity.this, NewActivity.class);
                            intent.putExtra("EMAIL", email); // Передача электронной почты в Intent
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Пользователь с такой почтой не найден", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        textSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class); // Исправлено здесь
                startActivity(intent);
            }
        });

    }
}