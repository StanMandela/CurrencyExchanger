package com.example.currencyexchanger;

public class ActivityItem {
    private String currencyFrom;
    private String currencyTo;

    public ActivityItem(String currency_From, String currency_To){
        currencyFrom= currency_From;
                currencyTo=currency_To;
    }
    public String getCurrencyFrom(){
        return currencyFrom;
    }
    public String getCurrencyTo(){
        return currencyTo;
    }



}
