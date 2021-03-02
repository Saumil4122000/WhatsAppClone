package com.example.geeksproject.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.geeksproject.MainActivity;
import com.example.geeksproject.R;
import com.example.geeksproject.VerifyPhone;

public class PhoneLoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    EditText mob_no;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mob_no = (EditText) findViewById(R.id.editTextPhone);
        submit = (Button) findViewById(R.id.button);
        submit.setOnClickListener(v -> {
            String phoneNo = mob_no.getText().toString();
            Intent intent = new Intent(getApplicationContext(), VerifyPhone.class);
            intent.putExtra("phoneNo",phoneNo);
            startActivity(intent);
        });
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
