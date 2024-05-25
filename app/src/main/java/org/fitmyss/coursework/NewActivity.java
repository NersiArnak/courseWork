package org.fitmyss.coursework;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class NewActivity extends AppCompatActivity {

    private TextView addEmailUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new);

        addEmailUser = findViewById(R.id.addEmailUser);
        Button btnBack = findViewById(R.id.backNewActivity);
        Button btnListShop = findViewById(R.id.shopButton);

        String email = getIntent().getStringExtra("EMAIL");
        if (email != null) {
            addEmailUser.setText(email);
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



    }
}
