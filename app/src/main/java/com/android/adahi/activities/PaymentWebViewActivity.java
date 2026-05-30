package com.android.adahi.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.adahi.R;

public class PaymentWebViewActivity extends AppCompatActivity {

    public static final String EXTRA_URL = "extra_url";

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_webview);

        webView = findViewById(R.id.paymentWebView);
        progressBar = findViewById(R.id.paymentProgress);

        String url = getIntent().getStringExtra(EXTRA_URL);
        if (url == null || url.trim().isEmpty()) {
            setResult(Activity.RESULT_CANCELED);
            finish();
            return;
        }

        android.webkit.WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String requested = request.getUrl().toString();
                if (handlePotentialReturnUrl(requested)) {
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                progressBar.setVisibility(View.GONE);
                android.util.Log.e("PaymentWebView", "WebView error " + errorCode + ": " + description);
                android.widget.Toast.makeText(PaymentWebViewActivity.this, "Unable to open payment page: " + description, android.widget.Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_CANCELED);
                finish();
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, android.webkit.WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                progressBar.setVisibility(View.GONE);
                android.util.Log.e("PaymentWebView", "HTTP error: " + errorResponse.getStatusCode());
                android.widget.Toast.makeText(PaymentWebViewActivity.this, "Payment site returned error: " + errorResponse.getStatusCode(), android.widget.Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_CANCELED);
                finish();
            }

            @Override
            public void onReceivedSslError(WebView view, android.webkit.SslErrorHandler handler, android.net.http.SslError error) {
                super.onReceivedSslError(view, handler, error);
                progressBar.setVisibility(View.GONE);
                android.util.Log.e("PaymentWebView", "SSL error: " + error.toString());
                android.widget.Toast.makeText(PaymentWebViewActivity.this, "SSL error while opening payment page", android.widget.Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        webView.loadUrl(url);
    }

    private boolean handlePotentialReturnUrl(String url) {
        String returnPrefix = com.android.adahi.BuildConfig.CHARGILY_APP_RETURN_URL;
        if (returnPrefix != null && !returnPrefix.trim().isEmpty() && url.startsWith(returnPrefix)) {
            // Return this URL to the caller so it can verify and update order status
            Intent data = new Intent();
            data.setData(Uri.parse(url));
            setResult(Activity.RESULT_OK, data);
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
}
