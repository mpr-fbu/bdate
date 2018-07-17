package io.github.rgdagir.mpr;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.parse.Parse;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApp extends Application {

//    private String parseAppId = this.getResources().getString(R.string.parseAppId);
//    private String parseServer = this.getResources().getString(R.string.parseServer);
    @Override
    public void onCreate() {
        super.onCreate();

        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Log.d("ParseApp", this.getString(R.string.parseAppId));
        Log.d("ParseApp", this.getString(R.string.parseServer));
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(this.getString(R.string.parseAppId)) // should correspond to APP_ID env variable
                .clientKey(null)  // set explicitly unless clientKey is explicitly configured on Parse server
                .clientBuilder(builder)
                .server(this.getString(R.string.parseServer)).build());
    }
}
