package com.example.bunprofunpro;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.bunprofunpro.MainActivity.apiKey;


public class Utilities {



    public static class StudyQueue {
        public String username;
        public Integer grammarPointCount;
        public Integer ghostReviewCount;
        public String creationDate;

        public Integer reviewsAvailable;
        public String nextReviewDate;
        public Integer reviewsNextHour;
        public Integer reviewsNextDay;

        public StudyQueue(String username, Integer grammarPointCount, Integer ghostReviewCount, String creationDate, Integer reviewsAvailable, String nextReviewDate, Integer reviewsNextHour, Integer reviewsNextDay) {
            this.username = username;
            this.grammarPointCount = grammarPointCount;
            this.ghostReviewCount = ghostReviewCount;
            this.creationDate = creationDate;

            this.reviewsAvailable = reviewsAvailable;
            this.nextReviewDate = nextReviewDate;
            this.reviewsNextHour = reviewsNextHour;
            this.reviewsNextDay = reviewsNextDay;
        }
    }



    public static class ApiConnection implements Runnable {
        private volatile String response;

        @Override
        public void run() {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL("https://bunpro.jp/api/user/" + apiKey + "/study_queue");

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }

                response = total.toString();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        public String getResponse() {
            return response;
        }
    }



    public static String jsonResponse(String option) {
        String apiResponse;

        ApiConnection api = new ApiConnection();
        Thread thread = new Thread(api);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        apiResponse = api.getResponse();
        return apiResponse;
    }



    public static StudyQueue buildStudyQueue() throws Exception {
        JSONObject json;
        JSONObject uijson;
        JSONObject sqjson;
        StudyQueue response;

        String username;
        Integer grammarPointCount;
        Integer ghostReviewCount;
        String creationDate;
        Integer reviewsAvailable;
        String nextReviewDate;
        Integer reviewsAvailableNextHour;
        Integer reviewsAvailableNextDay;

        try {
            json = new JSONObject(jsonResponse("study_queue"));
            uijson = json.getJSONObject("user_information");
            sqjson = json.getJSONObject("requested_information");

            username = uijson.getString("username");
            grammarPointCount = uijson.getInt("grammar_point_count");
            ghostReviewCount = uijson.getInt("ghost_review_count");
            creationDate = uijson.getString("creation_date");
            reviewsAvailable = sqjson.getInt("reviews_available");
            nextReviewDate = sqjson.getString("next_review_date");
            reviewsAvailableNextHour = sqjson.getInt("reviews_available_next_hour");
            reviewsAvailableNextDay = sqjson.getInt("reviews_available_next_day");
        }

        catch (Exception e) {
            throw new JSONException(e);
        }

        response = new StudyQueue(username, grammarPointCount, ghostReviewCount, creationDate, reviewsAvailable, nextReviewDate, reviewsAvailableNextHour, reviewsAvailableNextDay);
        return response;
    }
}