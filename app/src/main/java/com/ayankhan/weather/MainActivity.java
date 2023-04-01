package com.ayankhan.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.appsearch.SearchResult;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ayankhan.weather.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(SEARCH_QUERY_URL_EXTRA,editText.getText().toString());
        outState.putCharSequence(JSON_RESULT_EXTRA,searchResult.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    EditText editText;
    TextView textView;
    TextView searchResult;
    TextView errorView;
    ProgressBar progressBar;
    private static final String SEARCH_QUERY_URL_EXTRA = "query";
    private static final String JSON_RESULT_EXTRA = "result";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

           int menuItemThatWasSelected = item.getItemId();
           if (menuItemThatWasSelected == R.id.action_search) {
               Context context = MainActivity.this;
               String message = "Search clicked";
               Toast.makeText(context, message, Toast.LENGTH_LONG).show();
               makeGithubSearchQuery();
               return true;
           }
           else if(menuItemThatWasSelected == R.id.action_refresh)
           {
               Context context = MainActivity.this;
               String message = "Refresh clicked";
               searchResult.setText("");
               Toast.makeText(context, message, Toast.LENGTH_LONG).show();
               makeGithubSearchQuery();
               return  true;
           }
           return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.et_search_box);
        textView = (TextView) findViewById(R.id.url);
        searchResult = (TextView) findViewById(R.id.tv_url_display);
        errorView = (TextView) findViewById(R.id.errortext);
        progressBar = findViewById(R.id.pv_bar);

        if(savedInstanceState!=null)
        {
            searchResult.setText(savedInstanceState.getString(JSON_RESULT_EXTRA));
            editText.setText(savedInstanceState.getString(SEARCH_QUERY_URL_EXTRA));
        }
    }
    private void ShowJSONData()
    {
       searchResult.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.INVISIBLE);
    }
   private void ShowError()
  {
      searchResult.setVisibility(View.INVISIBLE);
       errorView.setVisibility(View.VISIBLE);
  }

    private void makeGithubSearchQuery() {
        String githubQuery = editText.getText().toString();
        //URL githubsearchurl = NetworkUtils.buildUrl(githubQuery);
        URL weatherUrl = NetworkUtils.buildUrl2();
        //searchResult.setText(githubsearchurl.toString());
        searchResult.setText(weatherUrl.toString());
        //new GithubQueryTask().execute(githubsearchurl);
        new FetchWeather().execute(weatherUrl);
    }
    public class  FetchWeather extends AsyncTask<URL, Void, String>{
        @Override
        public String doInBackground(URL... urls)
        {
            URL search =urls[0];
            String response=null;
            try{
                response = NetworkUtils.getResponseFromHttpUrl(search);

            }catch(IOException e)
            {
                e.printStackTrace();
            }
            return  response;
        }

        @Override
        public void onPostExecute(String s)
        {
            if(s!=null && !s.isEmpty())
            {
                try {
                    JSONObject json = new JSONObject(s);
                    JSONObject city= json.getJSONObject("city");
                    String cod= json.getString("cod");

                    searchResult.setText(city.getString("name")+"\n\n\n");
                    searchResult.append(cod);
                    ShowJSONData();

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    errorView.setText("Error occured");
                    ShowError();
                }
            }
        }
    }

    public class GithubQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL serch = urls[0];
            String githubSearchResults = null;
            try {
                githubSearchResults = NetworkUtils.getResponseFromHttpUrl(serch);
            }catch (IOException e)
            {
                e.printStackTrace();
            }
            return githubSearchResults;
        }

        @Override
        protected void onPostExecute(String s)
        {
            progressBar.setVisibility(View.INVISIBLE);
            if(s!=null && !s.equals(" "))
            {
                searchResult.setText(s);
               ShowJSONData();
            }
            else {
                errorView.setText("Failed to load data");
                ShowError();
           }
        }



    }
}