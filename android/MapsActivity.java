package com.example.owner.googlemapstest;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

//バックグラウンド実行
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import android.widget.Toast;

//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
//
//    private GoogleMap mMap;
//    private final int REQUEST_PERMISSION = 1000;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//        if( Build.VERSION.SDK_INT >= 23 ){  // Android 6, API 23以上でパーミッシンの確認
//            checkPermission();
//        }else{
//            start();
//        }
//    }
//
//    public void start(){
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//    }
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        // LatLng sydney = new LatLng(-34, 151);
//    // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//    // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//
//        CameraPosition camerapos = new CameraPosition.Builder()
//                .target(new LatLng(33.845579, 132.765734)).zoom(15.5f).build();
//        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos)); // 地図の中心の変更する
//
////        愛媛大学にマーカーを設置してみよう。
//        Marker marker;
//        MarkerOptions options = new MarkerOptions()
//                .position(new LatLng(33.850514, 132.771498))
//                .title("タイトル");
//        marker = mMap.addMarker(options);
//
////        円を描くのも割と簡単です。
//        LatLng latlng = new LatLng(33.850514, 132.771498);
//        CircleOptions circleOptions = new CircleOptions().center(latlng)
//                .radius(100).strokeColor(Color.BLUE).strokeWidth(1.0f);
//        Circle circle = mMap.addCircle(circleOptions);
////        削除は
////        circle.remove();
////        Google Map上への線の描き方
//        PolylineOptions popt = new PolylineOptions();
//        popt.add(new LatLng(35.689488, 139.691706)); // 東京
//        popt.add(new LatLng(34.052234, -118.243685)); // ロサンゼルス
//        popt.add(new LatLng(33.850514, 132.771498)); //愛媛大学
//        popt.color(0x8000ffff);  //ARGBカラーを指定 (Aは透明度)
//        popt.width(10);
//        Polyline polyline = mMap.addPolyline(popt);
////        削除は polyline.remove();
//
////        Google Map上へのテキスト文字列の表示
//        Paint w_paint = new Paint();
//        w_paint.setAntiAlias(true);
//        w_paint.setColor(Color.argb(128,255,0,0));
//        w_paint.setTextSize(32);
//        String txt = "ここが愛媛大学だよ";
//        w_paint.getTextBounds(txt, 0, txt.length(), new Rect());
//        Paint.FontMetrics fm = w_paint.getFontMetrics();//フォントマトリックス
//        int mtw = (int) w_paint.measureText(txt);//幅
//        int fmHeight = (int) (Math.abs(fm.top) + fm.bottom);//高さ
//        Bitmap bmp = Bitmap.createBitmap(mtw, fmHeight, Bitmap.Config.ARGB_8888);
//        Canvas cv = new Canvas(bmp);
//        cv.drawText(txt, 0, Math.abs(fm.ascent), w_paint);
//        MarkerOptions mopt = new MarkerOptions().position(new LatLng(33.850514, 132.771498)).icon(BitmapDescriptorFactory.fromBitmap(bmp));
//        marker = mMap.addMarker(mopt);
//
//
//        mMap.setMyLocationEnabled(true);
//
//    }
//
//    // 位置情報許可の確認
//    public void checkPermission() {
//        if(  ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
//            start();  // 許可されているときの処理
//        }else{ // 拒否していた場合は許可を求める
//            if( ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ){
//                ActivityCompat.requestPermissions(MapsActivity.this,
//                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
//            }else{
//                Toast.makeText(this, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT).show();
//                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);
//            }
//        }
//    }
//
//    // 結果の受け取り
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if( requestCode == REQUEST_PERMISSION ){   // 使用が許可された
//            if( grantResults[0] == PackageManager.PERMISSION_GRANTED ){
//                start();
//                return;
//            }else{ // それでも拒否された時の対応
//                Toast.makeText(this, "これ以上なにもできません", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//}

/*
* level2 現在地の送信
*
* */
//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
//
//    private GoogleMap mMap;
//    private final int REQUEST_PERMISSION = 1000;
//    LocationManager locationManager;
//    String id = "";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//        if( Build.VERSION.SDK_INT >= 23 ){  // Android 6, API 23以上でパーミッシンの確認
//            checkPermission();
//        }else{
//            start();
//        }
//        File file = new File(this.getFilesDir(), "settings"); //ファイル名を指定
//        if( file.exists() ){ //すでにファイルが存在している場合
//            try{
//                RandomAccessFile f = new RandomAccessFile(file, "r");
//                byte[] bytes = new byte[(int)f.length()];
//                f.readFully(bytes);
//                f.close();
//                String[] array = new String(bytes).split(","); // , で分割する(拡張性を考慮)
//                id = array[0];
//            } catch (Exception e) {}
//            Toast.makeText(this, "あなたのIDは " + id, Toast.LENGTH_SHORT).show();
//        }else{
//            inputID();
//        }
//    }
//
//    private void inputID() {
//        final EditText editText = new EditText(MapsActivity.this);
//        InputFilter inputFilter = new InputFilter() {
//            @Override
//            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                if (source.toString().matches("^[0-9a-zA-Z._-]+$")) { //半角英数字と._-に限定する
//                    return source;
//                } else {
//                    return "";
//                }
//            }
//        };
//        // フィルターの配列を作成しセットする
//        InputFilter[] filters = new InputFilter[]{inputFilter};
//        editText.setFilters(filters);
//        editText.setText(id);
//
//        new AlertDialog.Builder(MapsActivity.this)
//                .setIcon(android.R.drawable.ic_dialog_info)
//                .setTitle("IDを入力してください")
//                .setView(editText)  //setViewにてビューを設定
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        id = editText.getText().toString();
//                        if (id.length() == 0) id = "none";
//                        File file = new File(MapsActivity.this.getFilesDir(), "settings");
//                        try {
//                            FileOutputStream out = new FileOutputStream(file);
//                            out.write((id + ",").getBytes());
//                            out.close();
//                        } catch (Exception e) {
//                        }
//                        Toast.makeText(MapsActivity.this, "あなたのIDは " + id, Toast.LENGTH_LONG).show();
//                    }
//                })
//                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                    }
//                })
//                .show();
//    }
//
//    public void start(){
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (locationManager != null) {
//            // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
//        }
//    }
//
//    protected void onPause() {
//        if (locationManager != null) {
//            locationManager.removeUpdates(this);
//        }
//        super.onPause();
//    }
//
//    public class Http extends AsyncTask<String, String, String> {
//        private URL url;
//        private int flag;
//        public Http(String urltext, int flag) {
//            try{
//                this.url = new URL(urltext);
//            }
//            catch(Exception e){}
//            this.flag = flag;
//        }
//        @Override
//        public String doInBackground(String...params) {
//            String t = "";
//            try{
//                HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                con.setRequestMethod("GET");
//                con.setInstanceFollowRedirects(false);
//                con.setRequestProperty("Accept-Language", "jp");
//                con.connect();
//
//                InputStream in = con.getInputStream();
//                byte bodyByte[] = new byte[1024000];
//
//                int len, readPos = 0;
//                while(true){
//                    len=in.read(bodyByte, readPos, 1024000 - readPos);
//                    if( len < 0 ) break;
//                    readPos += len;
//                }
//                in.close();
//                con.disconnect();
//                t = new String(bodyByte, "UTF-8");
//            }
//            catch(Exception e){}
//            return t;
//        }
//        @Override
//        protected void onPostExecute(String result){ //自動的に呼ばれるので自分で呼び出さないこと！
//            if( flag==1 ){ //戻り値を利用する(getloc.phpを呼び出す)場合
//                // statusView.setText(result);
//            }
//        }
//    }
//
//    Circle circle = null;
//
//    /* LocationListener Methods */
//    @Override
//    public void onLocationChanged(Location location) {
//        Calendar cal = Calendar.getInstance();
//        int hour = cal.get(Calendar.HOUR_OF_DAY);
//        int minute = cal.get(Calendar.MINUTE);
//        int second = cal.get(Calendar.SECOND);
//        double lat = location.getLatitude();
//        double lng = location.getLongitude();
//        double alt = location.getAltitude();
//        double acc = location.getAccuracy();
//        float speed = location.hasSpeed() ? location.getSpeed() : Float.NaN;
//        float bearing = location.hasBearing() ? location.getBearing() : Float.NaN;
//
//        String time = "00000" + (hour * 10000 + minute * 100 + second);
//        time = time.substring(time.length() - 6);
//
//        if( circle!=null ) circle.remove();
//        LatLng latlng = new LatLng(lat, lng);
//        CircleOptions circleOptions = new CircleOptions().center(latlng)
//                .radius(acc).strokeColor(Color.GREEN).strokeWidth(1.0f);
//        circle = mMap.addCircle(circleOptions);
//
//        Http ht  = new Http("http://pbl.jp/now.php?"+id+","+time+","+lat+","+lng+","+alt+","+acc+","+ speed, 0);
//        ht.execute();
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) { }
//
//    @Override
//    public void onProviderEnabled(String provider) { }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) { }
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//
//        mMap.setMyLocationEnabled(true);
//    }
//
//    // 位置情報許可の確認
//    public void checkPermission() {
//        if(  ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
//            start();  // 許可されているときの処理
//        }else{ // 拒否していた場合は許可を求める
//            if( ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ){
//                ActivityCompat.requestPermissions(MapsActivity.this,
//                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
//            }else{
//                Toast.makeText(this, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT).show();
//                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);
//            }
//        }
//    }
//
//    // 結果の受け取り
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if( requestCode == REQUEST_PERMISSION ){   // 使用が許可された
//            if( grantResults[0] == PackageManager.PERMISSION_GRANTED ){
//                start();
//                return;
//            }else{ // それでも拒否された時の対応
//                Toast.makeText(this, "これ以上なにもできません", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//}

/*
* 仲間の位置表示 level3
*
* */

//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, Runnable  {
//
//    private GoogleMap mMap;
//    private final int REQUEST_PERMISSION = 1000;
//    LocationManager locationManager;
//    String id = "";
//    private Marker[] marker = new Marker[50];
//    private LatLng[] latlng = new LatLng[50];
//    private Circle[] circle = new Circle[50];
//    int[] col = {0xff0000ff,0xff00a000,0xffff0000,0xff00ffff,0xffff00ff,0xffa0a000,0xff00ff00,0xff00a0ff,0xff8080ff,0xff666666,0xffff80ff};
//    private int idcnt = 0;
//    String result;
//    double lat, lng, alt, acc;
//    private Thread thread;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//        if( Build.VERSION.SDK_INT >= 23 ){  // Android 6, API 23以上でパーミッシンの確認
//            checkPermission();
//        }else{
//            start();
//        }
//        File file = new File(this.getFilesDir(), "settings"); //ファイル名を指定
//        if( file.exists() ){ //すでにファイルが存在している場合
//            try{
//                RandomAccessFile f = new RandomAccessFile(file, "r");
//                byte[] bytes = new byte[(int)f.length()];
//                f.readFully(bytes);
//                f.close();
//                String[] array = new String(bytes).split(","); // , で分割する(拡張性を考慮)
//                id = array[0];
//            } catch (Exception e) {}
//            Toast.makeText(this, "あなたのIDは " + id, Toast.LENGTH_SHORT).show();
//        }else{
//            inputID();
//        }
//    }
//
//    private void inputID() {
//        final EditText editText = new EditText(MapsActivity.this);
//        InputFilter inputFilter = new InputFilter() {
//            @Override
//            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                if (source.toString().matches("^[0-9a-zA-Z._-]+$")) { //半角英数字と._-に限定する
//                    return source;
//                } else {
//                    return "";
//                }
//            }
//        };
//        // フィルターの配列を作成しセットする
//        InputFilter[] filters = new InputFilter[]{inputFilter};
//        editText.setFilters(filters);
//        editText.setText(id);
//
//        new AlertDialog.Builder(MapsActivity.this)
//                .setIcon(android.R.drawable.ic_dialog_info)
//                .setTitle("IDを入力してください")
//                .setView(editText)  //setViewにてビューを設定
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        id = editText.getText().toString();
//                        if (id.length() == 0) id = "none";
//                        File file = new File(MapsActivity.this.getFilesDir(), "settings");
//                        try {
//                            FileOutputStream out = new FileOutputStream(file);
//                            out.write((id + ",").getBytes());
//                            out.close();
//                        } catch (Exception e) {
//                        }
//                        Toast.makeText(MapsActivity.this, "あなたのIDは " + id, Toast.LENGTH_LONG).show();
//                    }
//                })
//                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                    }
//                })
//                .show();
//    }
//
//    public void start(){
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (locationManager != null) {
//            // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
//        }
//        thread = new Thread(this);  // ← ★★★ この2行を追加してください ★★★
//        thread.start();             // ← ★★★ この2行を追加してください ★★★
//    }
//
//    protected void onPause() {
//        if (locationManager != null) {
//            locationManager.removeUpdates(this);
//        }
//        thread = null; // ← ★★★ この1行を追加してください ★★★
//        super.onPause();
//    }
//
//    public class Http extends AsyncTask<String, String, String> {
//        private URL url;
//        private int flag;
//        public Http(String urltext, int flag) {
//            try{
//                this.url = new URL(urltext);
//            }
//            catch(Exception e){}
//            this.flag = flag;
//        }
//        @Override
//        public String doInBackground(String...params) {
//            String t = "";
//            try{
//                HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                con.setRequestMethod("GET");
//                con.setInstanceFollowRedirects(false);
//                con.setRequestProperty("Accept-Language", "jp");
//                con.connect();
//
//                InputStream in = con.getInputStream();
//                byte bodyByte[] = new byte[1024000];
//
//                int len, readPos = 0;
//                while(true){
//                    len=in.read(bodyByte, readPos, 1024000 - readPos);
//                    if( len < 0 ) break;
//                    readPos += len;
//                }
//                in.close();
//                con.disconnect();
//                t = new String(bodyByte, "UTF-8");
//            }
//            catch(Exception e){}
//            return t;
//        }
//        String _result;
//        @Override
//        protected void onPostExecute(String result){ //自動的に呼ばれるので自分で呼び出さないこと！
//            if( flag==1 ){ //戻り値を利用する(getloc.phpを呼び出す)場合
//                // statusView.setText(result);
//                if( result.length() > 0 ) { // 通信失敗時は result が "" となり、split でエラーになるので回避
//                    _result = result;
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            for (int i = 0; i < idcnt; i++) {
//                                marker[i].remove();
//                                circle[i].remove();
//                            }
//                            String[] array = _result.split("\n");
//                            idcnt = array.length;
//                            if( idcnt > 1 ) idcnt --;;
//                            for (int i = 0; i < idcnt; i++) {
//                                String[] locaStr = array[i].split(",");
//                                latlng[i] = new LatLng(Double.valueOf(locaStr[3]), Double.valueOf(locaStr[4]));
//                                String _id = locaStr[0];
//                                String jikan = locaStr[2].substring(0, 2) + ":" + locaStr[2].substring(2, 4) + ":" + locaStr[2].substring(4);
//                                float[] results = new float[1];
//                                String kyori = "―";
//                                if( _id.equals(id) ) kyori = "自分";
//                                else if (lat > 0) {
//                                    Location.distanceBetween(lat, lng,
//                                            Double.valueOf(locaStr[3]), Double.valueOf(locaStr[4]), results);
//                                    if (results != null && results.length > 0) {
//                                        kyori = String.valueOf((int) ((double) results[0] + 0.5)) + "m";
//                                    }
//                                }
//                                Paint w_paint = new Paint();
//                                w_paint.setAntiAlias(true);
//                                w_paint.setColor(col[i % col.length]);
//                                w_paint.setTextSize(32);
//                                String txt = jikan + " " + _id + " " + kyori;
//                                w_paint.getTextBounds(txt, 0, txt.length(), new Rect());
//                                Paint.FontMetrics fm = w_paint.getFontMetrics();//フォントマトリックス
//                                int mtw = (int) w_paint.measureText(txt);//幅
//                                int fmHeight = (int) (Math.abs(fm.top) + fm.bottom);//高さ
//                                Bitmap bmp = Bitmap.createBitmap(mtw, fmHeight, Bitmap.Config.ARGB_8888);
//                                Canvas cv = new Canvas(bmp);
//                                cv.drawText(txt, 0, Math.abs(fm.ascent), w_paint);
//                                MarkerOptions options = new MarkerOptions().position(latlng[i]).icon(BitmapDescriptorFactory.fromBitmap(bmp));
//                                marker[i] = mMap.addMarker(options);
//                                CircleOptions circleOptions = new CircleOptions().center(latlng[i]).radius(Float.valueOf(locaStr[6]))
//                                        .strokeColor(col[i % col.length]).strokeWidth(2.0f);
//                                circle[i] = mMap.addCircle(circleOptions);
//                            }
//                        }
//                    });
//                }
//            }
//        }
//    }
//
//    public void run() {
//        while( thread != null ) {
//            result = "";
//            Http ht  = new Http("http://pbl.jp/getloc.php", 1);
//            ht.execute();
//            try{
//                Thread.sleep(5000);
//            }catch(Exception e){}
//        }
//    }
//
//    /* LocationListener Methods */
//    @Override
//    public void onLocationChanged(Location location) {
//        Calendar cal = Calendar.getInstance();
//        int hour = cal.get(Calendar.HOUR_OF_DAY);
//        int minute = cal.get(Calendar.MINUTE);
//        int second = cal.get(Calendar.SECOND);
//        double lat = location.getLatitude();
//        double lng = location.getLongitude();
//        double alt = location.getAltitude();
//        double acc = location.getAccuracy();
//        float speed = location.hasSpeed() ? location.getSpeed() : Float.NaN;
//        float bearing = location.hasBearing() ? location.getBearing() : Float.NaN;
//
//        String time = "00000" + (hour * 10000 + minute * 100 + second);
//        time = time.substring(time.length() - 6);
//
//        Http ht  = new Http("http://pbl.jp/now.php?"+id+","+time+","+lat+","+lng+","+alt+","+acc+","+ speed, 0);
//        ht.execute();
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) { }
//
//    @Override
//    public void onProviderEnabled(String provider) { }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) { }
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        mMap.setMyLocationEnabled(true);
//    }
//
//    // 位置情報許可の確認
//    public void checkPermission() {
//        if(  ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
//            start();  // 許可されているときの処理
//        }else{ // 拒否していた場合は許可を求める
//            if( ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ){
//                ActivityCompat.requestPermissions(MapsActivity.this,
//                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
//            }else{
//                Toast.makeText(this, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT).show();
//                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);
//            }
//        }
//    }
//
//    // 結果の受け取り
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if( requestCode == REQUEST_PERMISSION ){   // 使用が許可された
//            if( grantResults[0] == PackageManager.PERMISSION_GRANTED ){
//                start();
//                return;
//            }else{ // それでも拒否された時の対応
//                Toast.makeText(this, "これ以上なにもできません", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//}

/*
*
* serviceを使ってバックグラウンド実行
* */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, Runnable  {

    private GoogleMap mMap;
    private final int REQUEST_PERMISSION = 1000;
    String id = "";
    private Marker[] marker = new Marker[50];
    private LatLng[] latlng = new LatLng[50];
    private Circle[] circle = new Circle[50];
    int[] col = {0xff0000ff,0xff00a000,0xffff0000,0xff00ffff,0xffff00ff,0xffa0a000,0xff00ff00,0xff00a0ff,0xff8080ff,0xff666666,0xffff80ff};
    private int idcnt = 0;
    String result;
    GPSService gpsService = null;
    Intent serviceIntent;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        serviceIntent = new Intent(this, GPSService.class);
        startService(serviceIntent);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if( Build.VERSION.SDK_INT >= 23 ){  // Android 6, API 23以上でパーミッシンの確認
            checkPermission();
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
        final EditText editText = new EditText(MapsActivity.this);
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

        new AlertDialog.Builder(MapsActivity.this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("IDを入力してください")
                .setView(editText)  //setViewにてビューを設定
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        id = editText.getText().toString();
                        if( id.length() == 0 ) id = "none";
                        File file = new File(MapsActivity.this.getFilesDir(), "settings");
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            out.write((id + ",").getBytes());
                            out.close();
                        } catch (Exception e) {
                        }
                        Toast.makeText(MapsActivity.this, "あなたのIDは " + id, Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        thread = new Thread(this);  // ← ★★★ この2行を追加してください ★★★
        thread.start();              // ← ★★★ この2行を追加してください ★★★
    }

    protected void onPause() {
        unbindService(serviceConnection);
        thread = null; // ← ★★★ この1行を追加してください ★★★
        super.onPause();
    }

    public class Http extends AsyncTask<String, String, String> {
        private URL url;
        private Boolean flag;
        public Http(String urltext, Boolean flag) {
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

        String _result;
        @Override
        protected void onPostExecute(String result){ //自動的に呼ばれるので自分で呼び出さないこと！
            if( flag ){ //戻り値を利用する(getloc.phpを呼び出す)場合
                // statusView.setText(result);
                if( result.length() > 0 ) { // 通信失敗時は result が "" となり、split でエラーになるので回避
                    _result = result;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < idcnt; i++) {
                                marker[i].remove();
                                circle[i].remove();
                            }
                            String[] array = _result.split("\n");
                            idcnt = array.length;
                            if( idcnt > 1 ) idcnt --;;
                            for (int i = 0; i < idcnt; i++) {
                                String[] locaStr = array[i].split(",");
                                latlng[i] = new LatLng(Double.valueOf(locaStr[3]), Double.valueOf(locaStr[4]));
                                String _id = locaStr[0];
                                String jikan = locaStr[2].substring(0, 2) + ":" + locaStr[2].substring(2, 4) + ":" + locaStr[2].substring(4);
                                float[] results = new float[1];
                                String kyori = "―";
                                if (locaStr[0].equals(id)) kyori = "自分";
                                else if( gpsService != null && gpsService.lat > 0 ) {
                                    Location.distanceBetween(gpsService.lat, gpsService.lng,
                                            Double.valueOf(locaStr[3]), Double.valueOf(locaStr[4]), results);
                                    if (results != null && results.length > 0) {
                                        kyori = String.valueOf((int) ((double) results[0] + 0.5)) + "m";
                                    }
                                }
                                Paint w_paint = new Paint();
                                w_paint.setAntiAlias(true);
                                w_paint.setColor(col[i % col.length]);
                                w_paint.setTextSize(32);
                                String txt = jikan + " " + _id + " " + kyori;
                                w_paint.getTextBounds(txt, 0, txt.length(), new Rect());
                                Paint.FontMetrics fm = w_paint.getFontMetrics();//フォントマトリックス
                                int mtw = (int) w_paint.measureText(txt);//幅
                                int fmHeight = (int) (Math.abs(fm.top) + fm.bottom);//高さ
                                Bitmap bmp = Bitmap.createBitmap(mtw, fmHeight, Bitmap.Config.ARGB_8888);
                                Canvas cv = new Canvas(bmp);
                                cv.drawText(txt, 0, Math.abs(fm.ascent), w_paint);
                                MarkerOptions options = new MarkerOptions().position(latlng[i]).icon(BitmapDescriptorFactory.fromBitmap(bmp));
                                marker[i] = mMap.addMarker(options);
                                CircleOptions circleOptions = new CircleOptions().center(latlng[i]).radius(Float.valueOf(locaStr[6]))
                                        .strokeColor(col[i % col.length]).strokeWidth(2.0f);
                                circle[i] = mMap.addCircle(circleOptions);
                            }
                        }
                    });
                }
            }
        }
    }

    public void run() {
        while( thread != null ) {
            result = "";
            Http ht  = new Http("http://pbl.jp/getloc.php", true);
            ht.execute();
            try{
                Thread.sleep(5000);
            }catch(Exception e){}
        }
    }
    // 位置情報許可の確認
    public void checkPermission() {
        if(  ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            // 許可されているときの処理は特になし
        }else{ // 拒否していた場合は許可を求める
            if( ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ){
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
            }else{
                Toast.makeText(this, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);
            }
        }
    }

    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if( requestCode == REQUEST_PERMISSION ){   // 使用が許可された
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                return;
            }else{ // それでも拒否された時の対応
                Toast.makeText(this, "これ以上なにもできません", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            gpsService = ((GPSService.GPSServiceBinder)service).getService();
            gpsService.id = id;
            gpsService.startTracking(10000);
        }
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            gpsService = null;
        }
    };
}

/*
*
*
* GPS応用編
* */
//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, Runnable  {
//
//    private GoogleMap mMap;
//    private final int REQUEST_PERMISSION = 1000;
//    String id = "";
//    private Marker[] marker = new Marker[50];
//    private String[] markertitle = new String[50];  // ★★★この1行追加
//    private LatLng[] latlng = new LatLng[50];
//    private Circle[] circle = new Circle[50];
//    int[] col = {0xff0000ff,0xff00a000,0xffff0000,0xff00ffff,0xffff00ff,0xffa0a000,0xff00ff00,0xff00a0ff,0xff8080ff,0xff666666,0xffff80ff};
//    private int idcnt = 0;
//    String result;
//    GPSService gpsService = null;
//    Intent serviceIntent;
//    private Thread thread;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//
//        serviceIntent = new Intent(this, GPSService.class);
//        startService(serviceIntent);
//
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//        if( Build.VERSION.SDK_INT >= 23 ){  // Android 6, API 23以上でパーミッシンの確認
//            checkPermission();
//        }
//
//        File file = new File(this.getFilesDir(), "settings"); //ファイル名を指定
//        if( file.exists() ){ //すでにファイルが存在している場合
//            try{
//                RandomAccessFile f = new RandomAccessFile(file, "r");
//                byte[] bytes = new byte[(int)f.length()];
//                f.readFully(bytes);
//                f.close();
//                String[] array = new String(bytes).split(","); // , で分割する(拡張性を考慮)
//                id = array[0];
//            } catch (Exception e) {}
//            Toast.makeText(this, "あなたのIDは " + id, Toast.LENGTH_SHORT).show();
//        }else{
//            inputID();
//        }
//    }
//
//    private void inputID() {
//        final EditText editText = new EditText(MapsActivity.this);
//        InputFilter inputFilter = new InputFilter() {
//            @Override
//            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                if (source.toString().matches("^[0-9a-zA-Z._-]+$")) { //半角英数字と._-に限定する
//                    return source;
//                } else {
//                    return "";
//                }
//            }
//        };
//        // フィルターの配列を作成しセットする
//        InputFilter[] filters = new InputFilter[]{inputFilter};
//        editText.setFilters(filters);
//        editText.setText(id);
//
//        new AlertDialog.Builder(MapsActivity.this)
//                .setIcon(android.R.drawable.ic_dialog_info)
//                .setTitle("IDを入力してください")
//                .setView(editText)  //setViewにてビューを設定
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        id = editText.getText().toString();
//                        if( id.length() == 0 ) id = "none";
//                        File file = new File(MapsActivity.this.getFilesDir(), "settings");
//                        try {
//                            FileOutputStream out = new FileOutputStream(file);
//                            out.write((id + ",").getBytes());
//                            out.close();
//                        } catch (Exception e) {
//                        }
//                        Toast.makeText(MapsActivity.this, "あなたのIDは " + id, Toast.LENGTH_LONG).show();
//                    }
//                })
//                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                    }
//                })
//                .show();
//    }
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        // タップ時のイベントハンドラ登録
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng point) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
//                intent.setData(Uri.parse("http://maps.google.com/maps?saddr="+gpsService.lat+","+gpsService.lng+"&daddr="+point.latitude+","+point.longitude));
//                startActivity(intent);
//            }
//        });
//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker _marker) {
//                Toast.makeText(getApplicationContext(), "マーカータップ", Toast.LENGTH_LONG).show();
//                for(int i=0;i<idcnt;i++){
//                    if( _marker.equals(marker[i]) ) {
//                        Calendar cal = Calendar.getInstance();    // 今日の日付（例：20160807） の文字列を生成するルーチン
//                        int yyyy = cal.get(Calendar.YEAR);
//                        int mm = cal.get(Calendar.MONTH) + 1;  // 0から11の範囲になる
//                        int dd = cal.get(Calendar.DAY_OF_MONTH);
//                        String yyyymmdd = "" + (yyyy * 10000 + mm * 100 + dd);
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://pbl.jp/log/" + yyyymmdd + "/" + markertitle[i]));
//                        startActivity(intent);
//                        break;
//                    }
//                }
//                return false;
//            }
//        });
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//        thread = new Thread(this);  // ← ★★★ この2行を追加してください ★★★
//        thread.start();              // ← ★★★ この2行を追加してください ★★★
//    }
//
//    protected void onPause() {
//        unbindService(serviceConnection);
//        thread = null; // ← ★★★ この1行を追加してください ★★★
//        super.onPause();
//    }
//
//    public class Http extends AsyncTask<String, String, String> {
//        private URL url;
//        private int flag;
//        public Http(String urltext, int flag) {
//            try{
//                this.url = new URL(urltext);
//            }
//            catch(Exception e){}
//            this.flag = flag;
//        }
//        @Override
//        public String doInBackground(String...params) {
//            String t = "";
//            try{
//                HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                con.setRequestMethod("GET");
//                con.setInstanceFollowRedirects(false);
//                con.setRequestProperty("Accept-Language", "jp");
//                con.connect();
//
//                InputStream in = con.getInputStream();
//                byte bodyByte[] = new byte[024000];
//                int len, readPos = 0;
//                while(true){
//                    len=in.read(bodyByte, readPos, 1024000 - readPos);
//                    if( len < 0 ) break;
//                    readPos += len;
//                }
//                in.close();
//                con.disconnect();
//                t = new String(bodyByte, "UTF-8");
//            }
//            catch(Exception e){}
//            return t;
//        }
//
//        String _result;
//        @Override
//        protected void onPostExecute(String result){ //自動的に呼ばれるので自分で呼び出さないこと！
//            if( flag==1 ){ //戻り値を利用する(getloc.phpを呼び出す)場合
//                // statusView.setText(result);
//                if( result.length() > 0 ) { // 通信失敗時は result が "" となり、split でエラーになるので回避
//                    _result = result;
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            for (int i = 0; i < idcnt; i++) {
//                                marker[i].remove();
//                                circle[i].remove();
//                            }
//                            String[] array = _result.split("\n");
//                            idcnt = array.length;
//                            if( idcnt > 1 ) idcnt --;;
//                            for (int i = 0; i < idcnt; i++) {
//                                String[] locaStr = array[i].split(",");
//                                latlng[i] = new LatLng(Double.valueOf(locaStr[3]), Double.valueOf(locaStr[4]));
//                                String _id = locaStr[0];
//                                String jikan = locaStr[2].substring(0, 2) + ":" + locaStr[2].substring(2, 4) + ":" + locaStr[2].substring(4);
//                                float[] results = new float[1];
//                                String kyori = "―";
//                                if( _id.equals(id) ) kyori = "自分";
//                                else if( gpsService != null && gpsService.lat > 0 ) {
//                                    Location.distanceBetween(gpsService.lat, gpsService.lng,
//                                            Double.valueOf(locaStr[3]), Double.valueOf(locaStr[4]), results);
//                                    if (results != null && results.length > 0) {
//                                        kyori = String.valueOf((int) ((double) results[0] + 0.5)) + "m";
//                                    }
//                                }
//                                Paint w_paint = new Paint();
//                                w_paint.setAntiAlias(true);
//                                w_paint.setColor(col[i % col.length]);
//                                w_paint.setTextSize(32);
//                                String txt = jikan + " " + _id + " " + kyori;
//                                w_paint.getTextBounds(txt, 0, txt.length(), new Rect());
//                                Paint.FontMetrics fm = w_paint.getFontMetrics();//フォントマトリックス
//                                int mtw = (int) w_paint.measureText(txt);//幅
//                                int fmHeight = (int) (Math.abs(fm.top) + fm.bottom);//高さ
//                                Bitmap bmp = Bitmap.createBitmap(mtw, fmHeight, Bitmap.Config.ARGB_8888);
//                                Canvas cv = new Canvas(bmp);
//                                cv.drawText(txt, 0, Math.abs(fm.ascent), w_paint);
//                                MarkerOptions options = new MarkerOptions().position(latlng[i]).icon(BitmapDescriptorFactory.fromBitmap(bmp));
//                                marker[i] = mMap.addMarker(options);
//                                markertitle[i] = _id;                 // ★★★この1行追加
//                                CircleOptions circleOptions = new CircleOptions().center(latlng[i]).radius(Float.valueOf(locaStr[6]))
//                                        .strokeColor(col[i % col.length]).strokeWidth(2.0f);
//                                circle[i] = mMap.addCircle(circleOptions);
//                            }
//                        }
//                    });
//                }
//            }
//        }
//    }
//
//    public void run() {
//        while( thread != null ) {
//            result = "";
//            Http ht  = new Http("http://pbl.jp/getloc.php", 1);
//            ht.execute();
//            try{
//                Thread.sleep(5000);
//            }catch(Exception e){}
//        }
//    }
//    // 位置情報許可の確認
//    public void checkPermission() {
//        if(  ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
//            // 許可されているときの処理は特になし
//        }else{ // 拒否していた場合は許可を求める
//            if( ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ){
//                ActivityCompat.requestPermissions(MapsActivity.this,
//                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
//            }else{
//                Toast.makeText(this, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT).show();
//                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);
//            }
//        }
//    }
//
//    // 結果の受け取り
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if( requestCode == REQUEST_PERMISSION ){   // 使用が許可された
//            if( grantResults[0] == PackageManager.PERMISSION_GRANTED ){
//                return;
//            }else{ // それでも拒否された時の対応
//                Toast.makeText(this, "これ以上なにもできません", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        public void onServiceConnected(ComponentName className, IBinder service) {
//            gpsService = ((GPSService.GPSServiceBinder)service).getService();
//            gpsService.id = id;
//            gpsService.startTracking(10000);
//        }
//        public void onServiceDisconnected(ComponentName name) {
//            // TODO Auto-generated method stub
//            gpsService = null;
//        }
//    };
//}