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
import java.util.Arrays;
import java.util.Random;

public class PokemonActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView numberTextView;
    private TextView type1TxtV;
    private String url;
    private RequestQueue requestQueue;
    private ImageView imageView;
    private TextView mov1;
    private TextView mov2;
    private TextView mov3;
    private TextView mov4;

    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        random = new Random();

        url = getIntent().getStringExtra("url");
        nameTextView = findViewById(R.id.pokemon_name);
        numberTextView = findViewById(R.id.pokemon_number);
        type1TxtV = findViewById(R.id.pokemon_type1);
        imageView = findViewById(R.id.pokemon_img);
        mov1 = findViewById(R.id.pokemon_move1);
        mov2 = findViewById(R.id.pokemon_move2);
        mov3 = findViewById(R.id.pokemon_move3);
        mov4 = findViewById(R.id.pokemon_move4);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

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
                    JSONArray moves = response.getJSONArray("moves");
                    String finalType="";
                    for(int i=0; i<types.length();i++){
                        JSONObject typeEntry = types.getJSONObject(i);
                        int slot = typeEntry.getInt("slot");
                        String type = typeEntry.getJSONObject("type").getString("name");
                        type = type.substring(0, 1).toUpperCase() + type.substring(1);
                        if(slot==1){
                            finalType = type;
                        }else if(slot==2){
                            finalType = finalType.concat(" & ".concat(type));
                        }
                        setTypeText(finalType);
                    }
                    for (TextView textView : Arrays.asList(mov1, mov2, mov3, mov4)) {
                        int movNo = random.nextInt(moves.length());
                        String move = moves.getJSONObject(movNo).getJSONObject("move").getString("name");
                        textView.setText(move);
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

    private void setTypeText(String type){
        type1TxtV.setText(type);
    }

}