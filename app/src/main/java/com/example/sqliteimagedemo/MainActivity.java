package com.example.sqliteimagedemo;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.os.FileUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    private int STORAGE_PERMISSION_CODE = 23;
    ImageView imageView;
    SQLiteDatabase db;
    Button button1;
    public static final int PICK_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1=findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });

        //Permission to access external storage
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        imageView = (ImageView) findViewById(R.id.imageView);
        //creating database
        db = this.openOrCreateDatabase("test.db", Context.MODE_PRIVATE,null);
        //creating table for storing image
        db.execSQL("create table if not exists imageTb ( image blob )");
    }



    public void viewImage(View view)
    {
        Cursor c = db.rawQuery("select * from imageTb", null);
        if(c.moveToNext())
        {
            byte[] image = c.getBlob(0);
            Bitmap bmp= BitmapFactory.decodeByteArray(image, 0 , image.length);
            imageView.setImageBitmap(bmp);
            Toast.makeText(this,"Done", Toast.LENGTH_SHORT).show();
        }
    }

    public  void fetchImage(View view) throws IOException {

        File folder= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/demoImage.jpg/");
        FileInputStream fis = new FileInputStream(folder);
        byte[] image= new byte[fis.available()];
        fis.read(image);
        ContentValues values = new ContentValues();
        values.put("image",image);
        db.insert("imageTb", null,values);
        fis.close();
        Toast.makeText(this,"Image Fetched", Toast.LENGTH_SHORT).show();
    }
}