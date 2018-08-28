package com.example.owner.test1;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements LocationListener, Runnable {

    LocationManager locationManager;
    TextView statusView;
    private final int REQUEST_PERMISSION = 1000;
    String id = "";
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if( Build.VERSION.SDK_INT >= 23 ){  // Android 6, API 23以上でパーミッシンの確認
            checkPermission();
        }else{
            start();
        }
        File file = new File(this.getFilesDir(), "settings"); //ファイル名を指定
        if( file.exists() ){ //すでにファイルが存在している場合
            try{
                RandomAccessFile f = new RandomAccessFile(file, "r");
                byte[] bytes = new byte[(int)f.length()];
                f.readFully(bytes);
                f.close();
                String[] array = new String(bytes).split(","); // , で分割する(拡張性を考慮)
                id = array[0];
            } catch (Exception e) {}
            Toast.makeText(this, "あなたのIDは " + id, Toast.LENGTH_SHORT).show();
        }else{
            inputID();
        }
    }

    private void inputID() {
        final EditText editText = new EditText(MainActivity.this);
        InputFilter inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.toString().matches("^[0-9a-zA-Z._-]+$")) { //半角英数字と._-に限定する
                    return source;
                } else {
                    return "";
                }
            }
        };
        // フィルターの配列を作成しセットする
        InputFilter[] filters = new InputFilter[]{inputFilter};
        editText.setFilters(filters);
        editText.setText(id);

        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("IDを入力してください")
                .setView(editText)  //setViewにてビューを設定
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        id = editText.getText().toString();
                        if (id.length() == 0) id = "none";
                        File file = new File(MainActivity.this.getFilesDir(), "settings");
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            out.write((id + ",").getBytes());
                            out.close();
                        } catch (Exception e) {
                        }
                        Toast.makeText(MainActivity.this, "あなたのIDは " + id, Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }


    private void start() {
        statusView = (TextView) findViewById(R.id.textView);
        statusView.setMovementMethod(ScrollingMovementMethod.getInstance());
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    protected void onResume() {
        if (locationManager != null) {
            // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this); // 本運用
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
        }
        super.onResume();
        thread = new Thread(this);  // ← ★★★ この2行を追加してください ★★★
        thread.start();             // ← ★★★ この2行を追加してください ★★★

    }

    protected void onPause() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        thread = null; // ← ★★★ この1行を追加してください ★★★
        super.onPause();
    }

//    Threadを追加してダウンロード
    public void run() {
        while( thread!=null ){
            Http ht  = new Http("http://pbl.jp/getloc.php", 1);
            ht.execute();
            try {
                Thread.sleep(5000);   // ← ★★★ 5秒間隔に設定 ★★★
            } catch (Exception e) {}
        }
    }

    /* LocationListener Methods */
    @Override
    public void onLocationChanged(Location location) {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        double alt = location.getAltitude();
        double acc = location.getAccuracy();
        float speed = location.hasSpeed() ? location.getSpeed() : Float.NaN;
        float bearing = location.hasBearing() ? location.getBearing() : Float.NaN;

        String time = "00000" + (hour * 10000 + minute * 100 + second);
        time = time.substring(time.length() - 6);
        statusView.setText(time + "," + lat + ","  + lng + "," + alt + "," + acc + "," + speed + "," + bearing);

        Http ht  = new Http("http://pbl.jp/now.php?"+id+","+time+","+lat+","+lng+","+alt+","+acc+","+ speed, 0); //←★★★追加★★★
        ht.execute();                                                                                            //←★★★追加★★★
//        statusView.setText(id + "," + time + "," + lat + "," + lng + "," + alt + "," + acc + "," + speed + "," + bearing);  // ←★★★ id + "," + を追加 ★★★
    
    }

    @Override
    public void onProviderDisabled(String provider) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    // 位置情報許可の確認
    public void checkPermission() {
        if( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            start();  // 許可されているときの処理
        }else{ // 拒否していた場合は許可を求める
            if( ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) ){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
            }else{
                Toast.makeText(this, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);
            }
        }
    }

    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if( requestCode == REQUEST_PERMISSION ){   // 使用が許可された
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                start();
                return;
            }else{ // それでも拒否された時の対応
                Toast.makeText(this, "これ以上なにもできません", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    データ送信
    public class Http extends AsyncTask<String, String, String> {
        private URL url;
        private int flag;
        public Http(String urltext, int flag) {
            try{
                this.url = new URL(urltext);
            }
            catch(Exception e){}
            this.flag = flag;
        }
        @Override
        public String doInBackground(String...params) {
            String t = "";
            try{
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setInstanceFollowRedirects(false);
                con.setRequestProperty("Accept-Language", "jp");
                con.connect();

                InputStream in = con.getInputStream();
                byte bodyByte[] = new byte[1024000];

                int len, readPos = 0;
                while(true){
                    len=in.read(bodyByte, readPos, 1024000 - readPos);
                    if( len < 0 ) break;
                    readPos += len;
                }
                in.close();
                con.disconnect();
                t = new String(bodyByte, "UTF-8");
            }
            catch(Exception e){}
            return t;
        }
        @Override
        protected void onPostExecute(String result){ //自動的に呼ばれるので自分で呼び出さないこと！
            if( flag==1 ){ //戻り値を利用する(getloc.phpを呼び出す)場合
                statusView.setText(result);
            }
        }
    }

}