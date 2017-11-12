package checknrcformat.android.example.com.cinemaonline.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import checknrcformat.android.example.com.cinemaonline.MainActivity;
import checknrcformat.android.example.com.cinemaonline.R;

public class RegisterActivity extends AppCompatActivity {
    EditText username, password, email,confirm_Edit;
    String user, pass, emai, confirm;
    TextView register;
    FirebaseAuth auth;
    Uri userUri;
    ImageView userImage;
    ProgressDialog pd;

    private StorageReference mStorageRef;

    public static int USERPROFILE_UPLOAD = 12345;
    public static String STORAGE_PATH = "Users";

    String storageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.name_edit);
        password = (EditText) findViewById(R.id.password_edit);
        confirm_Edit = (EditText) findViewById(R.id.confirm_edit);
        email = (EditText) findViewById(R.id.email_edit);

        register = (TextView) findViewById(R.id.txt_signup);

        Firebase.setAndroidContext(this);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = username.getText().toString();
                pass = password.getText().toString();
                emai = email.getText().toString();
                if (user.equals("")) {
                    username.setError("can't be blank");
                } else if (pass.equals("")) {
                    password.setError("can't be blank");
                } else if (!user.matches("[A-Za-z0-9]+")) {
                    username.setError("only alphabet or number allowed");
                } else if (user.length() < 5) {
                    username.setError("at least 5 characters long");
                } else if (pass.length() < 5) {
                    password.setError("at least 5 characters long");
                } else {
                    //
                    pd = new ProgressDialog(RegisterActivity.this);
                    pd.setMessage("Loading...");
                    pd.show();


                    auth.createUserWithEmailAndPassword(emai, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Authentication Success!" + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    });

                    String url = "https://onlinemovies-d27ab.firebaseio.com/uploading/users.json";

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            Firebase reference = new Firebase("https://onlinemovies-d27ab.firebaseio.com/uploading/users");
                            Map<String, String> map = new HashMap<>();
                            map.put("username", username.getText().toString());
                            map.put("email", emai);
                            map.put("password", pass);
                            if (s.equals("null")) {
                                //add firebase database at myFirebaseproject/messages/user
                                //user and his child password (this set password as a vlaue of user

                                reference.child(user).setValue(map);

                            } else {
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if (!obj.has(user)) {
                                        //add firebase database myFirebaseproject/messages/user
                                        reference.child(user).setValue(map);
                                        Toast.makeText(getApplicationContext(), "registration successful", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    } else {
                                        Toast.makeText(getApplicationContext(), "username already exists", Toast.LENGTH_LONG).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            pd.dismiss();
                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError);
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(getApplicationContext());
                    rQueue.add(request);
                }
            }
        });

    }


}
