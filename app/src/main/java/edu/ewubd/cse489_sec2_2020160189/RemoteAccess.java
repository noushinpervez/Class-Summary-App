package edu.ewubd.cse489_sec2_2020160189;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@SuppressWarnings("ALL")

public class RemoteAccess {
    private static RemoteAccess instance = new RemoteAccess();
    private String TAG = "RemoteAccess";

    private RemoteAccess() {
    }

    public static RemoteAccess getInstance() {
        return instance;
    }

    public String makeHttpRequest(String url, String method, List<NameValuePair> params) {
        HttpURLConnection http = null;
        InputStream is = null;
        String data = "";

        // making HTTP request
        try {
            if (params != null) {
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
            }

            System.out.println("@RemoteAccess-" + ": " + url);
            URL urlc = new URL(url);
            http = (HttpURLConnection) urlc.openConnection();
            http.connect();

            if (http.getResponseCode() == 200) {
                is = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) sb.append(line + "\n");

                is.close();
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            http.disconnect();
        } catch (Exception e) {
        }
        return null;
    }

    public String remove(String baseUrl, String sid, String semester, String id) {
        String url = baseUrl + "?action=remove&sid=" + sid + "&semester=" + semester + "&id=" + id;
        return makeHttpRequest(url, "GET", null);
    }
}