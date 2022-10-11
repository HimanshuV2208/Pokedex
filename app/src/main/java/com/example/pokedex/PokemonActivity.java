package com.example.pokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
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
                    String type1 = "", type2 = "";
                    for(int i=0; i<types.length();i++){
                        JSONObject typeEntry = types.getJSONObject(i);
                        int slot = typeEntry.getInt("slot");
                        String type = typeEntry.getJSONObject("type").getString("name");
                        type = type.substring(0, 1).toUpperCase() + type.substring(1);
                        if(slot==1) type1 = type;
                        else if(slot==2) type2 = type;
                    }
                    setTypeText(type1, type2);
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

    private void setTypeText(String type1, String type2){
        BackgroundColorSpan bcs1 = getBackground(type1), bcs2;
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(type1);
        if(type2.equals("")) {
            bcs2 = null;
        }
        else {
            bcs2 = getBackground(type2);
            ssb.append(" & ");
            ssb.append(type2);
        }
        ssb.setSpan(bcs1, 0, type1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if(bcs2!=null) ssb.setSpan(bcs2, type1.length()+3, type1.length()+3+type2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        type1TxtV.setText(ssb);
    }

    private BackgroundColorSpan getBackground(String type){
        BackgroundColorSpan backgroundColorSpan;
        int color = 0, col;
        if(type.equalsIgnoreCase("ghost")) color = R.color.ghost;
        else if(type.equalsIgnoreCase("dragon")) color = R.color.dragon;
        else if(type.equalsIgnoreCase("ice")) color = R.color.ice;
        else if(type.equalsIgnoreCase("fighting")) color = R.color.fighting;
        else if(type.equalsIgnoreCase("electric")) color = R.color.electric;
        else if(type.equalsIgnoreCase("rock")) color = R.color.rock;
        else if(type.equalsIgnoreCase("bug")) color = R.color.bug;
        else if(type.equalsIgnoreCase("fire")) color = R.color.fire;
        else if(type.equalsIgnoreCase("grass")) color = R.color.grass;
        else if(type.equalsIgnoreCase("ground")) color = R.color.ground;
        else if(type.equalsIgnoreCase("psychic")) color = R.color.psychic;
        else if(type.equalsIgnoreCase("flying")) color = R.color.flying;
        else if(type.equalsIgnoreCase("normal")) color = R.color.normal;
        else if(type.equalsIgnoreCase("water")) color = R.color.water;
        else if(type.equalsIgnoreCase("poison")) color = R.color.poison;
        int c = getResources().getColor(color);
        backgroundColorSpan = new BackgroundColorSpan(c);
        return backgroundColorSpan;
    }

}