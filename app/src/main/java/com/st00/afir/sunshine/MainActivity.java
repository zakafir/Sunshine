package com.st00.afir.sunshine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler,LoaderManager.LoaderCallbacks<String[]> {

    private static final int FORECAST_LOADER_ID = 0;
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


        int loaderId = FORECAST_LOADER_ID;
        LoaderManager.LoaderCallbacks<String[]> callback = MainActivity.this;
        Bundle bundleForLoader = null;
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);


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
            getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
            showWeatherDataView();
        } else {
            showWeatherDataView();
        }
    }

    private void showWeatherDataView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(String weatherForDay) {
        if(mToast != null){
            mToast.cancel();
        }
        Context context = this;
        Intent intent = new Intent(context,DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT,weatherForDay);
        startActivity(intent);
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {
            /* This String array will hold and help cache our weather data */
            String[] mWeatherData = null;

            @Override
            protected void onStartLoading() {
                if (mWeatherData != null) {
                    deliverResult(mWeatherData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public String[] loadInBackground() {

                try {
                    if (mSearchBoxEditText.getText() != null && !mSearchBoxEditText.getText().toString().equals("")) {
                        URL weatherRequestUrl = NetworkUtils.buildUrl(mSearchBoxEditText.getText().toString());

                    String jsonWeatherResponse = NetworkUtils
                            .getResponseFromHttpUrl(weatherRequestUrl);

                    String[] allDaysForcastWeather = NetworkUtils.getSimpleWeatherStringsFromJson(jsonWeatherResponse);

                    return allDaysForcastWeather;
                    }else{
                        return null;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(String[] data) {
                mWeatherData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mForecastAdapter.setWeatherData(data);
        if (null == data) {
            showErrorMessage();
        } else {
            showWeatherDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

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
