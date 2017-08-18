package com.example.permissionmanager;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.timshinlee.utils.PermissionManager;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private TextView result;
    PermissionManager.PermissionCallback callback = new PermissionManager.PermissionCallback() {
        @Override
        public void onAllGranted() {
            result.setText("");
            result.setText("all permission has been granted");
        }

        @Override
        public void onDenied(String[] deniedPermissions) {
            result.setText("");
            result.setText("the following permissions has been denied: " + Arrays.toString(deniedPermissions));
        }
    };
    private PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = (TextView) findViewById(R.id.result);
        permissionManager = PermissionManager.getInstance(callback);
    }

    public void request(View view) {
        final String[] permissions = {
                Manifest.permission.CALL_PHONE,
//                Manifest.permission.CAMERA
        };
        permissionManager.requestPermissionIfNeeded(this, permissions, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void call(View view) {
        final Uri uri = Uri.parse("tel:10000");
        //noinspection MissingPermission
        startActivity(new Intent(Intent.ACTION_CALL, uri));
    }
}
