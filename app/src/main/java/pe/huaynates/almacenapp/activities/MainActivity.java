package pe.huaynates.almacenapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import pe.huaynates.almacenapp.R;
import pe.huaynates.almacenapp.models.Producto;
import pe.huaynates.almacenapp.services.ApiService;
import pe.huaynates.almacenapp.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView productosList;
    private static final int REQUEST_REGISTER_FORM = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //testRest();

        productosList = findViewById(R.id.recyclerview);
        productosList.setLayoutManager(new LinearLayoutManager(this));

        productosList.setAdapter(new ProductosAdapter());

        initialize();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void logout(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().remove("islogged").remove("token").commit();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }


    private void testRest(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);

        Call<List<Producto>> call = service.findAll();

        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {

                if(response.isSuccessful()){
                    List<Producto> productos = response.body();
                    Log.d("MainActivity", "productos: "+productos);
                }else{
                    Toast.makeText(MainActivity.this, "Error: "+response,Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error crítico: "+t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void initialize() {

        ApiService service = ApiServiceGenerator.createService(this,ApiService.class);

        service.findAll().enqueue(new Callback<List<Producto>>() {

            @Override
            public void onResponse(@NonNull Call<List<Producto>> call, @NonNull Response<List<Producto>> response) {
                try {

                    if (response.isSuccessful()) {

                        List<Producto> productos = response.body();
                        Log.d(TAG, "productos: " + productos);

                        ProductosAdapter adapter = (ProductosAdapter) productosList.getAdapter();
                        adapter.setProductos(productos);
                        adapter.notifyDataSetChanged();

                    } else {
                        throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                    }

                } catch (Throwable t) {
                    Log.e(TAG, "onThrowable: " + t.getMessage(), t);
                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Producto>> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }
    public void showRegister(View view){
        startActivityForResult(new Intent(this, RegisterActivity.class), REQUEST_REGISTER_FORM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_REGISTER_FORM) {
            initialize();   // refresh data from rest service
        }
    }

}
