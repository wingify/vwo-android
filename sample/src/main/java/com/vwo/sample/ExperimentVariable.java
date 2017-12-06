package com.vwo.sample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.vwo.mobile.VWO;

public class ExperimentVariable extends AppCompatActivity {

    EditText price;
    TextView finalPrice;
    private TextView tvDiscount;
    private float discount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_variable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvDiscount = (TextView) findViewById(R.id.discount);
        price = (EditText) findViewById(R.id.amount);
        finalPrice = (TextView) findViewById(R.id.final_price);

        Object varPrice = VWO.getVariationForKey("discount", "20");

        discount = Float.parseFloat(varPrice.toString());

        tvDiscount.setText(String.valueOf(discount) + " %");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void calculate(View v) {

        String amount = price.getText().toString();

        if (amount.length() == 0) {
            Snackbar.make(v, "Enter valid amount", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        try {
            int amt = Integer.parseInt(amount);

            int newAmount = (int) (amt * discount / 100);

            finalPrice.setText("Rs " + newAmount);
            VWO.trackConversion("discountCalculated", newAmount);


        } catch (NumberFormatException e) {
            Snackbar.make(v, "Enter valid integer value", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }


    }

}
