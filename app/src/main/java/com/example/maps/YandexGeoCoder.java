package com.example.maps;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;


public class YandexGeoCoder {


    public interface Callback {
        void onPositionFound(ArrayList<String> points);
        void onError(String text);
    }

    private String _apiKey = null;
    private Callback _callback = null;

    public YandexGeoCoder(String apiKey, Callback callback) {
        this._apiKey = apiKey;
        this._callback = callback;
    }

    public void SearchObject(double lat, double lon) throws IOException, JSONException {
        final ArrayList<String> addresses = new ArrayList<String>();
        String urlString = String.format("https://geocode-maps.yandex.ru/1.x/?apikey=%s&geocode=%s,%s&format=json", this._apiKey, lat, lon);
        final Callback cb = this._callback;
        new AsyncNetworking() {
            @Override
            protected void onPostExecute(String result) {
                try {
                    super.onPostExecute(result);
                    JSONObject jObject = new JSONObject(result);
                    JSONObject response = jObject.getJSONObject("response");
                    response = response.getJSONObject("GeoObjectCollection");
                    JSONArray results = response.getJSONArray("featureMember");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject geoObj = null;
                        String address = null;
                        try {
                            geoObj = results.getJSONObject(i).getJSONObject("GeoObject");
                            address = geoObj.getString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        addresses.add(address);
                    }
                    cb.onPositionFound(addresses);

                } catch (Exception e) {
                    cb.onError("Ничего не найдено");
                }
            }

        }.execute(urlString);
    }

    class AsyncNetworking extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL(strings[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection con = null;
            try {
                con = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                con.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            int status = 0;
            try {
                status = con.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Reader streamReader = null;
            if (status > 299) {
                streamReader = new InputStreamReader(con.getErrorStream());
            } else {
                try {
                    streamReader = new InputStreamReader(con.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Scanner s = new Scanner(streamReader).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            return result;
        }
    }
}
