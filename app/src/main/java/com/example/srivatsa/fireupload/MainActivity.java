package com.example.srivatsa.fireupload;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST=1;

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private TextView mTVShowUpload;
    private EditText mETFileName;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private Uri mImageUri;

    private StorageReference mStroageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonChooseImage=findViewById(R.id.button_choose_image);
        mButtonUpload=findViewById(R.id.button_upload_image);
        mTVShowUpload=findViewById(R.id.show_uploads);
        mETFileName=findViewById(R.id.file_name);
        mImageView=findViewById(R.id.image_view);
        mProgressBar=findViewById(R.id.progress_bar);
        mStroageRef= FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploads");

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();

            }
        });
        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUploadTask!=null&& mUploadTask.isInProgress()) {
                    Toast.makeText(MainActivity.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
                }
                uploadFile();
            }
        });
        mTVShowUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              OpenImagesActivity();
            }
        });
    }



    private void  openFileChooser()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK
                && data!= null && data.getData()!=null)
        {
            mImageUri=data.getData();

           Picasso.with(this).load(mImageUri).into(mImageView);

        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void  uploadFile()
    {
     if(mImageUri!=null) {
         StorageReference fileReference=mStroageRef.child(System.currentTimeMillis()
         + "."+getFileExtension(mImageUri));

         mUploadTask=fileReference.putFile(mImageUri)
                 .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                         Handler handler=new Handler();
                         handler.postDelayed(new Runnable() {
                             @Override
                             public void run() {
                                 mProgressBar.setProgress(0);
                             }
                         }, 3000);
                         Toast.makeText(MainActivity.this, "Upload Success!! Yay!", Toast.LENGTH_SHORT).show();
                         Upload upload=new Upload(mETFileName.getText().toString().trim(),
                                 taskSnapshot.getDownloadUrl().toString());
                         String uploadId=mDatabaseRef.push().getKey();
                         mDatabaseRef.child(uploadId).setValue(upload);
                     }
                 })
                 .addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                     }
                 })
                 .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                         double progress=(100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                         mProgressBar.setProgress((int)progress);

                     }
                 });
     }
     else {
         Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();
     }
    }
    private void OpenImagesActivity() {
        Intent intent=new Intent(this, ImagesActivity.class);
        startActivity(intent);
    }
}
