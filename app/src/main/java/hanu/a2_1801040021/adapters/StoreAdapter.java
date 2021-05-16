package hanu.a2_1801040021.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import hanu.a2_1801040021.R;
import hanu.a2_1801040021.db.ProductManager;
import hanu.a2_1801040021.models.Product;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreHolder> implements Filterable {
    List<Product> products;
    List<Product> searchedProducts;
    ProductManager productManager;

    public StoreAdapter(List<Product> products) {
        this.products = products;
        this.searchedProducts = products;
    }

    @NonNull
    @Override
    public StoreAdapter.StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Activity to display
        Context context = parent.getContext();

        //xml to java object
        LayoutInflater inflater = LayoutInflater.from(context);

        View itemView = inflater.inflate(R.layout.item_product, parent, false);

        return new StoreAdapter.StoreHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreAdapter.StoreHolder holder, int position) {
        Product product = searchedProducts.get(position);

        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return searchedProducts.size();
    }


    public class StoreHolder extends RecyclerView.ViewHolder {
        private TextView productName, productPrice;
        private ImageButton btnAddToCart;
        private ImageView productThumbnail;
        private Context context;

        public StoreHolder(@NonNull View itemView, Context context) {
            super(itemView);
            productName = itemView.findViewById(R.id.tvProductName);
            productPrice = itemView.findViewById(R.id.tvProductPrice);
            productThumbnail = itemView.findViewById(R.id.thumnail);
            btnAddToCart = itemView.findViewById(R.id.btnAdd);

            this.context = context;
        }

        public void bind(Product product) {
            productName.setText(product.getName());
            productPrice.setText(product.getUnitPrice() + " VND");

            btnAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    productManager = ProductManager.getInstance(context);
                    boolean isAdded = false;
                    boolean isUpdated = false;
                    Product productDb = productManager.findProductById(product.getId());
                    if(productDb == null){
                        product.increaseQuantity();
                        isAdded = productManager.addProduct(product);
                    }else{
                        productDb.increaseQuantity();
                        isUpdated = productManager.updateProduct(productDb);
                    }

                    if(isAdded || isUpdated){
                        Toast.makeText(context, "Add product successful", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "Add fail", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            ImageLoader task = new ImageLoader();
            task.execute(product.getThumbnail());
        }


        public class ImageLoader extends AsyncTask<String, Void, Bitmap> {
            URL image_url;
            HttpURLConnection urlConnection;


            @Override
            protected Bitmap doInBackground(String... strings) {
                try {
                    image_url = new URL(strings[0]);
                    urlConnection = (HttpURLConnection) image_url.openConnection();
                    urlConnection.connect();

                    InputStream is = urlConnection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    return bitmap;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                productThumbnail.setImageBitmap(bitmap);
            }
        }

    }

    //Search Handler
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String key = charSequence.toString();
                if (key.isEmpty()) {
                    searchedProducts = products;
                } else {
                    List<Product> listSearched = new ArrayList<>();
                    for (Product row : products) {
                        if (row.getName().toLowerCase().contains(key.toLowerCase())) {
                            listSearched.add(row);
                        }
                    }

                    searchedProducts = listSearched;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = searchedProducts;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                searchedProducts = (List<Product>) results.values;

                for (Product item :
                        searchedProducts) {
                }

                notifyDataSetChanged();
            }
        };
    }
}
