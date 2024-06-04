package org.fitmyss.coursework;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        dbHelper = new DBHelper(this);

        EditText editEmail = findViewById(R.id.emailID);
        EditText editPassword = findViewById(R.id.passwordID);

        Button btnAdd = findViewById(R.id.buttonAdd);
        Button btnBack = findViewById(R.id.backSignUp);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editLogin = findViewById(R.id.loginID);
                EditText editPassword = findViewById(R.id.passwordID);
                String login = editLogin.getText().toString();
                String password = editPassword.getText().toString();

                if (!login.isEmpty() && !password.isEmpty()) {
                    if (!dbHelper.checkUserByEmail(login)) {
                        Data objDataAdd = new Data(login, password);
                        dbHelper.addOne(objDataAdd);
                        Toast.makeText(SignupActivity.this, "Успешная регистрация", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignupActivity.this, "Аккаунт уже зарегистрирован", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignupActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
