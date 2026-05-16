package com.example.realtimechatapplication.di;

import com.example.realtimechatapplication.api.ApiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Singleton
    @Provides
    public HttpLoggingInterceptor provideHttpLoggingInterceptor(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }
    @Singleton
    @Provides
    public OkHttpClient provideOkHttpClient(HttpLoggingInterceptor interceptor, TokenManager tokenManager){
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder();

                    String token = tokenManager.getToken();
                    if (token != null && !token.isEmpty()){
                        requestBuilder.header("Authorization", "Bearer " + token);
                    }
                    return chain.proceed(requestBuilder.build());
                })
                .build();
    }
    @Singleton
    @Provides
    public Retrofit provideRetrofit( OkHttpClient client){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit;
    }
    @Singleton
    @Provides
    public ApiService provideApiService(Retrofit client){
        ApiService service = client.create(ApiService.class);
        return  service;
    }

}
