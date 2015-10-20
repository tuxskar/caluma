package prod.tuxskar.caluma;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import prod.tuxskar.caluma.gcm.RegisteringGcmActivity;
import prod.tuxskar.caluma.users.LoggedIn;
import prod.tuxskar.caluma.users.NewUserActivity;
import prod.tuxskar.caluma.ws.WSErrorHandler;
import prod.tuxskar.caluma.ws.WSHandler;
import prod.tuxskar.caluma.ws.models.users.LoginUser;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class LoginActivity extends Activity {
    static SharedDB sharedDB;
    static WSHandler service;
    static RequestInterceptor requestInterceptor;
    static private Context context;
    private EditText username = null;
    private EditText password = null;

    static public WSHandler getUserService(Context param_context) {
        if (service == null) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            if (sharedDB == null) {
                sharedDB = new SharedDB(param_context != null ? param_context : context);
            }
            if (context == null && param_context != null) {
                context = param_context;
            }
            requestInterceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    String token = sharedDB.getString(context.getString(R.string.userToken));
                    request.addHeader("Authorization", " Token " + token);
                    request.addHeader("WWW-Authenticate", " Token");
                }
            };
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(WSHandler.SERVICE_ENDPOINT)
                    .setErrorHandler(new WSErrorHandler())
                    .setRequestInterceptor(requestInterceptor)
                    .setConverter(new GsonConverter(gson)).build();
            service = restAdapter.create(WSHandler.class);
        }
        return service;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.editUsername);
        password = (EditText) findViewById(R.id.editPassword);
        sharedDB = new SharedDB(this.getApplicationContext());
        context = getApplicationContext();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    public void login(View view) {
        if (username.getText().toString().length() > 0
                && password.getText().toString().length() > 0) {
            Toast.makeText(getApplicationContext(), "Checking...",
                    Toast.LENGTH_SHORT).show();
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(WSHandler.SERVICE_ENDPOINT)
                    .setErrorHandler(new WSErrorHandler()).build();
            WSHandler service = restAdapter.create(WSHandler.class);
            sharedDB.putString(context.getString(R.string.userUsername),
                    username.getText().toString());
            service.getUserToken(new LoginUser(username.getText().toString(), password
                    .getText().toString()), new Callback<LoggedIn>() {
                @Override
                public void failure(RetrofitError arg0) {
                    Toast.makeText(getApplicationContext(),
                            "Wrong Credentials", Toast.LENGTH_SHORT).show();
                    sharedDB.putString(context.getString(R.string.userUsername), "");
                }

                @Override
                public void success(LoggedIn arg0, Response arg1) {
                    changeToHomeActivity(arg0);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(),
                    "Username and password field must be filled",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void newUser(View view) {
        Intent intent = new Intent(this, NewUserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }

    public void changeToHomeActivity(LoggedIn response) {
        sharedDB.putString(getString(R.string.userToken), response.getToken());
        sharedDB.putString(getString(R.string.userRole), response.getRole());
        sharedDB.putBoolean("userLoggedInState", true);
        getUserService(null);
        goToRegisteringActivity();
    }

    public void goToRegisteringActivity() {
        Intent intent = new Intent(this, RegisteringGcmActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        boolean isUserLoggedIn = sharedDB.getBoolean("userLoggedInState");
        if (isUserLoggedIn) {
            goToRegisteringActivity();
        }
        super.onResume();
    }

}
