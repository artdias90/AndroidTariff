package com.oneunit.test.cj2;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Abbas on 11/24/2015.
 * Updated by Arthur on 09/05/2016
 */
public class WelcomeDialog extends Dialog {

    private Button button, buttonConfigure;
    private String tariffNames[];
    private Context context;

    private Config config;

    public WelcomeDialog(Context context) {
        super(context);
        this.context = context;
        try {
            this.tariffNames = new TariffHandler(context).getTariffNames();
            this.config = new Config(context);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.welcome_dialog);
        this.setCanceledOnTouchOutside(false);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.context,
                R.layout.support_simple_spinner_dropdown_item, this.tariffNames);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);


        this.button = (Button) findViewById(R.id.config_confirm_button);
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ArrayList<String> userOpts = new ArrayList<>();
                    EditText data = (EditText) findViewById(R.id.welcome_tarif_data);
                    EditText phone = (EditText) findViewById(R.id.welcome_tarif_phone);
                    EditText sms = (EditText) findViewById(R.id.welcome_tarif_sms);
                    EditText price = (EditText) findViewById(R.id.welcome_tarif_price);
                    if(data.getText().toString().trim().equals("")
                            || phone.getText().toString().trim().equals("")
                            || sms.getText().toString().trim().equals("")
                            || price.getText().toString().trim().equals("")) {
                        Toast.makeText(getContext(), "Please fill all fields.", Toast.LENGTH_LONG).show();
                    } else {
                        userOpts.add(data.getText().toString());
                        userOpts.add(phone.getText().toString());
                        userOpts.add(sms.getText().toString());
                        userOpts.add(price.getText().toString());
                        config.createConfigFile(userOpts);
                    }
                }
                catch (IOException e){
                }
                dismiss();
            }
        });
    }
}
