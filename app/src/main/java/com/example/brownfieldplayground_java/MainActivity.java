package com.example.brownfieldplayground_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;

import com.facebook.react.BuildConfig;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;

import java.lang.reflect.InvocationTargetException;

public class MainActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler {
    private final int OVERLAY_PERMISSION_REQ_CODE = 1;
    ReactRootView reactRootView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            }
        }

        BridgeManager.shared.loadReactNative(this);

        Bundle initialProps = new Bundle();
        initialProps.putString("text", "Hello world!");

        reactRootView = new ReactRootView(this);
        reactRootView.startReactApplication(
                BridgeManager.shared.getReactNativeHost().getReactInstanceManager(),
                "MyReactComponent",
                initialProps
        );

        setContentView(reactRootView);
        initializeFlipper();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    // SYSTEM_ALERT_WINDOW permission not granted
                }
            }
        }
        BridgeManager.shared.getReactNativeHost().getReactInstanceManager().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ReactInstanceManager instanceManager = BridgeManager.shared.getReactNativeHost().getReactInstanceManager();

        if (instanceManager != null) {
            instanceManager.onHostPause(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ReactInstanceManager instanceManager = BridgeManager.shared.getReactNativeHost().getReactInstanceManager();

        if (instanceManager != null) {
            instanceManager.onHostResume(this, this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReactInstanceManager instanceManager = BridgeManager.shared.getReactNativeHost().getReactInstanceManager();

        if (instanceManager != null) {
            instanceManager.onHostDestroy(this);
        }
        if (instanceManager != null) {
            reactRootView.unmountReactApplication();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        ReactInstanceManager instanceManager = BridgeManager.shared.getReactNativeHost().getReactInstanceManager();
        if (keyCode == KeyEvent.KEYCODE_MENU && instanceManager != null) {
            instanceManager.showDevOptionsDialog();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ReactInstanceManager instanceManager = BridgeManager.shared.getReactNativeHost().getReactInstanceManager();
        instanceManager.onBackPressed();
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    private void initializeFlipper() {
        if (BuildConfig.DEBUG) {
            try {
                Class<?> aClass = Class.forName("com.example.brownfieldplayground_java");
                aClass
                        .getMethod("initializeFlipper", Context.class, ReactInstanceManager.class)
                        .invoke(null, this, BridgeManager.shared.getReactNativeHost().getReactInstanceManager());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}