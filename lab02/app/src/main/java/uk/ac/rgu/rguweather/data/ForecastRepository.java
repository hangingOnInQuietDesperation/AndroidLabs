package uk.ac.rgu.rguweather.data;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * This class provides the single point of truth in the app for {@link LocationForecast}s, and
 * will in the future deal with downloading, storing, and retrieving them.
 * @author  David Corsar
 */
public class ForecastRepository {

    /**
     * A field for how dates should be formatted before displaying to users
     * with the day of the month as a number, and the month as text
     */
    private static final String DATE_FORMAT = "dd MMM";

    /**
     * The Singleton instance for this repository
     */
    private static ForecastRepository INSTANCE;

    /**
     * The Context that the app is operating within
     */
    private Context context;

    /**
     * Gets the singleton {@link ForecastRepository} for use when managing {@link LocationForecast}s
     * in the app.
     * @return The {@link ForecastRepository} to be used for managing {@link LocationForecast}s in the app.
     */
    public static ForecastRepository getRepository(Context context){
        if (INSTANCE == null){
            synchronized (ForecastRepository.class) {
                if (INSTANCE == null)
                    INSTANCE = new ForecastRepository();
                    INSTANCE.context = context;
            }
        }
        return INSTANCE;
    }

    /**
     * Returns a {@link LocationForecast} for the given location name, generated at random
     * @param locationName The name of the location to return the LocationForecast for
     * @return a {@link LocationForecast} for today for the given location, with randomly generated temperature range.
     */
    public LocationForecast getForecastFor(String locationName){
        return getForecastFor(locationName, 0);
    }

    /**
     * Returns a {@link LocationForecast} for the given location name, generated at random.
     * @param locationName The name of the location to return the LocationForecast for.
     * @param  daysInTheFuture The number of days in the future to return a LocationForecast for.
     * @return a {@link LocationForecast} for the specified number of days in the future for the given location, with randomly generated temperature range.
     */
    public LocationForecast getForecastFor(String locationName, int daysInTheFuture){
        LocationForecast forecast = new LocationForecast();
        forecast.setLocationName(locationName);

        // create a random temperature range between 1 and 30
        Random random = new Random();
        int min = random.nextInt((20 -1) + 1) +1;
        int max = (int)(min * 1.5);
        forecast.setMinTemp(min);
        forecast.setMaxTemp(max);

        // note - as a SimpleDataFormat will be created every time this method is called,
        // this is not particularly good coding style.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        long time = System.currentTimeMillis();
        time += daysInTheFuture * (1000 * 60 * 60 * 24);

        forecast.setDate(sdf.format(new Date(time)));

        // again, not the best practice here - this could be improved
        RandomWeatherForecastGetter rwf = new RandomWeatherForecastGetter(context);
        forecast.setWeather(rwf.getWeather());

        return forecast;
    }

}
