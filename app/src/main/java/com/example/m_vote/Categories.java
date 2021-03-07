package com.example.m_vote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Categories extends AppCompatActivity {


    ListView listView;
    TextView textView;
    String[] Categories =
            {
                    "President",
                    "School Representative",
                    "Non Resident Rep"
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        textView = (TextView) findViewById(R.id.textView);
        listView = (ListView) findViewById(R.id.listView);

        final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.main_category, R.id.textView, Categories);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(

                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if (position == 0)
                        {
                            Intent i = new Intent(getApplicationContext(),President.class);
                            startActivity(i);
                        }

                        if (position == 1)
                        {
                            Intent i = new Intent(getApplicationContext(),SchoolRep.class);
                            startActivity(i);
                        }

                        if (position == 2)
                        {
                            Intent i = new Intent(getApplicationContext(), NonResident.class);
                            startActivity(i);
                        }

                    }
                }
        );

    }
}
