package org.rahul.stethoapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Register_Online extends AppCompatActivity {


    EditText DOB,emailid,pwd,confpwd,contactno,nameofsignup,SKey;
    Spinner genderSpinner;
    Button submit;
    EditText AddressP,DateP,TimeP;
    Switch AllocSwitch;
    AlertDialog.Builder builder;
    Switch DocAllocSwitch;
    String server_url = "http://10.14.79.58/dashboard/examples/android_online_register.php";
    String Epione = "0",Stethos="1",patientID,patientName,patientAddress,patientDOB,patientEmail,patientPassword,patientGender,patientBG,patientAllocKit,patientAllocTime,patientAllocDate,patientContactNo,patientDocAssg,patientskey;

    int allot=0,docallot=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        DOB = (EditText)findViewById(R.id.dobsignup);
        emailid = (EditText)findViewById(R.id.emailsignup);
        nameofsignup = (EditText)findViewById(R.id.namesignup);
        pwd = (EditText)findViewById(R.id.pwdsignup);
        confpwd = (EditText)findViewById(R.id.cnfpwdsignup);
        submit = (Button)findViewById(R.id.submit);
        contactno = (EditText)findViewById(R.id.contactsignup);
        genderSpinner = (Spinner)findViewById(R.id.gendersignup);
        AddressP = (EditText)findViewById(R.id.address1);
        SKey = (EditText)findViewById(R.id.skey);

        builder = new AlertDialog.Builder(Register_Online.this);

        final Spinner bloodgrp = findViewById(R.id.bloodgrp);
        String[] items = new String[]{"A+ve", "A-ve", "B+ve","B-","AB+ve","AB-ve","O+ve","O-ve"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        bloodgrp.setAdapter(adapter);

        Calendar cal = Calendar.getInstance();
        String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
        String month = Integer.toString(cal.get(Calendar.MONTH));
        String year = Integer.toString(cal.get(Calendar.YEAR));
        String hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
        String min = Integer.toString(cal.get(Calendar.MINUTE));

        final String time = hour+":"+min;
        final String date = day+"-"+month+"-"+year;

        AllocSwitch = (Switch)findViewById(R.id.kitallocswitch);
        DocAllocSwitch = (Switch) findViewById(R.id.docallocswitch);
        DateP = (EditText)findViewById(R.id.dateofalloc);
        TimeP = (EditText)findViewById(R.id.timeofalloc);
        final String[] KitAlloc = {"0"};

        AllocSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    allot=1;
                } else {
                    allot=0;
                }
            }
        });


        DocAllocSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    docallot=1;
                } else {
                    docallot=0;
                }
            }
        });


        String[] gender = new String[]{"Male", "Female", "Transgender","Others"};
        ArrayAdapter<String> gender_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, gender);
        genderSpinner.setAdapter(gender_adapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                patientID="";
                patientName = nameofsignup.getText().toString().trim();
                patientAddress = AddressP.getText().toString().trim();
                patientDOB = DOB.getText().toString().trim();
                patientEmail = emailid.getText().toString().trim();
                patientPassword = pwd.getText().toString().trim();
                patientGender = genderSpinner.getSelectedItem().toString().trim();
                patientBG = bloodgrp.getSelectedItem().toString().trim();
                patientAllocKit = Integer.toString(allot);
                patientAllocTime = time;
                patientAllocDate = date;
                patientName = nameofsignup.getText().toString().trim();
                patientContactNo = contactno.getText().toString().trim();
                patientDocAssg = Integer.toString(docallot);
                patientskey = SKey.getText().toString().trim();


                StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                builder.setTitle("Server Response");
                                builder.setMessage("Response:"+response);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        Intent intent = new Intent(Register_Online.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        }
                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(Register_Online.this,"Error...",Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String, String>();


                        params.put("EpiOne",Epione);
                        params.put("Stethoscope",Stethos);
                        params.put("PID",patientID);
                        params.put("Pname",patientName);
                        params.put("Paddress",patientAddress);
                        params.put("Pdob",patientDOB);
                        params.put("Email",patientEmail);
                        params.put("Password",patientPassword);
                        params.put("Gender",patientGender);
                        params.put("BloodGroup",patientBG);
                        params.put("Allocated",patientAllocKit);
                        params.put("Alloc_date",patientAllocDate);
                        params.put("Alloc_time",patientAllocTime);
                        params.put("ContactNo",patientContactNo);
                        params.put("Hashkey","");
                        params.put("doctor_assigned",patientDocAssg);
                        params.put("s_key",patientskey);

                        return params;
                    }
                };

                MySingleton.getInstance(Register_Online.this).addToRequestQueue(stringRequest);




            }
        });
    }
}

