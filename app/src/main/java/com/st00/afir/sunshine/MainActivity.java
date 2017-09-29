package com.st00.afir.sunshine;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.st00.afir.sunshine.utilities.ForecastAdapter;
import com.st00.afir.sunshine.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;


    private TextView mErrorMessageTextView;

    private ProgressBar mLoadingIndicator;

    private EditText mSearchBoxEditText;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_fetching);
        mErrorMessageTextView = (TextView) findViewById(R.id.error_msg);


        mSearchBoxEditText = (EditText) findViewById(R.id.et_search_box);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);
        mForecastAdapter = new ForecastAdapter(this);
        mRecyclerView.setAdapter(mForecastAdapter);
        loadWeatherData();


    }


    private void loadWeatherData() {
        if (mSearchBoxEditText.getText() != null && !mSearchBoxEditText.getText().toString().equals("")) {
            URL buildUrl = NetworkUtils.buildUrl(mSearchBoxEditText.getText().toString());
            showWeatherDataView();
            new FetchWeatherTask().execute(buildUrl);
        } else {
            showWeatherDataView();
        }
    }

    private void showWeatherDataView() {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(String weatherForDay) {
        if(mToast != null){
            mToast.cancel();
        }
        Context context = this;
        mToast = Toast.makeText(context, weatherForDay, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public class FetchWeatherTask extends AsyncTask<URL, Void, String[]> {

        @Override
        protected void onPreExecute() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(URL... params) {

            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            URL weatherRequestUrl = params[0];

            try {
                String jsonWeatherResponse = NetworkUtils
                        .getResponseFromHttpUrl(weatherRequestUrl);

                String[] allDaysForcastWeather = NetworkUtils.getSimpleWeatherStringsFromJson(jsonWeatherResponse);

                return allDaysForcastWeather;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] weatherData) {
            if (weatherData != null) {
                showWeatherDataView();
                mForecastAdapter.setWeatherData(weatherData);
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                loadWeatherData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
