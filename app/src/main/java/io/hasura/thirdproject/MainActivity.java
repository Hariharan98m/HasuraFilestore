
package io.hasura.thirdproject;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import io.hasura.sdk.Hasura;
import io.hasura.sdk.HasuraClient;
import io.hasura.sdk.HasuraUser;
import io.hasura.sdk.ProjectConfig;
import io.hasura.sdk.exception.HasuraException;
import io.hasura.sdk.model.response.FileUploadResponse;
import io.hasura.sdk.responseListener.AuthResponseListener;
import io.hasura.sdk.responseListener.FileUploadResponseListener;

import static android.R.attr.data;
import static android.R.id.message;

//import static io.hasura.thirdproject.MainActivity.EXTRA_MESSAGE;
public class MainActivity extends AppCompatActivity {


    HasuraClient client;
    File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        Hasura.setProjectConfig(new ProjectConfig.Builder()
                .setProjectName("dilate70")
                .build())
                .enableLogs()
                .initialise(this);
        client = Hasura.getClient();

        //Request user permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }

        //Create a hasura user
        HasuraUser user = client.getUser(); // Hasura.getClient.getUser();

        if (user.isLoggedIn()) {
            Toast.makeText(this,"User logged in as"+client.getUser().getUsername(),Toast.LENGTH_SHORT).show();
            //This user is logged in
        } else {
            Toast.makeText(this,"User not logged in-- about to login" ,Toast.LENGTH_LONG).show();
            //This user is not logged in
            //Login
            user.setUsername("***");
            user.setPassword("***");
            user.login(new AuthResponseListener() {

                @Override
                public void onSuccess(String s) {
                    Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
                }

                public void onFailure(HasuraException e) {

                }
            });

        }

    }

    public void choose(View view){

        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("audio/mp3");
        startActivityForResult(i,1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    int permissionCheck= ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Permission Check");
                    builder.setMessage(Integer.toString(permissionCheck));
                    builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();


                } else {

                }
                return;
            }
        }
    }

    public void upload(View view) {

        Toast.makeText(getBaseContext(), "Upload started", Toast.LENGTH_LONG).show();
        if (client.getUser().isLoggedIn()) {
            client.useFileStoreService()
                    .uploadFile("1st_song", file, "audio/mp3", new FileUploadResponseListener() {
                        @Override
                        public void onUploadComplete(FileUploadResponse fileUploadResponse) {
                            Toast.makeText(getBaseContext(), "fileId is " + fileUploadResponse.getFile_id(), Toast.LENGTH_LONG).show();
                            Toast.makeText(getBaseContext(), "fileUser is " + fileUploadResponse.getUser_id(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onUploadFailed(HasuraException e) {
                            Toast.makeText(getBaseContext(), "Upload failed " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK == resultCode) {
            /*
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Result");
            builder.setMessage("URI = "+ data.getData().toString());
            builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
            */
        }
        file= new File(data.getData().getPath());
        if(file.exists()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(file.getAbsolutePath());
            builder.setMessage("File name is" + file.getName());
            builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }

        }
    }

