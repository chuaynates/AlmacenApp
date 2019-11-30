package pe.huaynates.almacenapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import pe.huaynates.almacenapp.R;
import pe.huaynates.almacenapp.models.Producto;
import pe.huaynates.almacenapp.services.ApiService;
import pe.huaynates.almacenapp.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private Long id;

    private ImageView fotoImage;
    private TextView nombreText;
    private TextView detallesText;
    private TextView precioText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        fotoImage = (ImageView)findViewById(R.id.foto_image);
        nombreText = (TextView)findViewById(R.id.nombre_text);
        detallesText = (TextView)findViewById(R.id.detalles_text);
        precioText = (TextView)findViewById(R.id.precio_text);

        id = getIntent().getExtras().getLong("ID");
        Log.e(TAG, "id:" + id);

        initialize();
    }

    private void initialize() {

        ApiService service = ApiServiceGenerator.createService(this,ApiService.class);

        Call<Producto> call = service.showProducto(id);

        call.enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        Producto producto = response.body();
                        Log.d(TAG, "producto: " + producto);

                        String url = ApiService.API_BASE_URL + "/productos/images/" + producto.getImagen();
                        ApiServiceGenerator.createPicasso(DetailActivity.this).load(url).into(fotoImage);

                        nombreText.setText(producto.getNombre());
                        detallesText.setText(producto.getDetalles());
                        precioText.setText("Precio: S/. " + producto.getPrecio());

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(DetailActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(DetailActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }




}
