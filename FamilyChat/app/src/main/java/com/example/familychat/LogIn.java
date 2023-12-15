package com.example.familychat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.familychat.model.UserContext;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class LogIn extends AppCompatActivity {
    String message;
    String token;
    EditText usernameInput,passwordInput;
    ProgressBar progressBar;
    Button logInBtn,forgetPassBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameInput = findViewById(R.id.login_username);
        passwordInput = findViewById(R.id.login_password);
        logInBtn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        forgetPassBtn = findViewById(R.id.login_forget_password_btn);
        setInProgress(false);

        logInBtn.setOnClickListener(new View.OnClickListener(){
            public  void onClick(View v){
                setInProgress(true);
                IsValidUser(usernameInput.getText().toString(), passwordInput.getText().toString());

            }
        });
    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            logInBtn.setVisibility(View.GONE);
            forgetPassBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            logInBtn.setVisibility(View.VISIBLE);
            forgetPassBtn.setVisibility(View.VISIBLE);
        }
    }
    public void IsValidUser(String username, String password) {

        try {
            String baseUrl = "http://familychat.somee.com/FamilyChat/UserLogIn";
            String url = baseUrl + "?UserName=" + username + "&Password=" + password;



            RequestQueue queue = Volley.newRequestQueue(this);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                message = response.getString("message");
                                token = response.getString("token");
                                Toast.makeText(LogIn.this,  message, Toast.LENGTH_SHORT).show();
                                UserContext user;
                                if(username.equals("Iftekhar")){
                                    user = new UserContext();
                                    user.IsUser = true;
                                    user.UserId = 1;
                                    user.name = "Iftekhar";
                                    user.connectionId = "";
                                }else {
                                    user = new UserContext();
                                    user.IsUser = true;
                                    user.UserId = 2;
                                    user.name = "Ifty";
                                    user.connectionId = "";
                                }
                                Intent home = new Intent(LogIn.this, Home.class);
                                home.putExtra("user", user);
                                startActivity(home);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LogIn.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                    setInProgress(false);
                                }
                            });
                        }
                    });
            queue.add(jsonObjectRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void buttonColorChange(View v,Button colorButton){
        int color = Color.parseColor("#e74c3c");
        colorButton.setBackgroundColor(color);

        // Delay to reset color after 500 milliseconds (0.5 seconds)
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                int originalColor = Color.parseColor("#3498db");
                colorButton.setBackgroundColor(originalColor);
            }
        }, 500);
    }

}
