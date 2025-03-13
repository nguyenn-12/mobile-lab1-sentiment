package com.example.lab1_2;
import okhttp3.*;
import org.json.*;

import java.io.IOException;

public class SentimentService {
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private static final String API_KEY = "AIzaSyAy-U43S1QbVjcNMSPEkWx3xUUq_qpHy8Q"; // Thay API Key thật của bạn

    public interface SentimentCallback {
        void onSuccess(String sentiment);
        void onFailure(String errorMessage);
    }

    public static void analyzeSentiment(String text, SentimentCallback callback) {
        OkHttpClient client = new OkHttpClient();

        JSONObject requestBody = new JSONObject();
        try {
            JSONArray contentsArray = new JSONArray();
            JSONObject contentObject = new JSONObject();
            JSONArray partsArray = new JSONArray();
            JSONObject textObject = new JSONObject();

            // Prompt chỉ yêu cầu trả về 'positive' hoặc 'negative'
            textObject.put("text", "Analyze the sentiment of this sentence: \"" + text +
                    "\". Return only 'positive' or 'negative'. Do not return 'neutral' or any extra text.");
            partsArray.put(textObject);
            contentObject.put("parts", partsArray);
            contentsArray.put(contentObject);
            requestBody.put("contents", contentsArray);
        } catch (JSONException e) {
            callback.onFailure("Error: JSON formatting failed");
            return;
        }

        RequestBody body = RequestBody.create(requestBody.toString(), MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(API_URL + "?key=" + API_KEY)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Error: API call failed.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("Error: API request failed (" + response.code() + ").");
                    return;
                }

                try {
                    String responseData = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseData);

                    if (jsonResponse.has("candidates") && jsonResponse.getJSONArray("candidates").length() > 0) {
                        String sentiment = jsonResponse.getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text")
                                .trim().toLowerCase(); // Chuẩn hóa kết quả

                        if (sentiment.equals("positive") || sentiment.equals("negative")) {
                            callback.onSuccess(sentiment);
                        } else {
                            callback.onFailure("Error: Unexpected sentiment result.");
                        }
                    } else {
                        callback.onFailure("Error: Invalid API response format.");
                    }
                } catch (JSONException e) {
                    callback.onFailure("Error: Failed to parse JSON response.");
                }
            }
        });
    }
}
