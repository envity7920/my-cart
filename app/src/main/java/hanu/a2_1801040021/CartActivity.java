package hanu.a2_1801040021;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hanu.a2_1801040021.adapters.CartAdapter;
import hanu.a2_1801040021.db.ProductManager;
import hanu.a2_1801040021.models.Product;

public class CartActivity extends AppCompatActivity {
    RecyclerView rvCart;
    public TextView totalPrice;
    List<Product> cartLines;
    CartAdapter adapter;
    ProductManager productManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        productManager = ProductManager.getInstance(this);

        //Recycler view
        rvCart = findViewById(R.id.rv_order);

        totalPrice = findViewById(R.id.total_val);
        totalPrice.setText(productManager.getTotalPrice()+" VND");

        //Get data from SQLite Database
        productManager = ProductManager.getInstance(this);
        cartLines = productManager.getAllProducts();


        rvCart.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(cartLines, this);
        rvCart.setAdapter(adapter);

    }

}