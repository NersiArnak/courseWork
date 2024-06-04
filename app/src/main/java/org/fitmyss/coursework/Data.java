package org.fitmyss.coursework;

import android.content.Intent;

public class Data {
    String email;
    String password;

    int id;
    int quantity;
    int price;

    String nameProduct;
    String characteristics;

    public Data(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Data(int id, int quantity, int price, String nameProduct, String characteristics) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
        this.nameProduct = nameProduct;
        this.characteristics = characteristics;
    }
    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return nameProduct;
    }

    public String getCharacteristics() {
        return characteristics;
    }

    public int getId() {
        return id;
    }
}
