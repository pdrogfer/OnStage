package com.pdrogfer.onstage.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.firebase_client.OnAuthenticationCompleted;
import com.pdrogfer.onstage.firebase_client.UserAuthFirebaseClient;
import com.pdrogfer.onstage.firebase_client.UserAuthSuperClient;
import com.pdrogfer.onstage.model.UserType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

// Using an Interface to receive updates from UserAuthFirebaseClient-UserAuthServerClient
public class RegisterActivity extends BaseActivity implements View.OnClickListener, OnAuthenticationCompleted {

    private static final String TAG = "RegisterActivity";

    private static final int INTENT_REQUEST_CAMERA = 1;
    private static final int INTENT_SELECT_FILE = 2;

    private EditText emailField, passwordField, nameField;
    private RadioGroup userTypeRadioGroup;
    private RadioButton userFanRadioButton, userMusicianRadioButton, userVenueRadioButton;
    private Button logInButton, registerButton;
    private CircleImageView userThumbnailImageView;
    private FloatingActionButton fabTakePicture;

    private String emailValue, passwordValue, artisticNameValue, userTypeValue;

    private UserAuthSuperClient userAuth;
    Context context;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        // do authentication using Firebase
        userAuth = UserAuthFirebaseClient.getInstance(this, this);
        context = this;

        // Views
        emailField = (EditText) findViewById(R.id.field_email);
        passwordField = (EditText) findViewById(R.id.field_password);
        nameField = (EditText) findViewById(R.id.field_name);
        userTypeRadioGroup = (RadioGroup) findViewById(R.id.radioGroupUserType);
        userFanRadioButton = (RadioButton) findViewById(R.id.radioButtonFan);
        userMusicianRadioButton = (RadioButton) findViewById(R.id.radioButtonMusician);
        userVenueRadioButton = (RadioButton) findViewById(R.id.radioButtonVenue);
        logInButton = (Button) findViewById(R.id.button_log_in);
        registerButton = (Button) findViewById(R.id.button_register);
        userThumbnailImageView = (CircleImageView) findViewById(R.id.profile_image);

        // Click listeners
        logInButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        setFabRegisterActivity();
        // TODO: Remove in production, this logs a test user, to skip sign in Activity
        // forTestingOnly();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setFabRegisterActivity() {
        fabTakePicture = (FloatingActionButton) findViewById(R.id.fab_photo_register);
        if (fabTakePicture != null) {
            fabTakePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogTakeOrPickImage();
                }
            });
        }
    }

    private void dialogTakeOrPickImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (options[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, INTENT_REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), INTENT_SELECT_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == INTENT_REQUEST_CAMERA)
                onCaptureImageResult(data);
        } else {
            // Ssome error or cancelled action?
            Log.i(TAG, "onActivityResult: ResultCode " + resultCode);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                Bitmap bitmaFromGallery = MediaStore.Images.Media.getBitmap(
                        getApplicationContext().getContentResolver(), data.getData());
                loadImageToThumbnail(bitmaFromGallery);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap bitmapFromCamera = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmapFromCamera.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadImageToThumbnail(bitmapFromCamera);
    }

    private void loadImageToThumbnail(Bitmap thumbnail) {
        userThumbnailImageView.setImageBitmap(thumbnail);
    }

    private void forTestingOnly() {
        // using Firebase
        userAuth.checkAuth();
        emailValue = "testuser@hotmail.com";
        passwordValue = "aaaaaa";
        artisticNameValue = "test user";
        userTypeValue = "MUSICIAN";
        logIn();
//         register();
    }

    @Override
    public void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

        // Check auth on Activity start
        //userAuth.checkAuth();

        // set default userTypeValue ?
        // userTypeValue = String.valueOf(UserType.FAN);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    private void logIn() {
        Log.d(Utils.LOG_IN, "LogInActivity");
        userAuth.signIn(emailValue, passwordValue, artisticNameValue, userTypeValue);
    }

    private void register() {
        Log.d(Utils.LOG_IN, "Register");
        userAuth.registerUser(emailValue, passwordValue, artisticNameValue, userTypeValue);
    }

    @Override
    public void onClick(View v) {
        emailValue = emailField.getText().toString();
        passwordValue = passwordField.getText().toString();
        artisticNameValue = nameField.getText().toString();
        if (!validateForm(emailValue, passwordValue, artisticNameValue, userTypeValue)) {
            return;
        }
        showProgressDialog();
        switch (v.getId()) {
            case R.id.button_log_in:
                logIn();
                break;
            case R.id.button_register:
                register();
                break;
        }
    }

    private boolean validateForm(String email, String password, String artisticName, String userType) {
        boolean result = true;
        if (TextUtils.isEmpty(email)) {
            emailField.setError(getString(R.string.field_required_warning));
            result = false;
        } else {
            emailField.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordField.setError(getString(R.string.field_required_warning));
            result = false;
        } else {
            passwordField.setError(null);
        }

        if (TextUtils.getTrimmedLength(password) < 6) {
            passwordField.setError("Too short, at least 6 characters");
            result = false;
        } else {
            passwordField.setError(null);
        }

        if (TextUtils.isEmpty(artisticName)) {
            nameField.setError(getString(R.string.field_required_warning));
            result = false;
        } else {
            nameField.setError(null);
        }
        // userTypeValue is set to FAN by default
        return result;
    }


    @Override
    public void onAuthenticationCompleted(Boolean success, String message) {
        hideProgressDialog();
        if (success) {
            startActivity(new Intent(RegisterActivity.this, GigsListActivity.class));
            finish();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    public void onRadioBtnClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.radioButtonFan:
                if (checked) {
                    userTypeValue = String.valueOf(UserType.FAN);
                    Log.i(Utils.TAG, "onRadioBtnClick: UserType: " + userTypeValue);
                }
                break;
            case R.id.radioButtonMusician:
                if (checked) {
                    userTypeValue = String.valueOf(UserType.MUSICIAN);
                    Log.i(Utils.TAG, "onRadioBtnClick: UserType: " + userTypeValue);
                }
                break;
            case R.id.radioButtonVenue:
                if (checked) {
                    userTypeValue = String.valueOf(UserType.VENUE);
                    Log.i(Utils.TAG, "onRadioBtnClick: UserType: " + userTypeValue);
                }
                break;
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Register Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
