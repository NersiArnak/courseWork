package org.fitmyss.coursework;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ShopActivity extends AppCompatActivity {

    private EditText editProductId, editQuantity, editPrice, editName, editCharacteristics;
    private Button buttonAddProduct, buttonViewProducts;
    private ListView productListView;
    private DBHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> productList;

    private int cashBalance = 0;

    private TextView textCashBalance; // Перемещаем сюда, чтобы инициализировать после загрузки макета

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop);

        dbHelper = new DBHelper(this);

        editProductId = findViewById(R.id.editProductId);
        editQuantity = findViewById(R.id.editQuantity);
        editPrice = findViewById(R.id.editPrice);
        editName = findViewById(R.id.editName);
        editCharacteristics = findViewById(R.id.editCharacteristics);

        buttonAddProduct = findViewById(R.id.buttonAddProduct);
        buttonViewProducts = findViewById(R.id.buttonViewProducts);
        productListView = findViewById(R.id.productListView);

        textCashBalance = findViewById(R.id.textCashBalance); // Инициализируем здесь

        productList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productList);
        productListView.setAdapter(adapter);

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });

        buttonViewProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewProducts();
            }
        });
    }

    private void addProduct() {
        int id = Integer.parseInt(editProductId.getText().toString().trim());
        int quantity = Integer.parseInt(editQuantity.getText().toString().trim());
        int price = Integer.parseInt(editPrice.getText().toString().trim());
        String name = editName.getText().toString().trim();
        String characteristics = editCharacteristics.getText().toString().trim();

        Data product = new Data(id, quantity, price, name, characteristics);
        dbHelper.addProduct(product);

        dbHelper.updateCashBalance(price); // Увеличиваем значение кассы на цену товара

        cashBalance += price;
        textCashBalance.setText("Кассовый баланс: " + cashBalance);

        Toast.makeText(this, "Product added successfully!", Toast.LENGTH_SHORT).show();
    }


    private void viewProducts() {
        Cursor cursor = dbHelper.getAllProducts();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No products found", Toast.LENGTH_SHORT).show();
            return;
        }

        productList.clear();
        StringBuilder stringBuilder = new StringBuilder();
        while (cursor.moveToNext()) {
            stringBuilder.append("ID: ").append(cursor.getString(0)).append("\n");
            stringBuilder.append("Quantity: ").append(cursor.getString(1)).append("\n");
            stringBuilder.append("Price: ").append(cursor.getString(2)).append("\n");
            stringBuilder.append("Name: ").append(cursor.getString(3)).append("\n");
            stringBuilder.append("Characteristics: ").append(cursor.getString(4)).append("\n\n");
            productList.add(stringBuilder.toString());
            stringBuilder.setLength(0); // Clear the StringBuilder for the next product
        }

        adapter.notifyDataSetChanged();
        cursor.close();
    }
}