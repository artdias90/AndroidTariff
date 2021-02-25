package com.oneunit.test.cj2;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Bishal on 10/20/2015.
 * * Updated by Arthur on 09.04.2016.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {

    TextView user_tarif_link;
    Spinner languageSpinner;
    ArrayAdapter<CharSequence> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment,container,false);

        user_tarif_link = (TextView) view.findViewById(R.id.edit_user_tarif);
        user_tarif_link.setOnClickListener(this);

        languageSpinner =(Spinner)view.findViewById(R.id.language_spinner);
        adapter = ArrayAdapter.createFromResource(getActivity(),R.array.language,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Language selection handling
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:

                        break;
                    case 1:
                        ((MainActivity) getActivity()).changeLocale("en");
                        break;
                    case 2:
                        ((MainActivity) getActivity()).changeLocale("de");
                        break;
                    case 3:
                        ((MainActivity) getActivity()).changeLocale("fr");
                        break;
                    case 4:
                        ((MainActivity) getActivity()).changeLocale("es");
                        break;
                    case 5:
                        ((MainActivity) getActivity()).changeLocale("ar ");
                        break;
                    default:
                        break;


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }


    // Open new activity from setting sto manage database
    @Override
    public void onClick(View v) {
        TextView textView = (TextView)v;
        String type = String.valueOf(textView.getTag());
        Intent intent;
        switch(type) {
            case "edit_user_tarif":
                intent = new Intent(getActivity(), UserStatsActivity.class);
                startActivity(intent);
        }


    }

}
