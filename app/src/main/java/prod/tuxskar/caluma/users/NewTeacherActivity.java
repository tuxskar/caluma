package prod.tuxskar.caluma.users;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import prod.tuxskar.caluma.R;
import prod.tuxskar.caluma.SharedDB;
import prod.tuxskar.caluma.TeacherHomeActivity;
import prod.tuxskar.caluma.ws.WSErrorHandler;
import prod.tuxskar.caluma.ws.WSHandler;
import prod.tuxskar.caluma.ws.models.users.Teacher;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewTeacherActivity extends Activity {

    static SharedDB sharedDB;
    private EditText name = null;
    private EditText last_name = null;
    private EditText email = null;
    private EditText username = null;
    private EditText password = null;
    private EditText dept = null;
    private EditText description = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_teacher_form);
        name = (EditText) findViewById(R.id.editName);
        last_name = (EditText) findViewById(R.id.editLastName);
        email = (EditText) findViewById(R.id.editEmail);
        username = (EditText) findViewById(R.id.editUsername);
        password = (EditText) findViewById(R.id.editPassword);
        dept = (EditText) findViewById(R.id.editDept);
        description = (EditText) findViewById(R.id.editDescription);
        sharedDB = new SharedDB(this.getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    public void createNewTeacher(View view) {
        String username = this.username.getText().toString();
        if (username.length() > 0 && password.getText().toString().length() > 0) {
            Toast.makeText(getApplicationContext(), "Creating new user",
                    Toast.LENGTH_SHORT).show();
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(WSHandler.SERVICE_ENDPOINT)
                    .setErrorHandler(new WSErrorHandler()).build();
            WSHandler service = restAdapter.create(WSHandler.class);
            sharedDB.putString(getString(R.string.userUsername), username);
            service.createNewUser(
                    new Teacher(this.username.getText().toString(), password
                            .getText().toString(), name.getText().toString(),
                            last_name.getText().toString(), email.getText()
                            .toString(), dept.getText().toString(),
                            description.getText().toString()),
                    new Callback<LoggedIn>() {
                        @Override
                        public void failure(RetrofitError arg0) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Something went Wrong, try again with other username",
                                    Toast.LENGTH_SHORT).show();
                            sharedDB.putString(getString(R.string.userUsername), "");
                        }

                        @Override
                        public void success(LoggedIn arg0, Response arg1) {
                            changeToHomeActivity(arg0);
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(),
                    "At least username and password field must be filled",
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
        sharedDB.putString(getString(R.string.userRole), response.getRole() == null ? response.getRole() : "TEAC");
        sharedDB.putBoolean("userLoggedInState", true);
        goToHomeActivity();
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(this, TeacherHomeActivity.class);
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
