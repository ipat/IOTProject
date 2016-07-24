package com.example.pat.iottest;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    NetworkTask networkTask;
    Button btnStart, btnSend;
    TextView textStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        btnStart = (Button) findViewById(R.id.btnStart);
        btnSend = (Button) findViewById(R.id.btnSend);
        textStatus = (TextView) findViewById(R.id.textStatus);
        btnStart.setOnClickListener(btnStartListener);
        btnSend.setOnClickListener(btnSendListener);
    }

    private View.OnClickListener btnStartListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btnStart.setVisibility(View.INVISIBLE);
            networkTask = new NetworkTask();
            networkTask.execute();
        }
    };

    private View.OnClickListener btnSendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            textStatus.setText("Sending Message to AsyncTask.");
            networkTask.SendDataToNetwork("GET / HTTP/1.1\\r\\n\\r\\n");
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    public void sendClicked(View view) throws IOException {
//        String ipaddress = ((EditText)findViewById(R.id.IPText)).getText().toString();
//        Log.v("IOTLOG", ipaddress);
//        new SendData().execute(ipaddress);
//
//
//
//    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.pat.iottest/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.pat.iottest/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public class NetworkTask extends AsyncTask<Void, byte[], Boolean> {
        Socket nsocket;
        InputStream nis;
        OutputStream nos;

        @Override
        protected void onPreExecute() {
            Log.i("IOTLOG", "onPreExecute");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = false;
            try {
                Log.i("IOTLOG", "doInBackground: Creating socket");
                SocketAddress sockaddr = new InetSocketAddress("192.168.1.1", 80);
                nsocket = new Socket();
                nsocket.connect(sockaddr, 5000);
                if (nsocket.isConnected()) {
                    nis = nsocket.getInputStream();
                    nos = nsocket.getOutputStream();
                    Log.i("IOTLOG", "doInBackground: Socket created, streams assigned");
                    Log.i("IOTLOG", "doInBackground: Waiting for initial data...");
                    byte[] buffer = new byte[4096];
                    int read = nis.read(buffer, 0, 4096);
                    while(read != -1) {
                        byte[] tempdata = new byte[read];
                        System.arraycopy(buffer, 0, tempdata, 0, read);
                        publishProgress(tempdata);
                        Log.i("IOTLOG", "doInBackground: Got some data");
                        read = nis.read(buffer, 0, 4096);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("IOTLOG", "doInBackground: IOException");
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("IOTLOG", "doInBackground: Exception");
                result = true;
            } finally {
                try {
                    nis.close();
                    nos.close();
                    nsocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("IOTLOG", "doInBackground: Finished");
            }

            return result;
        }

        public void SendDataToNetwork(String cmd) {
            try {
                if (nsocket.isConnected()) {
                    Log.i("IOTLOG", "SendDataToNetwork: Writing received message to socket");
                    nos.write(cmd.getBytes());
                } else {
                    Log.i("IOTLOG", "SendDataToNetwork: Cannot send message. Socket is closed");
                }
            } catch (Exception e) {
                Log.i("IOTLOG", "SendDataToNetwork: Message send failed. Caught an exception");
            }
        }

        @Override
        protected void onProgressUpdate(byte[]... values) {
            if (values.length > 0) {
                Log.i("IOTLOG", "onProgressUpdate: " + values[0].length + " bytes received.");
                textStatus.setText(new String(values[0]));
            }
        }
        @Override
        protected void onCancelled() {
            Log.i("IOTLOG", "Cancelled.");
            btnStart.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Log.i("IOTLOG", "onPostExecute: Completed with an Error.");
                textStatus.setText("There was a connection error.");
            } else {
                Log.i("IOTLOG", "onPostExecute: Completed.");
            }
            btnStart.setVisibility(View.VISIBLE);
        }
    }

//    class SendData extends AsyncTask<String, Void, Void> {
//
//        private Exception exception;
//
//        protected Void doInBackground(String... urls) {
//            try {
//                URL url= new URL(urls[0]);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                Log.v("IOTLOG", "Start connected: " + urls[0]);
//                try {
//                    Log.v("IOTLOG", "Start Output");
//                    urlConnection.setDoOutput(true);
//                    urlConnection.setChunkedStreamingMode(0);
//
//                    OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
//                    out.write("HELLO FROM THE OTHER SIDE".getBytes(Charset.forName("UTF-8")));
//
//                    Log.v("IOTLOG", "Finish sending");
//
//                }
////            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
////            readStream(in);
//                catch (Exception e) {
//                    this.exception = e;
//                    Log.v("IOTLOG", "1ERROR!!!!");
//                    e.printStackTrace();
//                }
//                finally{
//                    urlConnection.disconnect();
//                }
//
//            } catch (Exception e) {
//                this.exception = e;
//                Log.v("IOTLOG", "2ERROR!!!!");
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        protected void onPostExecute() {
//            // TODO: check this.exception
//            // TODO: do something with the feed
//        }
//    }
}
