package com.example.pokedex;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PokedexAdapter extends RecyclerView.Adapter<PokedexAdapter.PokedexViewHolder> {
    static class PokedexViewHolder extends RecyclerView.ViewHolder{

        public LinearLayout containerView;
        public TextView textView;

        PokedexViewHolder(View view){
            super(view);
            containerView = view.findViewById(R.id.pokedex_row);
            textView = view.findViewById(R.id.pokedex_row_text_view);

            containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Pokemon current = (Pokemon) containerView.getTag();
                    Intent intent = new Intent(view.getContext(), PokemonActivity.class);
                    intent.putExtra("url", current.getURL());

                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    private List<Pokemon> pokemon = new ArrayList<>();
    private RequestQueue requestQueue;

    PokedexAdapter(Context context){
        requestQueue = Volley.newRequestQueue(context);
        loadPokemon();
    }

    public void loadPokemon(){
        String url = "https://pokeapi.co/api/v2/pokemon?limit=151";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    for(int i = 0; i<results.length();i++){
                        JSONObject result = results.getJSONObject(i);
                        String name = result.getString("name");
                        pokemon.add(new Pokemon(name.substring(0, 1).toUpperCase()+ name.substring(1),
                                result.getString("url")));
                    }
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("error", "JSON error");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "Pokemon List error");
            }
        });

        requestQueue.add(request);
    }


    @NonNull
    @NotNull
    @Override
    public PokedexViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokedex_row,parent,false);
        return new PokedexViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PokedexViewHolder holder, int position) {
        Pokemon current = pokemon.get(position);
        holder.textView.setText(current.getName());

        holder.containerView.setTag(current);
    }

    @Override
    public int getItemCount() {
        return pokemon.size();
    }
}
