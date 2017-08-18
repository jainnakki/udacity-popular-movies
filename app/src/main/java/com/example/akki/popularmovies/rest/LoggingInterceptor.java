package com.example.akki.popularmovies.rest;

import android.util.Log;

import java.io.IOException;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by Akshay on 24-07-2017.
 */

public class LoggingInterceptor implements Interceptor {
    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {

        HttpUrl url = chain.request().url()
                .newBuilder()
                .addQueryParameter("api_key", "72b6dbc52bed1237a8eabba476078bc2")
                .build();

        Request request = chain.request().newBuilder().url(url).build();


        long t1 = System.nanoTime();
        String requestLog = String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers());

        if (request.method().compareToIgnoreCase("post") == 0) {
            requestLog = "\n" + requestLog + "\n" + bodyToString(request);
        }
        Log.d("TAG", "request" + "\n" + requestLog);

        okhttp3.Response response = chain.proceed(request);
        long t2 = System.nanoTime();

        String responseLog = String.format(Locale.US, "Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers());

        String bodyString = response.body().string();

        Log.d("TAG", "response" + "\n" + responseLog + "\n" + bodyString);

        return response.newBuilder()
                .body(ResponseBody.create(response.body().contentType(), bodyString))
                .build();
    }

    private static String bodyToString(final Request request) {

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            if (copy != null && copy.body() != null) // make sure its not null to avoif NPE
                copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}