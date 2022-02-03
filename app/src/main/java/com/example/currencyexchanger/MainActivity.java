package com.example.currencyexchanger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import kotlin.math.UMathKt;
import okhttp3.MediaType;

public class MainActivity extends AppCompatActivity {
    public String TAG = "ordersBOT";
    Button convertButton;
    Dialog fromDialog;
    ListView listView;
    Dialog toDialog;
    TextView convertFromDropdownTextView,conversionRateTextView,convertToDropdownTextView;
    EditText amountToConvert;
    String convertFromValue,convertToValue,conversionValue,oneTra;
    String []country ={"USD","KES","TZS", "EUR","AUD"};
    ArrayList<String> arrayList,transactions;
    private RecyclerView.LayoutManager mlayoutManager;
    private RecyclerView mRecylerview;
    ArrayAdapter<String> adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        convertFromDropdownTextView= findViewById(R.id.convert_from_dropdown_menu);
        convertToDropdownTextView = findViewById(R.id.convert_to_dropdown_menu);
        convertButton = findViewById(R.id.conversionButton);
        conversionRateTextView = findViewById(R.id.conversion_rate_text);
        amountToConvert =findViewById(R.id.amountToConvert);
        listView = findViewById(R.id.listView);

        arrayList= new ArrayList<>();
        transactions = new ArrayList<>();
        transactions.add("No new items");

        adapter = new ArrayAdapter<>(getApplicationContext() , android.R.layout.simple_list_item_1,transactions);

        listView.setAdapter(adapter);

        for(String i :country){
            arrayList.add(i);

        }
        convertFromDropdownTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDialog = new Dialog(MainActivity.this);
                fromDialog.setContentView(R.layout.from_spinner);
                fromDialog.getWindow().setLayout(650,800);
                fromDialog.show();

                EditText editText = fromDialog.findViewById(R.id.edite_text);
                ListView listView= fromDialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,arrayList);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        adapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        convertFromDropdownTextView.setText(adapter.getItem(i));
                        fromDialog.dismiss();
                        convertFromValue = adapter.getItem(i);

                    }
                });
            }
        });

        convertToDropdownTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDialog = new Dialog(MainActivity.this);
                toDialog.setContentView(R.layout.to_spinner);
                toDialog.getWindow().setLayout(650,800);
                toDialog.show();
                EditText editText = toDialog.findViewById(R.id.edite_text);
                ListView listView =toDialog.findViewById(R.id.list_view);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,
                        arrayList);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        adapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        convertToDropdownTextView.setText(adapter.getItem(i));
                        toDialog.dismiss();
                        convertToValue = adapter.getItem(i);

                    }
                });
                }
        });
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{

                    Double amountToConvert = Double.valueOf(MainActivity.this.amountToConvert.getText().toString());
                    getConversionRate(convertFromValue,convertToValue,amountToConvert);
                    addTransaction(convertFromValue,convertToValue);
                }catch ( Exception e){

                }
            }
        });

    }

    private void addTransaction(String convertFromValue, String convertToValue) {
        oneTra="Converted "+ convertFromValue +" to "+ convertToValue;
        transactions.add(oneTra);
        listView.setAdapter(adapter);
        Toast.makeText(MainActivity.this, "Added Item", Toast.LENGTH_LONG).show();

    }


    public String getConversionRate(String convertFromValue, String convertToValue, Double amountToConvert) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://free.currconv.com/api/v7/convert?q="+convertFromValue+"_"+convertToValue+"&compact=ultra&apiKey=bbb0b408149ebaf7290b";
        StringRequest request= new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    Log.e(TAG, "Event: "+jsonObject);

                    Double conversionRateValue = round(((Double) jsonObject.get(convertFromValue + "_" + convertToValue)), 2);
                    conversionValue = ""+ round((conversionRateValue* amountToConvert),2);
                    conversionRateTextView.setText(conversionValue);

                  //  saveDataToList(convertFromValue,convertToValue);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
     return null;
    }




    public static double round(Double value, int places) {
        if(places<0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd= bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }



}
