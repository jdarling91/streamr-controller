package com.example.streamr_controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import maps.MovieMap;

import com.example.streamr_controller.objects.MovieData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends Activity {
	private ArrayAdapter<MovieData> mAdapter;
	private ArrayList<MovieData> mMovieList = new ArrayList<MovieData>();
	
	final String GET_MOVIES_URL = "http://192.168.0.17:8888/api/rest/movies_get_movies.php";
	
	// Views //////////
	ListView mListView;
	Button mSearchButton;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Set up the search button.
        mSearchButton = (Button) findViewById(R.id.searchButton);
        mSearchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SearchActivity.class);
				startActivity(intent);
			}
        });
        
        // MY GOD THIS IS DIRTY
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mMovieList = getMovies();
        mAdapter = new ArrayAdapter<MovieData>(this, android.R.layout.simple_list_item_1, android.R.id.text1, mMovieList);
        mListView = (ListView) findViewById(R.id.list);
        
        // Assign adapter to mListView
        mListView.setAdapter(mAdapter); 
  
        // mListView Item Click Listener
        mListView.setOnItemClickListener(new OnItemClickListener() {
   
        	@Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  
        		// Get the movie that was clicked.
            	MovieData clickedMovie = (MovieData) mListView.getItemAtPosition(position);
            	
            	// Create a new intent to start the player activity.
            	Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
            	intent.putExtra("movie", clickedMovie);
            	
            	// Start the activity
            	startActivity(intent);
            }
    	}); 
    }


    private ArrayList<MovieData> getMovies() {    	
    	String jsonResult = performGET(GET_MOVIES_URL);
    	MovieMap movieMap = new MovieMap(jsonResult);
    	return movieMap.getMovieData();
	}
    
    /**
     * Perform a get request to the given URL.
     * 
     * @param resourceUrl The URL where the resource is located.
     * @return 
     */
    private String performGET(String resourceUrl) {
    	try {
            URL url = new URL(resourceUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            InputStream inStream = connection.getInputStream();
            if (connection.getResponseCode() == 204) {
                return "error";
            }
            BufferedReader input = new BufferedReader(new InputStreamReader(inStream));

            String line = "";
            String result = "";
            while ((line = input.readLine()) != null) {
                result += line;
            }

            return result;
        } catch (Exception ex) {
            System.err.println("MainActivity/" + ex);
            return ex.toString();
        }
    }
}
