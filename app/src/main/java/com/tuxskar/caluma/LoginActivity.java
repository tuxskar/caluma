package com.tuxskar.caluma;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tuxskar.caluma.gcm.RegisteringGcmActivity;
import com.tuxskar.caluma.users.LoggedIn;
import com.tuxskar.caluma.users.NewUserActivity;
import com.tuxskar.caluma.ws.WSErrorHandler;
import com.tuxskar.caluma.ws.WSHandler;
import com.tuxskar.caluma.ws.models.users.LoginUser;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
//		username.setText("demo");
//		password.setText("demo");
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
			service.getUserToken(new LoginUser(username.getText().toString(), password
					.getText().toString()), new Callback<LoggedIn>() {
				@Override
				public void failure(RetrofitError arg0) {
					Toast.makeText(getApplicationContext(),
							"Wrong Credentials", Toast.LENGTH_SHORT).show();
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
	
	
	public void changeToHomeActivity(LoggedIn response){
		sharedDB.putString(getString(R.string.userToken), response.getToken());
		sharedDB.putString(getString(R.string.userRole), response.getRole());
		sharedDB.putBoolean("userLoggedInState", true);
		goToRegisteringActivity();
	}
	
	public void goToHomeActivity(){
		Intent intent = new Intent(this, sharedDB.getString(
				getString(R.string.userRole)).compareTo("TEAC") == 0 ? TeacherHomeActivity.class : StudentHomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}
	
	public void goToRegisteringActivity(){
		Intent intent = new Intent(this, RegisteringGcmActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
//		RegisteringGdmActivity
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
