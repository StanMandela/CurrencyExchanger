package com.example.currencyexchanger.Retrofit;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitInterface {
    @GET("v4/latest/{currency}")
    Call<JSONObject> getExchangeCurrency(@Path("currency") String currency);
}
