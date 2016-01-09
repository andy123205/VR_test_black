package com.example.andylab.vr_test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
//import android.support.v4.widget.CircleImageView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private MyThread thread;
    private MyHandler handler;
    private MyHandler2 handler2;
    private Timer timer;
    private MyTask task;
    private TextView txv,txv2;
    private Button btn,btn2;
    private ToggleButton tbtn;
    private ImageView img,img2;
    private Bitmap bp;
    private double fps;
    private String UrlSrc = "http://140.121.137.185:7070/?action=snapshot";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去標題 & 全螢幕
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        txv = (TextView)findViewById(R.id.textView);
        txv2 = (TextView)findViewById(R.id.textView2);
        btn = (Button)findViewById(R.id.button);
        btn2 = (Button)findViewById(R.id.button2);
        tbtn = (ToggleButton)findViewById(R.id.toggleButton);

        img = (ImageView)findViewById(R.id.imageView);
        img2 = (ImageView)findViewById(R.id.imageView2);

        handler = new MyHandler();
        handler2 = new MyHandler2();
        thread = new MyThread();
        timer = new Timer();
        task = new MyTask();

        timer.schedule(task,500,1000);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handler.post(thread);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handler.removeCallbacks(thread);
            }
        });

        tbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UrlSrc =( (isChecked)? "http://140.121.137.185:7171/?action=snapshot":"http://140.121.137.185:7070/?action=snapshot");
                if(isChecked){
                    txv.setText("Webcam");
                }
                else{
                    txv.setText("Phonecam");
                }
            }
        });

    }

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

    private class MyThread extends Thread{
        @Override
        public void run() {
            handler.sendMessage(new Message());
            fps = fps + 1;
            //bp = getBitmapFromURL("http://140.121.137.185:7171/?action=snapshot");
            handler.postDelayed(thread,33);
        }
    }

    private class MyHandler extends Handler {
        private int i;
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //txv.setText("time:" +(i/30));
            //i++;


            //建立一個AsyncTask執行緒進行圖片讀取動作，並帶入圖片連結網址路徑
            new AsyncTask<String, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... params) {
                    String url = params[0];
                    return getBitmapFromURL(url);
                    //return getBitmapFromURL("http://140.121.137.185:7171/?action=snapshot");
                }

                @Override
                protected void onPostExecute(Bitmap result) {
                    super.onPostExecute(result);
                    //img.setImageBitmap(result);
                    img.setImageBitmap(toRoundCorner(result,200.f));
                    //img2.setImageBitmap(result);
                    img2.setImageBitmap(toRoundCorner(result,200.f));
                }
            }.execute(UrlSrc);//http://140.121.137.185:7171/?action=snapshot
        }
    }

    private static Bitmap getBitmapFromURL(String imageUrl) {
        try
        {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private class MyTask extends TimerTask{

        @Override
        public void run() {
            handler2.sendMessage(new Message());
        }
    }

    private class MyHandler2 extends Handler{

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            txv2.setText("fps="+fps);
            fps = 0;
        }
    }

    public static Bitmap toRoundCorner(Bitmap bitmap, float pixels) {
        System.out.println("图片是否变成圆角模式了+++++++++++++");
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        System.out.println("pixels+++++++" + pixels);

        return output;
    }
}

