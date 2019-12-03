package com.datatheorem.android.trustkit.reporting;

import android.os.AsyncTask;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import com.datatheorem.android.trustkit.pinning.SystemTrustManager;
import com.datatheorem.android.trustkit.utils.TrustKitLog;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;


// This returns an obscure threading error on API level < 16
@RequiresApi(api = 16)
class BackgroundReporterTask extends AsyncTask<Object, Void, Integer> {

    private static final SSLSocketFactory systemSocketFactory = getSystemSSLSocketFactory();

    @Override
    protected final Integer doInBackground(Object... params) {
        Integer lastResponseCode = null;

        // First parameter is the report
        PinningFailureReport report = (PinningFailureReport) params[0];

        // Remaining parameters are report URLs - send the report to each of them
        for (int i=1; i<params.length; i++) {
            URL reportUri = (URL) params[i];
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) reportUri.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);

                // If basic authentication was specified in the URL, set it up on the connection
                if (reportUri.getUserInfo() != null) {
                    String basicAuth = "Basic " + new String(Base64.encode(reportUri.getUserInfo().getBytes(), Base64.DEFAULT));
                    connection.setRequestProperty("Authorization", basicAuth);
                }

                if (connection instanceof HttpsURLConnection) {
                    // HTTPS URL
                    HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
                    // Use the default system factory - this will avoid an infinite loop of report
                    // uploads if the reporting server triggers SSL failures
                    // This also means that no pinning validation will be done before Android N, but
                    // for reports this is fine
                    httpsConnection.setSSLSocketFactory(systemSocketFactory);
                }

                connection.connect();

                final OutputStream stream = new BufferedOutputStream(connection.getOutputStream());
                stream.write(report.toJson().toString().getBytes("UTF-8"));
                stream.flush();
                stream.close();

                lastResponseCode = connection.getResponseCode();
            } catch (IOException e) {
                TrustKitLog.i("Background upload - task completed with error:" + e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
        return lastResponseCode;
    }

    private static SSLSocketFactory getSystemSSLSocketFactory() {
        SSLContext context;
        try {
            context = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Should never happen");
        }
        if (context == null) {
            throw new IllegalStateException("Should never happen");
        }

        try {
            context.init(null, new TrustManager[] { SystemTrustManager.getInstance() }, null);
        } catch (KeyManagementException e) {
            throw new IllegalStateException("Should never happen");
        }
        return context.getSocketFactory();
    }
}
