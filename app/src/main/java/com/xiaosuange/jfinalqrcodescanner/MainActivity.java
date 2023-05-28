package com.xiaosuange.jfinalqrcodescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private String key;
    private String value;
    private EditText keyed;
    private EditText valueed;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        keyed = findViewById(R.id.key);
        valueed = findViewById(R.id.value);
        button = findViewById(R.id.scan);
        Button button = findViewById(R.id.scan);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                key = keyed.getText().toString();
                value = valueed.getText().toString();
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
//                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                List<String> formats = new ArrayList<>();
                formats.add(BarcodeFormat.QR_CODE.name());
                formats.add(BarcodeFormat.DATA_MATRIX.name());

                //IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.setDesiredBarcodeFormats(formats);

                integrator.setOrientationLocked(false);
                integrator.setPrompt("Scan a QR Code");
                integrator.initiateScan();
            }
        });

    }

    public void addHeadersToConnection(HttpURLConnection connection, String... headers) {
        // 遍历所有传入的头部信息
        for (int i = 0; i < headers.length; i += 2) {
            String key = headers[i];
            String value = headers[i + 1];

            // 将当前头部字段添加到连接对象的请求头中
            connection.setRequestProperty(key, value);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String contents = result.getContents();
            if (contents != null) {

                new Thread(() -> {
                    try {
                        OkHttpClient client = new OkHttpClient().newBuilder()
                                .build();
                        Request request = new Request.Builder()
                                .url(contents)
                                .addHeader(key, value)
                                .build();
                        Response response = client.newCall(request).execute();
                    } catch (IOException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).start();
            }
        }
    }


}