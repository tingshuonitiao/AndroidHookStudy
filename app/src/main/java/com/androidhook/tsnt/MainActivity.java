package com.androidhook.tsnt;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hookClipboardService();
        initView();
    }

    private void initView() {
        final EditText editText = (EditText) findViewById(R.id.edittext);
        Button copyBtn = (Button) findViewById(R.id.copy);
        Button showBtn = (Button) findViewById(R.id.show);

        final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString().trim();
                ClipData clip = ClipData.newPlainText("simple text", text);
                clipboardManager.setPrimaryClip(clip);
            }
        });
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData clip = clipboardManager.getPrimaryClip();
                Toast.makeText(MainActivity.this, clip.getItemAt(0).getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hookClipboardService() {
        try {
            Class<?> serviceManager = Class.forName("android.os.ServiceManager");
            Method getServiceMethod = serviceManager.getMethod("getService", String.class);
            IBinder rawBinder = (IBinder) getServiceMethod.invoke(null, Context.CLIPBOARD_SERVICE);

            IBinder hookBinder = (IBinder) Proxy.newProxyInstance(serviceManager.getClassLoader(),
                    new Class[]{IBinder.class},
                    new IClipboardHookBinderHandler(rawBinder));

            Field sCacheField = serviceManager.getDeclaredField("sCache");
            sCacheField.setAccessible(true);
            Map<String, IBinder> sCache = (Map<String, IBinder>) sCacheField.get(null);
            sCache.put(Context.CLIPBOARD_SERVICE, hookBinder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
