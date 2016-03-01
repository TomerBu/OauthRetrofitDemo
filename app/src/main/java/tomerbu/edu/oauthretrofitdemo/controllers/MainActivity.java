package tomerbu.edu.oauthretrofitdemo.controllers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tomerbu.edu.oauthretrofitdemo.R;
import tomerbu.edu.oauthretrofitdemo.api.GoogleProfileAPI;
import tomerbu.edu.oauthretrofitdemo.api.models.AccessTokenResponse;

public class MainActivity extends AppCompatActivity implements GoogleProfileAPI.OnAPIReady {


    // you should either define client id and secret as constants or in string resources
    private final String clientId = "387414573620-a5dbc02u4af3be0mrv5ll5ki94iaq317.apps.googleusercontent.com";
    private final String clientSecret = "l3FHjuvppIAsdNN6FCZ-0fiw";
    private final String redirectUri = "http://localhost";
    private final String scope = "https://www.googleapis.com/auth/plus.me";
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.profile)
    FloatingActionButton profile;

    private GoogleProfileAPI api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // the intent filter defined in AndroidManifest will handle the return from ACTION_VIEW intent
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(redirectUri)) {
            api = new GoogleProfileAPI(uri, this);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    public void onClick() {
    /*
      GET https://accounts.google.com/o/oauth2/v2/auth?
      scope=https://www.googleapis.com/auth/email&
      state=security_token%3D138r5719ru3e1%26url%3Dhttps://oa2cb.example.com/myHome&
      redirect_uri=https%3A%2F%2Fmyapp.example.com%2Fcallback&
      response_type=code&
      client_id=8127352506391.apps.googleusercontent.com&
      prompt=consent&
      include_granted_scopes=true
    */
        String token = getSharedPreferences("Oauth", MODE_PRIVATE).getString("token", null);
        if (token != null) {
            AccessTokenResponse accessTokenResponse = new Gson().fromJson(token, AccessTokenResponse.class);
            api = new GoogleProfileAPI(accessTokenResponse, this);
            return;
        }

        Uri uri = Uri.parse(GoogleProfileAPI.BASE_URL + "auth" + "?client_id=" + clientId + "&redirect_uri=" + redirectUri + "&scope=" + scope + "&response_type=code");
        Log.d("TomerBu", uri.toString());
        Intent intent = new Intent(
                Intent.ACTION_VIEW,
                uri);
        startActivity(intent);
    }

    @OnClick(R.id.profile)
    public void profile() {
        api.getProfile();
    }

    @Override
    public void onApiReady(AccessTokenResponse tokenResponse) {
        api.getProfile();
        Gson gson = new Gson();
        String jsonToken = gson.toJson(tokenResponse);
        getSharedPreferences("Oauth", MODE_PRIVATE).edit().putString("token", jsonToken).commit();
    }
}
