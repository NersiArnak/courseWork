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

public class StockActivity extends AppCompatActivity {

    private EditText editProductId, editQuantity, editPrice, editName, editCharacteristics;
    private Button buttonAddProduct, buttonViewProducts, buttonDeleteProducts, updateButton;
    private ListView productListView;
    private DBHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> productList;

    private int cashBalance = 0;

    private TextView textCashBalance;

    private static final String PREFS_NAME = "ShopPrefs";
    private static final String CASH_BALANCE_KEY_STOCK = "cashBalance";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock);

        dbHelper = new DBHelper(this);

        editProductId = findViewById(R.id.editProductIdStock);
        editQuantity = findViewById(R.id.editQuantityStock);
        editPrice = findViewById(R.id.editPriceStock);
        editName = findViewById(R.id.editNameStock);
        editCharacteristics = findViewById(R.id.editCharacteristicsStock);

        buttonAddProduct = findViewById(R.id.buttonAddProductStock);
        buttonViewProducts = findViewById(R.id.buttonViewProductsStock);
        productListView = findViewById(R.id.productListViewStock);
        buttonDeleteProducts = findViewById(R.id.buttonSellProductsStock);
        textCashBalance = findViewById(R.id.textCashBalanceStock);
        updateButton = findViewById(R.id.buttonUpdateProductsStock);

        productList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productList);
        productListView.setAdapter(adapter);

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductStock();
            }
        });

        buttonViewProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewProductsStock();
            }
        });

        Button backStock = findViewById(R.id.backStock);
        backStock.setOnClickListener(new View.OnClickListener() { // Теперь backShop инициализирован
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(StockActivity.this, NewActivity.class);
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
                    int priceToDelete = dbHelper.getProductPriceStock(id);
                    dbHelper.sellProductStock(id); // Изменили на sellProduct
                    cashBalance += priceToDelete; // Вместо уменьшения, увеличиваем кассовый баланс
                    textCashBalance.setText("Общая касса: " + cashBalance);
                    saveCashBalance();

                    viewProductsStock();
                    Toast.makeText(StockActivity.this, "Продукт продан", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StockActivity.this, "Введите ID продукта для продажи", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                        Toast.makeText(StockActivity.this, "Заполните все поля для обновления", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int quantity = Integer.parseInt(quantityString);
                    int price = Integer.parseInt(priceString);

                    int currentPrice = dbHelper.getProductPriceStock(id);

                    price *= quantity;

                    int priceDifference = price - currentPrice;

                    Data newData = new Data(id, quantity, price, name, characteristics);
                    dbHelper.updateProductStock(newData);

                    if (priceDifference < 0) {
                        cashBalance += Math.abs(priceDifference);
                    } else if (priceDifference > 0) {
                    }

                    textCashBalance.setText("Общая касса: " + cashBalance);

                    viewProductsStock();

                    Toast.makeText(StockActivity.this, "Продукт успешно обновлен", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StockActivity.this, "Введите ID продукта для обновления", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadCashBalance();

    }

    private void addProductStock() {
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
        dbHelper.addProductStock(product);

        Toast.makeText(this, "Товар добавлен успешно!", Toast.LENGTH_SHORT).show();
    }

    private void viewProductsStock() {
        Cursor cursor = dbHelper.getAllProductsStock();
        if (cursor.getCount() == 0) {
            Toast.makeText(this
                    , "Товары не найдены", Toast.LENGTH_SHORT).show();
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
        editor.putInt(CASH_BALANCE_KEY_STOCK, cashBalance);
        editor.apply();
    }

    private void loadCashBalance() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        cashBalance = sharedPreferences.getInt(CASH_BALANCE_KEY_STOCK, 0);
        textCashBalance.setText("Общая касса: " + cashBalance);
    }
}
