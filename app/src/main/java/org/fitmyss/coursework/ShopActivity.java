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
        buttonDeleteProducts = findViewById(R.id.buttonDeleteProducts);
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

        backShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(ShopActivity.this, NewActivity.class);
                startActivity(intent3);
            }
        });

        buttonDeleteProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idString = editProductId.getText().toString(); // Получаем ID из EditText
                if (!idString.isEmpty()) {
                    int id = Integer.parseInt(idString);
                    int priceToDelete = dbHelper.getProductPrice(id);
                    dbHelper.deleteProduct(id);
                    cashBalance -= priceToDelete;
                    textCashBalance.setText("Кассовый баланс: " + cashBalance);
                    saveCashBalance();

                    viewProducts();
                    Toast.makeText(ShopActivity.this, "Продукт удален", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShopActivity.this, "Введите ID продукта для удаления", Toast.LENGTH_SHORT).show();
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

                    // Умножаем цену на количество
                    price *= quantity;

                    Data newData = new Data(id, quantity, price, name, characteristics);
                    dbHelper.updateProduct(newData);

                    viewProducts();

                    Toast.makeText(ShopActivity.this, "Продукт успешно обновлен", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShopActivity.this, "Введите ID продукта для обновления", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonViewProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewProducts();
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

        // Проверяем, заполнены ли все поля
        if (idString.isEmpty() || quantityString.isEmpty() || priceString.isEmpty() || name.isEmpty() || characteristics.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = Integer.parseInt(idString);
        int quantity = Integer.parseInt(quantityString);
        int price = Integer.parseInt(priceString);

        // Умножаем цену на количество
        price *= quantity;

        Data product = new Data(id, quantity, price, name, characteristics);
        dbHelper.addProduct(product);

        dbHelper.updateCashBalance(price); // Увеличиваем значение кассы на цену товара

        cashBalance += price;
        textCashBalance.setText("Кассовый баланс: " + cashBalance);

        // Сохраняем обновленный кассовый баланс
        saveCashBalance();

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
            stringBuilder.setLength(0); // Clear the StringBuilder for the next product
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
        textCashBalance.setText("Кассовый баланс: " + cashBalance);
    }
}