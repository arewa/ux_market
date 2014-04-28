package com.uxmarket;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.uxmarket.adapter.ListArrayAdapter;
import com.uxmarket.model.TableRow;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {

    public static final String DATA_URL = "http://www.ux.ua/ru/marketdata/indexresult-csv.aspx?type=2";

    private ListArrayAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new ListArrayAdapter(this, new ArrayList<TableRow>());

        setListAdapter(adapter);

        Button updateButton = (Button)findViewById(R.id.button_update);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
            }
        });

        updateData();
    }

    private void updateData() {
        if (!isNetworkAvaiable()) {
            Toast.makeText(this, getString(R.string.network_warning), Toast.LENGTH_LONG).show();
            return;
        }
        new UpdateDataTask(this).execute(DATA_URL);
    }

    private boolean isNetworkAvaiable() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo.isConnected();

        return isWifiConn || isMobileConn;
    }

    private class UpdateDataTask extends AsyncTask<String, Void, List<TableRow>> {

        private MainActivity activity;

        public UpdateDataTask(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(this.activity, "", getString(R.string.update_progress_message), true);
            progressDialog.setCancelable(false);
        }

        @Override
        protected List<TableRow> doInBackground(String... urls) {

            List<TableRow> result = new ArrayList<TableRow>();

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(urls[0]);
            HttpResponse response = null;
            try {
                response = httpclient.execute(httpget);
            } catch (IOException e) {
                e.printStackTrace();
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(), Charset.forName("windows-1251")));
                    String line;
                    boolean ignoreFirstLine = true;
                    while ((line = br.readLine()) != null) {
                        if (ignoreFirstLine) {
                            ignoreFirstLine = false;
                            continue;
                        }
                        String[] data = line.split(";");
                        TableRow nextRow = new TableRow();
                        nextRow.symbol = data[0];
                        nextRow.description = data[1];
                        nextRow.price = data[2];
                        nextRow.percentChange = data[3];

                        result.add(nextRow);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(List<TableRow> result) {
            adapter = new ListArrayAdapter(this.activity, result);

            setListAdapter(adapter);

            progressDialog.dismiss();
        }
    }
}
