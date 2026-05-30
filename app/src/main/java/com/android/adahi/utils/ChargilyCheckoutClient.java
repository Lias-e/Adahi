package com.android.adahi.utils;

import android.util.Log;

import com.android.adahi.BuildConfig;
import com.android.adahi.models.Order;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ChargilyCheckoutClient {

    private static final String TAG = "ChargilyCheckoutClient";
    private static final String CHECKOUTS_PATH = "/checkouts";
    // Chargily requires success/failure URLs to use http or https. Use the configured
    // app return URL from BuildConfig so it can be overridden per environment.
    private static final String APP_RETURN_URL = com.android.adahi.BuildConfig.CHARGILY_APP_RETURN_URL;
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private ChargilyCheckoutClient() {
    }

    public interface Callback {
        void onSuccess(ChargilyCheckoutResponse response);

        void onError(String message, Exception exception);
    }

    public interface CheckoutStatusCallback {
        void onSuccess(String status);

        void onError(String message, Exception exception);
    }

    public static final class ChargilyCheckoutResponse {
        private final String checkoutId;
        private final String checkoutUrl;
        private final String successUrl;
        private final String failureUrl;

        public ChargilyCheckoutResponse(String checkoutId, String checkoutUrl, String successUrl, String failureUrl) {
            this.checkoutId = checkoutId;
            this.checkoutUrl = checkoutUrl;
            this.successUrl = successUrl;
            this.failureUrl = failureUrl;
        }

        public String getCheckoutId() {
            return checkoutId;
        }

        public String getCheckoutUrl() {
            return checkoutUrl;
        }

        public String getSuccessUrl() {
            return successUrl;
        }

        public String getFailureUrl() {
            return failureUrl;
        }
    }

    public static void createCheckout(Order order, String collectionName, Callback callback) {
        EXECUTOR.execute(() -> {
            HttpURLConnection connection = null;
            try {
                ensureApiConfigured();

                JSONObject metadata = new JSONObject();
                metadata.put("orderId", order.getOrderId());
                metadata.put("collectionName", collectionName);
                metadata.put("orderType", order.getOrderType());

                JSONObject requestBody = new JSONObject();
                requestBody.put("amount", Math.round(order.getFeeAmount()));
                requestBody.put("currency", "dzd");
                requestBody.put("locale", "ar");
                requestBody.put("description", order.getOrderType() + " order for Adahi");
                requestBody.put("success_url", APP_RETURN_URL + "?result=success&orderId=" + order.getOrderId() + "&collectionName=" + collectionName);
                requestBody.put("failure_url", APP_RETURN_URL + "?result=failure&orderId=" + order.getOrderId() + "&collectionName=" + collectionName);
                requestBody.put("metadata", metadata);

                URL url = new URL(joinUrl(BuildConfig.CHARGILY_API_BASE_URL, CHECKOUTS_PATH));
                connection = openAuthorizedJsonConnection(url, "POST");
                writeJsonBody(connection, requestBody.toString());

                int responseCode = connection.getResponseCode();
                String responseBody = readResponseBody(responseCode >= 200 && responseCode < 300 ? connection.getInputStream() : connection.getErrorStream());
                if (responseCode < 200 || responseCode >= 300) {
                    throw new IOException("Chargily checkout request failed: HTTP " + responseCode + " - " + responseBody);
                }

                JSONObject responseJson = new JSONObject(responseBody);
                String checkoutId = responseJson.optString("id", "");
                String checkoutUrl = responseJson.optString("checkout_url", "");
                String successUrl = responseJson.optString("success_url", "");
                String failureUrl = responseJson.optString("failure_url", "");

                if (checkoutUrl.trim().isEmpty()) {
                    throw new IOException("Chargily response did not include a checkout URL");
                }

                callback.onSuccess(new ChargilyCheckoutResponse(checkoutId, checkoutUrl, successUrl, failureUrl));
            } catch (Exception exception) {
                Log.e(TAG, "Failed to create Chargily checkout", exception);
                callback.onError(exception.getMessage(), exception);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    public static void fetchCheckoutStatus(String checkoutId, CheckoutStatusCallback callback) {
        EXECUTOR.execute(() -> {
            HttpURLConnection connection = null;
            try {
                ensureApiConfigured();

                if (checkoutId == null || checkoutId.trim().isEmpty()) {
                    throw new IllegalArgumentException("Missing checkout id");
                }

                URL url = new URL(joinUrl(BuildConfig.CHARGILY_API_BASE_URL, CHECKOUTS_PATH + "/" + checkoutId));
                connection = openAuthorizedJsonConnection(url, "GET");

                int responseCode = connection.getResponseCode();
                String responseBody = readResponseBody(responseCode >= 200 && responseCode < 300 ? connection.getInputStream() : connection.getErrorStream());
                if (responseCode < 200 || responseCode >= 300) {
                    throw new IOException("Chargily retrieve checkout failed: HTTP " + responseCode + " - " + responseBody);
                }

                JSONObject responseJson = new JSONObject(responseBody);
                String status = responseJson.optString("status", "pending");
                callback.onSuccess(status);
            } catch (Exception exception) {
                Log.e(TAG, "Failed to retrieve Chargily checkout status", exception);
                callback.onError(exception.getMessage(), exception);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private static void ensureApiConfigured() {
        if (BuildConfig.CHARGILY_SECRET_KEY == null || BuildConfig.CHARGILY_SECRET_KEY.trim().isEmpty()) {
            throw new IllegalStateException("CHARGILY_SECRET_KEY is missing. Add it in Gradle properties or environment.");
        }
        if (BuildConfig.CHARGILY_API_BASE_URL == null || BuildConfig.CHARGILY_API_BASE_URL.trim().isEmpty()) {
            throw new IllegalStateException("CHARGILY_API_BASE_URL is missing.");
        }
    }

    private static HttpURLConnection openAuthorizedJsonConnection(URL url, String method) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setRequestMethod(method);
        connection.setDoInput(true);
        connection.setRequestProperty("Authorization", "Bearer " + BuildConfig.CHARGILY_SECRET_KEY);
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        return connection;
    }

    private static void writeJsonBody(HttpURLConnection connection, String body) throws IOException {
        connection.setDoOutput(true);
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(bytes);
            outputStream.flush();
        }
    }

    private static String joinUrl(String baseUrl, String path) {
        String normalizedBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return normalizedBaseUrl + path;
    }

    private static String readResponseBody(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }

        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }
        return body.toString();
    }
}
