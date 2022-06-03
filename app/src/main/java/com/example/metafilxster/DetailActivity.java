package com.example.metafilxster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.metafilxster.databinding.ActivityDetailBinding;
import com.example.metafilxster.databinding.ActivityMainBinding;
import com.example.metafilxster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;


public class DetailActivity extends YouTubeBaseActivity {

    private String getVideoUrl;
    // the movie to display
    Movie movie;
    // the view objects
    TextView tvTitle;

    TextView tvOverview;
    RatingBar rbVoteAverage;
    ImageView detailPosterImage;
    YouTubePlayerView ytPlayerView;
    String videoId;
    private ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_detail);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());

        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        tvTitle = binding.detailTitle;
        tvOverview = binding.detailOverview;
        rbVoteAverage = binding.ratingBar;
        ytPlayerView = binding.player;

        videoId = "tKodtNFpzBA";
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("DetailActivity", String.format("Showing details for '%s'", movie.getTitle()));
        getVideoUrl = String.format("https://api.themoviedb.org/3/movie/%d/videos?api_key=%s", movie.getId(), getString(R.string.movieDb_api_key));
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(getVideoUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d("DetailActivity", "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i("DetailActivity", "Results: " + results.toString());
                    if (results.length() > 0){
                        videoId = results.getJSONObject(0).getString("key");
                        Log.i("DetailActivity", "Video key: " + results.getJSONObject(0).getString("key").toString());


                    }
                    ytPlayerView.initialize(getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                            YouTubePlayer youTubePlayer, boolean b) {
                            // do any work here to cue video, play video, etc.
                            youTubePlayer.cueVideo(videoId);
                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                            YouTubeInitializationResult youTubeInitializationResult) {
                            // log the error
                            Log.e("MovieTrailerActivity", "Error initializing YouTube player");
                        }
                    });

//                    movies.addAll(Movie.fromJsonArray(results));
//                    movieAdapter.notifyDataSetChanged();
//                    Log.i(TAG, "Movies:" + movies.size());
                } catch (JSONException e) {
                    Log.e("DetailActivity", "Got json error right here!", e);
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("DetailActivity", "onFailure");
            }
        });
      

//        Glide.with(this)
//                .load(movie.getPosterPath())
//                .placeholder(R.drawable.flicks_movie_placeholder)
//                .into(detailPosterImage);
        // set the title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage / 2.0f);
    }
}