package com.example.qrscanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

import me.ydcool.lib.qrmodule.encoding.QrGenerator;

public class UserRegistration extends AppCompatActivity {
EditText NameEditText, NumberEditText, IdEditText, EmailEditText;
Button GenerateBtn;
ImageView UserImage, qrCodeImage;

final Context context = this;
LayoutInflater inflater;
View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        final Map<String, String> userDetails = new HashMap<>();
        inflater = this.getLayoutInflater();
        dialogView= inflater.inflate(R.layout.alert_image, null);


        NameEditText = findViewById(R.id.NameEdtx);
        NumberEditText = findViewById(R.id.NumberEdtx);
        IdEditText = findViewById(R.id.IdEdtx);
        EmailEditText = findViewById(R.id.EmailEdtx);
        UserImage = findViewById(R.id.imageView2);
        GenerateBtn = findViewById(R.id.button2);

        UserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        GenerateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performCheck();
                userDetails.put("Name", NameEditText.getText().toString());
                userDetails.put("Number", NumberEditText.getText().toString());
                userDetails.put("User Id", IdEditText.getText().toString());
                userDetails.put("Email", EmailEditText.getText().toString());
                try {
                    Bitmap qrCode = new QrGenerator.Builder()
                            .content(userDetails.toString())
                            .qrSize(300)
                            .margin(2)
                            .color(Color.BLACK)
                            .bgColor(Color.WHITE)
                            .ecc(ErrorCorrectionLevel.H)
                            .overlay(context,R.mipmap.ic_launcher)
                            .overlaySize(100)
                            .overlayAlpha(255)
                            .encode();

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                    dialogBuilder.setTitle("Qr Code");
                    dialogBuilder.setView(dialogView);
                    qrCodeImage = dialogView.findViewById(R.id.qrcode_img);
                    qrCodeImage.setImageBitmap(qrCode);

                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();

                    dialogBuilder.setNeutralButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                        }
                    });
                } catch (WriterException e) {
                    e.printStackTrace();
                }


            }
        });

    }
    //This method perfoms a check on the edit Text to make sure that the user puts details in them and not an empty edit text
    public  void performCheck() {
        if (TextUtils.isEmpty(NameEditText.getText())){
            NameEditText.setError("Input Name");
        }
        if (TextUtils.isEmpty(NumberEditText.getText())){
            NumberEditText.setError("Input Number");
        }
        if (TextUtils.isEmpty(IdEditText.getText())){
            IdEditText.setError("Input Id");
        }
        if (TextUtils.isEmpty(EmailEditText.getText())){
            EmailEditText.setError("Input Email");
        }
    }
//This code from here down enables the user to pick photos to the imageView
    public void  selectImage(){
        final  CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")){
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                }else if (options[item].equals("Choose from Gallery")){
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);

                }else if (options[item].equals("Cancel")){
                    dialog.dismiss();
                }

            }
        });
        builder.show();
    }
    //This method gets the chosen image to were it is needed
    String picturePath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED){
            switch (requestCode){
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        UserImage.setImageBitmap(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                picturePath = cursor.getString(columnIndex);
                                UserImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }

                    }
            }

        }
    }
}
