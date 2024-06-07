package org.fitmyss.coursework;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private Button buttonAddProduct, buttonViewProducts, buttonDeleteProducts, backShop;
    private ListView productListView;
    private DBHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> productList;

    private int cashBalance = 0;

    private TextView textCashBalance;

    private static final String PREFS_NAME = "ShopPrefs";
    private static final String CASH_BALANCE_KEY = "cashBalance";

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
        buttonDeleteProducts = findViewById(R.id.buttonSellProducts);
        backShop = findViewById(R.id.backShop);

        textCashBalance = findViewById(R.id.textCashBalanceStock);

        productListView = findViewById(R.id.productListView);

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

        backShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(ShopActivity.this, NewActivity.class);
                startActivity(intent3);
            }
        });

        buttonDeleteProducts.setText("Продать");
        buttonDeleteProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idString = editProductId.getText().toString(); // Получаем ID из EditText
                if (!idString.isEmpty()) {
                    int id = Integer.parseInt(idString);
                    Data productToDelete = dbHelper.getProductById(id);
                    if (productToDelete != null) {
                        int priceToDelete = productToDelete.getPrice();
                        dbHelper.sellProduct(id); // Изменили на sellProduct
                        cashBalance += priceToDelete; // Вместо уменьшения, увеличиваем кассовый баланс
                        textCashBalance.setText("Общая касса: " + cashBalance);
                        saveCashBalance();

                        viewProducts();
                        Toast.makeText(ShopActivity.this, "Материал продан", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ShopActivity.this, "Материал с указанным ID не найден", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ShopActivity.this, "Введите ID Материала для продажи", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button updateButton = findViewById(R.id.buttonUpdateProducts);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idString = editProductId.getText().toString();
                if (!idString.isEmpty()) {
                    int id = Integer.parseInt(idString);
                    String quantityString = editQuantity.getText().toString().trim();
                    String priceString = editPrice.getText().toString().trim();
                    String name = editName.getText().toString().trim();
                    String characteristics = editCharacteristics.getText().toString().trim();

                    if (quantityString.isEmpty() || priceString.isEmpty() || name.isEmpty() || characteristics.isEmpty()) {
                        Toast.makeText(ShopActivity.this, "Заполните все поля для обновления", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int quantity = Integer.parseInt(quantityString);
                    int price = Integer.parseInt(priceString);
                    int currentPrice = dbHelper.getProductPrice(id);

                    price *= quantity;

                    int priceDifference = currentPrice - price;

                    Data newData = new Data(id, quantity, price, name, characteristics);
                    dbHelper.updateProduct(newData);

                    if (priceDifference > 0) {
                        cashBalance += priceDifference;
                    }

                    textCashBalance.setText("Общая касса: " + cashBalance);
                    saveCashBalance();

                    viewProducts();

                    Toast.makeText(ShopActivity.this, "Продукт успешно обновлен", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShopActivity.this, "Введите ID продукта для обновления", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadCashBalance();
    }


    private void addProduct() {
        String idString = editProductId.getText().toString().trim();
        String quantityString = editQuantity.getText().toString().trim();
        String priceString = editPrice.getText().toString().trim();
        String name = editName.getText().toString().trim();
        String characteristics = editCharacteristics.getText().toString().trim();

        if (idString.isEmpty() || quantityString.isEmpty() || priceString.isEmpty() || name.isEmpty() || characteristics.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = Integer.parseInt(idString);
        int quantity = Integer.parseInt(quantityString);
        int price = Integer.parseInt(priceString);

        price *= quantity;

        Data product = new Data(id, quantity, price, name, characteristics);
        dbHelper.addProduct(product);

        Toast.makeText(this, "Товар добавлен успешно!", Toast.LENGTH_SHORT).show();
    }


    private void viewProducts() {
        Cursor cursor = dbHelper.getAllProducts();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Товары не найдены", Toast.LENGTH_SHORT).show();
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
            stringBuilder.setLength(0);

        }

        adapter.notifyDataSetChanged();
        cursor.close();
    }

    private void saveCashBalance() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(CASH_BALANCE_KEY, cashBalance);
        editor.apply();
    }

    private void loadCashBalance() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        cashBalance = sharedPreferences.getInt(CASH_BALANCE_KEY, 0);
        textCashBalance.setText("Общая касса: " + cashBalance);
    }
}