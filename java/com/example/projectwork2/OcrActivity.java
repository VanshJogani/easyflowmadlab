package com.example.projectwork2;

//package com.example.projectpaymentstry;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.projectwork2.R; // Replace with your package name

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OcrActivity extends AppCompatActivity {

    private TextView textOutput;
    private Button buttonProcess;
    private TextView ocrOutput;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private Button selectImageButton;
    private ImageView imageView; // To display the selected image
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        selectImageButton = findViewById(R.id.select_image_button); // Replace with your button ID
        imageView = findViewById(R.id.image_view); // Replace with your ImageView ID

        // Register the activity result launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri selectedImageUri = data.getData();
                            Bitmap selectedBitmap = handleSelectedImage(selectedImageUri);
                            if (selectedBitmap != null) {
                                processImage(selectedBitmap); // Call your processing method
                            }
                        }
                    }
                }
        );

        selectImageButton.setOnClickListener(v -> checkPermissionAndOpenGallery());
        textOutput = findViewById(R.id.text_output);
        buttonProcess = findViewById(R.id.button_select);
        ocrOutput = findViewById(R.id.ocroutput);

        // Set button click listener to process the image and extract text
       // buttonProcess.setOnClickListener(v -> processImage(Bitmap bitmap));
        selectImageButton.setOnClickListener(v -> checkPermissionAndOpenGallery());
    }

    private void processImage(Bitmap bitmap) {
        // Load image from drawable
        // Load image from drawable
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img2);
        InputImage image = InputImage.fromBitmap(bitmap, 0);

// Initialize Text Recognizer
        com.google.mlkit.vision.text.TextRecognizer recognizer =
                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

// Process the image for text extraction
        recognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    String extractedText = visionText.getText();
                    ocrOutput.setText(extractedText);
                    String totalAmount = findTotalAmount(extractedText);
                    String amt[] = totalAmount.split(" ");
                    Toast.makeText(this, ""+amt[1], Toast.LENGTH_SHORT).show();
                    textOutput.setText(totalAmount.isEmpty() ? "Total not found." : totalAmount);
                    //Toast.makeText(this, totalAmount, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), GroupExpenseActivity.class);
                    double amt1 = Double.parseDouble(amt[1]);
                    intent.putExtra("amt", amt1);
                    String gname = getIntent().getStringExtra("GROUP_NAME");
                    intent.putExtra("GROUP_NAME", gname);
                    startActivity(intent);
                });
                //.addOnFailureListener(e -> textOutput.setText("Error: " + e.getMessage()));
        // Error handling
    }
    private String findTotalAmount(String text) {
        Pattern pattern = Pattern.compile("\\d+(\\.\\d{1,2})?"); // Matches numbers like 13, 13.6, 13.60
        Matcher matcher = pattern.matcher(text);

        double max = -1;

        while (matcher.find()) {
            try {
                double value = Double.parseDouble(matcher.group());
                if (value > max) {
                    max = value;
                }
            } catch (NumberFormatException ignored) {}
        }

        return max > -1 ? "Total: " + String.format("%.2f", max) : "";
    }


    private void checkPermissionAndOpenGallery() {
        if (checkPermission()) {
            openGallery();
        } else {
            requestPermission();
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        String permission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ? Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;
        ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
    private Bitmap handleSelectedImage(Uri imageUri) {
        try {
            Bitmap bitmap;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            }
            imageView.setImageBitmap(bitmap);
            Log.d("Image URI", "Selected Image URI: " + imageUri.toString());
            return bitmap; // Return the Bitmap
        } catch (IOException e) {
            Log.e("Image Selection", "Error handling image: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            return null; // Return null in case of error
        }
    }

}



/*
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OcrActivity extends AppCompatActivity {

    private TextView textOutput;
    private Button buttonProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        textOutput = findViewById(R.id.text_output);
        buttonProcess = findViewById(R.id.button_select);

        // Set button click listener to process the image and extract text
        buttonProcess.setOnClickListener(v -> processImage());


    }

    private void processImage() {
        // Load image from drawable
        // Load image from drawable
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_img);
        InputImage image = InputImage.fromBitmap(bitmap, 0);

// Initialize Text Recognizer
        com.google.mlkit.vision.text.TextRecognizer recognizer =
                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

// Process the image for text extraction
        recognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    String extractedText = visionText.getText();
                    String totalAmount = findTotalAmount(extractedText);
                    textOutput.setText(totalAmount.isEmpty() ? "Total not found." : totalAmount);
                })
                .addOnFailureListener(e -> textOutput.setText("Error: " + e.getMessage()));

        // Error handling
        //String tot = textOutput.getText().toString();
        //String gn = getIntent().getStringExtra("GROUP_NAME");
        //String[] amt = tot.split(" ");
        //Toast.makeText(this, amt[1], Toast.LENGTH_SHORT).show();
        /*
        double amt1 = Double.parseDouble(amt[1]);
        Intent intent = new Intent(getApplicationContext(), GroupExpenseActivity.class);
        intent.putExtra("GROUP_NAME", gn);
        intent.putExtra("amount", amt1);
        startActivity(intent);

        //Intent intent = new Intent(getApplicationContext(), GroupExpenseActivity.class);
        //intent.putExtra(String)


    }
    private String findTotalAmount(String text) {
        Pattern pattern = Pattern.compile("\\d+(\\.\\d{1,2})?"); // Matches numbers like 13, 13.6, 13.60
        Matcher matcher = pattern.matcher(text);

        double max = -1;

        while (matcher.find()) {
            try {
                double value = Double.parseDouble(matcher.group());
                if (value > max) {
                    max = value;
                }
            } catch (NumberFormatException ignored) {}
        }

        return max > -1 ? "Total: " + String.format("%.2f", max) : "";
    }
}
*/