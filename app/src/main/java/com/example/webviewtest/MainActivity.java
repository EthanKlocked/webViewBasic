package com.example.webviewtest;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URISyntaxException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    // ------------------------- VAR -------------------------- //
    private static final String TAG = "MainActivityLog";
    //Log.i(TAG, "POST DATA : " + position);
    WebView wView;      // webView
    ProgressBar pBar;   // loading
    EditText urlEt;     // input for url
    EditText postKey;   // input for key
    EditText postValue; // input for value
    CheckBox cBox;      // checkbox for checking cookie reset
    Button getButton;   // button 'GET'
    Button postButton;  // button 'POST'

    // --------------- DeviceControl ---------------------- //
    @Override
    public void onBackPressed() {
        if(wView.canGoBack()){      // if IS BACK PAGE
            wView.goBack();         // GO BACK
        }else{
            super.onBackPressed();  // IF NOT BACK PAGE -> EXIT APPLICATION
        }
    }

    // --------------------------- LOGIC ------------------------- //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ---------------------- CREATE ------------------------ //
        super.onCreate(savedInstanceState);     //base data
        setContentView(R.layout.activity_main); //base layout

        // ------------------------ UI -------------------------- //
        wView = findViewById(R.id.wView);
        cBox = (CheckBox) findViewById(R.id.cookieClear);
        pBar = findViewById(R.id.pBar);
        urlEt = findViewById(R.id.urlEt);
        postKey = (EditText) findViewById(R.id.postKey);
        postValue = (EditText) findViewById(R.id.postValue);
        getButton = (Button)findViewById(R.id.getButton);
        postButton = (Button)findViewById(R.id.postButton);

        // ---------------------- LISTENER ---------------------- //
        //Enter Key (GET)
        urlEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    wView.loadUrl("https://"+urlEt.getText().toString()+"");
                }
                return true;
            }
        });

        //Post Button (POST)
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postData = postKey.getText().toString() + '=' + postValue.getText().toString();
                wView.postUrl("https://"+urlEt.getText().toString()+"", postData.getBytes());
            }
        });

        //Get Button (GET)
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cBox.isChecked()){
                    CookieManager.getInstance().removeAllCookies(null);
                    CookieManager.getInstance().flush();
                }
                wView.loadUrl("https://"+urlEt.getText().toString()+""); // connect url
            }
        });

        // ----------------------- EXEC ------------------------- //
        //hide loading
        pBar.setVisibility(View.GONE);

        //init webView
        initWebView();
    }

    // ------------------------ FUNCTION ------------------------ //
    //conf webView
    public void initWebView(){
        // 1. Connect
        wView.setWebViewClient(new WebViewClient(){
            @Override                                   // 1) Loading
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pBar.setVisibility(View.VISIBLE);       // Show Loading
            }
            @Override                                   // 2) Start
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pBar.setVisibility(View.GONE);          // Hide Loading
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                /*
                String url=request.getUrl().toString();
                if (url != null && url.startsWith("intent://")) {
                    Log.i(TAG, url);
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                        if (existPackage != null) {
                            startActivity(intent);
                        } else {
                            Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                            marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                            startActivity(marketIntent);
                        }
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (url != null && url.startsWith("market://")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        if (intent != null) {
                            startActivity(intent);
                        }
                        return true;
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                view.loadUrl(url);
                return true;
                */
                String url=request.getUrl().toString();
                if (request.isRedirect()){
                    view.loadUrl(url);
                    return true;
                }
                return false;
            }
        });

        // 2. WebSettings
        WebSettings ws = wView.getSettings();
        ws.setJavaScriptEnabled(true); // Javascript permit
        //wView.setWebContentsDebuggingEnabled(true);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        ws.setDomStorageEnabled(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        ws.setSupportMultipleWindows(true);

        // 3. start default url
        wView.loadUrl("http://211.37.174.67:3000/");
    }
}