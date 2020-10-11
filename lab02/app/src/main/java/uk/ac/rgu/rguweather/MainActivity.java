package uk.ac.rgu.rguweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import uk.ac.rgu.rguweather.data.*;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //make some initial variable
    private static final String TAG = "WEATHER_TEST";
    private static int sbProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create array of autofill Locations and 'adapt' it to work in a View
        //just a hardcoded array currently --fix at some point
        AutoCompleteTextView actvLocation = findViewById(R.id.actvLocation);
        String[] locationList = {"Aberdeen", "Dundee", "Edinburgh", "Glasgow", "Inverness", "Inverbervie"};
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList);
        actvLocation.setAdapter(adapter);
        actvLocation.setThreshold(1);//how many characters before giving suggestions

        // Add on click listener to the get forecast button
        Button btnGetForecast = findViewById(R.id.btn_get_forecast);
        btnGetForecast.setOnClickListener(this);

        //Create the seek bar and listener for future forecasts
        final SeekBar sbFuture = (SeekBar) findViewById(R.id.sbFuture);
        sbFuture.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //if functionality is required for when the user starts using the scrollbar
                //it goes here
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //when the user lets go of the seek bar record where it it
                sbProgress = sbFuture.getProgress();
                // then reload the view for that many days in the future
                makeForecast(sbProgress);
                //toast to tell the user how many days in the future
                Toast.makeText(getApplicationContext(), String.valueOf(sbProgress) + "Day(s)", Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onClick(View view) {
        //on clicking the "get forecast" button
        if (view.getId() == R.id.btn_get_forecast) {
            //make the weather view based on where the scroll bar is
            makeForecast(sbProgress);
        }
    }

    public void makeForecast(int progress) {
        //---Initialise

        Resources res = getResources();
        //Create icons as Drawable objects
        Drawable sunIcon = ResourcesCompat.getDrawable(res, R.drawable.sun, null);
        Drawable snowIcon = ResourcesCompat.getDrawable(res, R.drawable.snow, null);
        Drawable rainIcon = ResourcesCompat.getDrawable(res, R.drawable.rain, null);
        Drawable windIcon = ResourcesCompat.getDrawable(res, R.drawable.wind, null);

        //init various views
        AutoCompleteTextView actvLocation = findViewById(R.id.actvLocation);
        TextView tvHeader = findViewById(R.id.tvHeader);
        TextView tvMinTempValue = findViewById(R.id.tvMinTempValue);
        TextView tvMaxTempValue = findViewById(R.id.tvMaxTempValue);
        TextView tvForecastValue = findViewById(R.id.tvForecastValue);
        ImageView ivWeather = findViewById(R.id.ivWeather);
        //Create Forecast Repository
        ForecastRepository fr = ForecastRepository.getRepository(getApplicationContext());

        //---Create forecast
        //get location from user input
        final String chosenLocation = actvLocation.getText().toString();
        //create forecast for that location for the day in the future given by the
        //progress of the scroll bar.
        LocationForecast forecast = fr.getForecastFor(chosenLocation, sbProgress);
        //get results and format into strings to pass into TextViews
        String headerString = getString(R.string.tvHeaderText, forecast.getLocationName(), forecast.getDate());
        String minString = getString(R.string.tvMinTempValue, forecast.getMinTemp());
        String maxString = getString(R.string.tvMaxTempValue, forecast.getMaxTemp());
        String forecastValueString = forecast.getWeather();

        //---update ui
        //HeaderText
        tvHeader.setText(headerString);
        //Minimum Temperature
        tvMinTempValue.setText(minString);
        //Maximum Temperature
        tvMaxTempValue.setText(maxString);
        //weather
        switch (forecastValueString) {
            case "sunny":
                ivWeather.setImageDrawable(sunIcon);
                break;
            case "rain":
                ivWeather.setImageDrawable(rainIcon);
                break;
            case "snow":
                ivWeather.setImageDrawable(snowIcon);
                break;
            case "windy":
                ivWeather.setImageDrawable(windIcon);
                break;
        }
        tvForecastValue.setText(forecastValueString);

        //Add a toggle button to bookmark locations
        ToggleButton tbBookmark = findViewById(R.id.tbBookmark);
        //add a listener for the toggle button
        tbBookmark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                //if clicked then log and toast the user
                if (b) {
                    Log.d(TAG, "User has bookmarked the location " + chosenLocation);
                    Toast.makeText(getApplicationContext(), chosenLocation + " Bookmarked", Toast.LENGTH_SHORT).show();
                }
                //if un-clicked then log and toast the user
                else {
                    Log.d(TAG, "User has removed " + chosenLocation + " from bookmarks");
                    Toast.makeText(getApplicationContext(), chosenLocation + " Un-Bookmarked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

