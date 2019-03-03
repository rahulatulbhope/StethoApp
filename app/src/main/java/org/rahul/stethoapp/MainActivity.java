package org.rahul.stethoapp;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText Login,Pass;
    AlertDialog.Builder builder;
    String server_url = "http://10.14.79.58/login_android.php";

    String emailID,passWord;
    Button login_now,sign_now,guest_now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Login = (EditText)findViewById(R.id.email);
        Pass = (EditText)findViewById(R.id.passw);
        login_now = (Button)findViewById(R.id.login);
        sign_now = (Button)findViewById(R.id.signup);
        guest_now=(Button)findViewById(R.id.guest);

        login_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emailID = Login.getText().toString().trim();
                passWord = Pass.getText().toString().trim();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {



                                if(response.trim().length()>0)
                                {
                                    //openProfile();
                                    Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(MainActivity.this,Fetch_Audio_Online.class);
                                    i.putExtra("Hashkey",response.trim());
                                    startActivity(i);
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(MainActivity.this,"Error...",Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String, String>();

                        params.put("Email", emailID);
                        params.put("Password", passWord);
                        return params;
                    }
                };

                MySingleton.getInstance(MainActivity.this).addToRequestQueue(stringRequest);

            }
        });

        guest_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this,CommunicateWithPi.class);
                startActivity(i);



            }
        });

        sign_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this,Register_Online.class);
                startActivity(i);

            }
        });
    }


}
