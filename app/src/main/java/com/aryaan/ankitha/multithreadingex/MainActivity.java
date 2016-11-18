package com.aryaan.ankitha.multithreadingex;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private EditText editText;
    private ListView listView;
    private String[] listOfImage;
    private ProgressBar progressBar;
    private LinearLayout loadingSection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText)findViewById(R.id.editText);
        listView = (ListView)findViewById(R.id.urlList);
        listView.setOnItemClickListener(this);
        listOfImage = getResources().getStringArray(R.array.imageUrls);
        progressBar = (ProgressBar)findViewById(R.id.downloadProgress);
        loadingSection = (LinearLayout)findViewById(R.id.downloadingSection);
    }

    public void downloadImage(View view){

        String url = editText.getText().toString();
        Thread myThread = new Thread(new DownloadImagesThread(url));
        myThread.start();
    }

    public boolean downloadImageUsingThreads(String url){
        /*
        * 1 create the url object that represents the url
        * 2 open connection using that url object
        * 3 read data using input stream into byte array
        * 4 open a file output stream to save data on sd card
        * 5 write data to fileoutputstream
        * 6 close the connections
        * */
        //http://www..
        boolean successful = false;
        URL downloadURL = null;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        File file = null;
        FileOutputStream fileOutputStream = null;
        try {
            downloadURL = new URL(url);
            connection = (HttpURLConnection) downloadURL.openConnection();
            int read = -1;
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"
                    +Uri.parse(url).getLastPathSegment());
            //storage/0/emulated/downloads/9-credit-0.jpg
            fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            inputStream = connection.getInputStream();
            while((read = inputStream.read(buffer)) != -1){
                fileOutputStream.write(buffer,0,read);
                //Message.logMessage(""+read);
            }
            successful=true;
        } catch (MalformedURLException e) {
            Message.logMessage(""+e);

        } catch (IOException e) {
            Message.logMessage(""+e);

        }finally {
            if (connection != null){
                connection.disconnect();
            }
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Message.logMessage(""+e);
                }
            }
            if (fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Message.logMessage(""+e);
                }
            }
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingSection.setVisibility(View.GONE);
                }
            });
        }

        return successful;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        editText.setText(listOfImage[i]);
    }

    private class DownloadImagesThread implements Runnable{

        private String url;

        public DownloadImagesThread(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingSection.setVisibility(View.VISIBLE);
                }
            });
            downloadImageUsingThreads(url);
        }
    }

}
