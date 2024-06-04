package org.fitmyss.coursework;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.fitmyss.coursework.MainActivity;
import org.fitmyss.coursework.R;
import org.fitmyss.coursework.ShopActivity;
import org.fitmyss.coursework.StockActivity;

public class NewActivity extends AppCompatActivity {

    private TextView addEmailUser;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new);

        addEmailUser = findViewById(R.id.addEmailUser);
        Button btnBack = findViewById(R.id.backNewActivity);
        Button btnListShop = findViewById(R.id.shopButton);
        Button btnListStock = findViewById(R.id.stockButton);

        // Получаем email из Intent, если он есть
        String email = getIntent().getStringExtra("EMAIL");
        if (email != null) {
            addEmailUser.setText(email);
        } else {
            // Если email в Intent отсутствует, получаем его из SharedPreferences
            sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            email = sharedPreferences.getString("email", null);
            if (email != null) {
                addEmailUser.setText(email);
            }
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        btnListShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(NewActivity.this, ShopActivity.class);
                startActivity(intent1);
            }
        });

        btnListStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(NewActivity.this, StockActivity.class);
                startActivity(intent2);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Сохраняем значение addEmailUser в SharedPreferences при уничтожении активности
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", addEmailUser.getText().toString());
        editor.apply();
    }
}
