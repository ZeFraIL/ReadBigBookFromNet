package zeev.fraiman.readbigbookfromnet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    Button bRFN;
    String url_addr="https://www.gutenberg.org/files/98/98-0.txt",all="";
    Thread background;
    Handler handler;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (background.getState() == Thread.State.TERMINATED) {
                    tv.setText(all + "");
                    bRFN.setEnabled(true);
                }
                else
                    tv.setText(count + "");
            }
        };

        initComponents();

        bRFN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bRFN.setEnabled(false);
                background = new Thread (new Runnable() {
                    public void run() {
                        URL url = null;
                        try {
                            url = new URL(url_addr);
                            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                            InputStream is = urlConn.getInputStream();
                            InputStreamReader isr=new InputStreamReader(is);
                            BufferedReader br = new BufferedReader(isr);
                            String temp="";
                            count=0;
                            while ((temp=br.readLine())!=null) {
                                count++;
                                all += temp+"\n";
                                handler.sendMessage(handler.obtainMessage());
                            }
                            br.close();
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                background.start();
            }
        });
    }

    private void initComponents() {
        tv= findViewById(R.id.tv);
        bRFN= findViewById(R.id.bRFN);
    }
}