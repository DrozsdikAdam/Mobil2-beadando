package com.example.realtimechatapplication.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static Retrofit retrofit = null;
    private static String authToken = null;

    public static void setAuthToken(String token) {
        authToken = token;
        // Ha változik a token, újra kell építeni a Retrofitet, hogy az új klienst használja
        retrofit = null;
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static ApiService getApiService() {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor);

            clientBuilder.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();

                if (authToken != null && !authToken.isEmpty()) {
                    requestBuilder.header("Authorization", "Bearer " + authToken);
                }

                Request request = requestBuilder.build();
                return chain.proceed(request);
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(clientBuilder.build())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
