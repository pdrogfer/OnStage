package com.pdrogfer.onstage.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
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
import com.pdrogfer.onstage.database.Contract;
import com.pdrogfer.onstage.database.UsersContentProvider;
import com.pdrogfer.onstage.firebase_client.OnAuthenticationCompleted;
import com.pdrogfer.onstage.firebase_client.UserOperationsSuperClient;
import com.pdrogfer.onstage.firebase_client.UserRegServerClient;
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
    private Button cancelButton, registerButton;
    private CircleImageView userThumbnailImageView;
    private FloatingActionButton fabTakePicture;

    private String emailValue, passwordValue, artisticNameValue, userTypeValue;
    private int isUserActiveValue;
    private ProgressDialog regProgressDialog;

    private UserOperationsSuperClient userRegistration;
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
//        userRegistration = UserAuthFirebaseClient.getInstance(this, this);
//        context = this;

        // do authentication using Server
        userRegistration = UserRegServerClient.getInstance(this, this);
        context = this;

        // Views
        emailField = (EditText) findViewById(R.id.field_email);
        passwordField = (EditText) findViewById(R.id.field_password);
        nameField = (EditText) findViewById(R.id.field_name);
        userTypeRadioGroup = (RadioGroup) findViewById(R.id.radioGroupUserType);
        userFanRadioButton = (RadioButton) findViewById(R.id.radioButtonFan);
        userMusicianRadioButton = (RadioButton) findViewById(R.id.radioButtonMusician);
        userVenueRadioButton = (RadioButton) findViewById(R.id.radioButtonVenue);
        cancelButton = (Button) findViewById(R.id.btn_cancel_register);
        registerButton = (Button) findViewById(R.id.btn_register_register);
        userThumbnailImageView = (CircleImageView) findViewById(R.id.profile_image);

        // Click listeners
        cancelButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        setFabRegisterActivity();

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

    @Override
    public void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_register:
                startActivity(new Intent(RegisterActivity.this, Presentation.class));
                finish();
                break;
            case R.id.btn_register_register:

                // TODO: testing ONLY, remove in production
                emailField.setText(Utils.TEST_EMAIL);
                passwordField.setText(Utils.TEST_PASSWORD);
                nameField.setText(Utils.TEST_NAME);
                userTypeValue = String.valueOf(UserType.MUSICIAN);
                // ------- end testing block

                emailValue = emailField.getText().toString();
                passwordValue = passwordField.getText().toString();
                artisticNameValue = nameField.getText().toString();
                isUserActiveValue = 1;
                if (!validateForm(emailValue, passwordValue, artisticNameValue, userTypeValue)) {
                    return;
                }
                register();
                break;
        }
    }

    private void register() {
        showRegProgressDialog();
        Log.d(Utils.LOG_IN, "Register");
        userRegistration.registerUser(emailValue, passwordValue, artisticNameValue, userTypeValue);
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
        return result;
    }

    // this method works ok for both auth and reg cases
    @Override
    public void onAuthenticationCompleted(Boolean success, String name, String email, String password, String user_type) {
        hideRegProgressDialog();
        if (success) {
            // by default, new registered user is active, so 1
            isUserActiveValue = 1;
            insertUserToLocalDb(emailValue, passwordValue, artisticNameValue, userTypeValue, isUserActiveValue);
            startActivity(new Intent(RegisterActivity.this, GigsListActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Error registering user", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSignOut() {
        // do nothing here
    }

    private void insertUserToLocalDb(String emailValue, String passwordValue, String artisticNameValue, String userTypeValue, int isUserActive) {
        ContentValues values = new ContentValues();
        values.put(Contract.COLUMN_EMAIL, emailValue);
        values.put(Contract.COLUMN_PASSWORD, passwordValue);
        values.put(Contract.COLUMN_NAME, artisticNameValue);
        values.put(Contract.COLUMN_USER_TYPE, userTypeValue);
        values.put(Contract.COLUMN_USER_ACTIVE, isUserActive);
        Uri uri = getContentResolver().insert(UsersContentProvider.CONTENT_URI, values);
        Toast.makeText(this, uri.toString(), Toast.LENGTH_LONG).show();
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

    private void showRegProgressDialog() {
        regProgressDialog = new ProgressDialog(this);
        regProgressDialog.setCancelable(false);
        regProgressDialog.setMessage("Please wait...");
        regProgressDialog.show();
    }

    private void hideRegProgressDialog() {
        if (regProgressDialog != null && regProgressDialog.isShowing()) {
            regProgressDialog.dismiss();
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
