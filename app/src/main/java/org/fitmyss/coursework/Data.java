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

    public Data(String email, int id, int quantity, int price, String nameProduct, String characteristics) {
        this.email = email;
        this.id = id;
        this.quantity = quantity;
        this.price = price;
        this.nameProduct = nameProduct;
        this.characteristics = characteristics;
    }
}
