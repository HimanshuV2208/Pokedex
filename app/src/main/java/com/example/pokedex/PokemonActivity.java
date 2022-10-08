package com.example.pokedex;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PrivateKey;

public class PokemonActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView numberTextView;
    private TextView type1TxtV;
    private String url;
    private RequestQueue requestQueue;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        url = getIntent().getStringExtra("url");
        nameTextView = findViewById(R.id.pokemon_name);
        numberTextView = findViewById(R.id.pokemon_number);
        type1TxtV = findViewById(R.id.pokemon_type1);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        imageView = findViewById(R.id.pokemon_img);

        load();
    }

    public void load(){
        type1TxtV.setText("");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String name = response.getString("name");
                    nameTextView.setText(name.substring(0,1).toUpperCase()+name.substring(1));
                    int id = response.getInt("id");
                    numberTextView.setText(String.format("#%03d", id));
                    Picasso.get()
                            .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/"+id+".png")
                            .resize(256, 256).centerCrop().into(imageView);
                    JSONArray types = response.getJSONArray("types");
                    for(int i=0; i<types.length();i++){
                        JSONObject typeEntry = types.getJSONObject(i);
                        int slot = typeEntry.getInt("slot");
                        String type = typeEntry.getJSONObject("type").getString("name");
                        type = type.substring(0, 1).toUpperCase() + type.substring(1);
                        if(slot==1){
                            type1TxtV.setText(type);
                        }else if(slot==2){
                            String temp = type1TxtV.getText().toString() + " & ";
                            type1TxtV.setText(temp.concat(type));
                        }
                    }
                } catch (JSONException e) {
                    Log.e("error", "Pokemon JSON error");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "Pokemon Details error");
            }
        });
        requestQueue.add(request);
    }
}