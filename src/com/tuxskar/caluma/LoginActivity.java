package com.tuxskar.caluma;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tuxskar.caluma.ws.WSErrorHandler;
import com.tuxskar.caluma.ws.WSHandler;
import com.tuxskar.caluma.ws.models.Token;
import com.tuxskar.caluma.ws.models.User;

public class LoginActivity extends Activity {
	private EditText username = null;
	private EditText password = null;
	static SharedDB sharedDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		username = (EditText) findViewById(R.id.editUsername);
		password = (EditText) findViewById(R.id.editPassword);
		username.setText("demo");
		password.setText("demo");
		sharedDB = new SharedDB(this.getApplicationContext());
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
			service.getUserToken(new User(username.getText().toString(), password
					.getText().toString()), new Callback<Token>() {
				@Override
				public void failure(RetrofitError arg0) {
					Toast.makeText(getApplicationContext(),
							"Wrong Credentials", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void success(Token arg0, Response arg1) {
					changeToHomeActivity(arg0);
				}
			});
		} else {
			Toast.makeText(getApplicationContext(),
					"Username and password field must be filled",
					Toast.LENGTH_SHORT).show();
		}

	}
	
	public void changeToHomeActivity(Token token){
		sharedDB.putString(getString(R.string.userToken), token.getToken());
		sharedDB.putBoolean("userLoggedInState", true);
		goToHomeActivity();
	}
	
	private void goToHomeActivity(){
		Intent intent = new Intent(this, HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onResume() {
		boolean isUserLoggedIn = sharedDB.getBoolean("userLoggedInState");
		if (isUserLoggedIn) {
			goToHomeActivity();
		}
		super.onResume();
	}

}
