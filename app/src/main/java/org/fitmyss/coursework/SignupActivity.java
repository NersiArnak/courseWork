package org.fitmyss.coursework;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    DBHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private ListView contactsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        dbHelper = new DBHelper(this);

        EditText editLogin = findViewById(R.id.loginID);
        EditText editPassword = findViewById(R.id.passwordID);

        //contactsListView = findViewById(R.id.contactsListView);

        Button btnAdd = findViewById(R.id.buttonAdd);
        Button btnBack = findViewById(R.id.backSignUp);
        //btnGet.setOnClickListener(v -> showContacts());

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = editLogin.getText().toString();
                String password = editPassword.getText().toString();
                Data objDataAdd = new Data(login, password);
                dbHelper.addOne(objDataAdd);
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

    /*private void showContacts() {
        Cursor cursor = dbHelper.getAll();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "База данных пуста", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        while (cursor.moveToNext()) {
            stringBuilder.append("Почта: ").append(cursor.getString(0)).append("\n");
            stringBuilder.append("Пароль: ").append(cursor.getString(1)).append("\n");

        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringBuilder.toString().split("\n\n"));
        contactsListView.setAdapter(adapter);
    }*/

}