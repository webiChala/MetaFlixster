package com.example.metafilxster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.metafilxster.adapters.MovieAdapter;
import com.example.metafilxster.databinding.ActivityDetailBinding;
import com.example.metafilxster.databinding.ActivityMainBinding;
import com.example.metafilxster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {
    private String NOW_PLAYING;
    public static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    RecyclerView rvMovies;

    List<Movie> movies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        // activity_main.xml -> ActivityMainBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        NOW_PLAYING = String.format("https://api.themoviedb.org/3/movie/now_playing?api_key=%s", getString(R.string.movieDb_api_key));

//        // set bindings more efficiently through bindings
//        title = binding.title;       // was title = findViewById(R.id.title);
//        title.setText("My title");
//
//        // alternately, access views through binding when needed, instead of variables
//        binding.title.setText("My title");

        rvMovies = binding.rvMovies;
        movies = new ArrayList<>();
        //Create an adapter
        MovieAdapter movieAdapter = new MovieAdapter(this, movies);

        //set the adapter on the recycler view
        rvMovies.setAdapter(movieAdapter);
        //set a layout manager on the recycler view
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(NOW_PLAYING, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    movies.addAll(Movie.fromJsonArray(results));
                    movieAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Movies:" + movies.size());
                } catch (JSONException e) {
                    Log.e(TAG, "Got json error right here!", e);
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });
    }
}