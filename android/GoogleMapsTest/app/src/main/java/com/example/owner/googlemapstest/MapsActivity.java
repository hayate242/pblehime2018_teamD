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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.io.IOException;
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
import java.util.HashMap;
import java.util.Iterator;

import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


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
    int[] col = {0xff0000ff,0xff00a000,0xffff0000,0xff00ffff,0xffff00ff,0xffa0a000,0xff00ff00,0xff00a0ff,0xff8080ff,0xff666666,0xffff80ff};
    private int idcnt = 0;
    String result;
    GPSService gpsService = null;
    Intent serviceIntent;
    private Thread thread;
    String team_name = "";
    String password = "";
    String members[] = new String[100];
    TextView membersText;
    Button loginBtn;

    Boolean startLoginFlag = false;
    Boolean memberFlag = false;
    Boolean getLocationFlag = false;
    Boolean firstLineFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

//        text設定
        membersText = (TextView) findViewById(R.id.membersText);
        membersText.setMovementMethod(ScrollingMovementMethod.getInstance());

//        login Button
        loginBtn = (Button)findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

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
//        if( file.exists() ){ //すでにファイルが存在している場合
        if( false ){ //debug用
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
            checkLogin("team_name");
        }
    }

    private void checkLogin( String fileneame) {
        File file = new File(this.getFilesDir(), fileneame); //ファイル名を指定
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
            login();
        }
    }


    private void login(){
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
        editText.setText(team_name);

        new AlertDialog.Builder(MapsActivity.this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("チーム名を入力してください")
                .setView(editText)  //setViewにてビューを設定
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        team_name = editText.getText().toString();
                        if( team_name.length() == 0 ) team_name = "none";
//                        File file = new File(MapsActivity.this.getFilesDir(), "team_name");
//                        try {
//                            FileOutputStream out = new FileOutputStream(file);
//                            out.write((team_name + ",").getBytes());
//                            out.close();
//                        } catch (Exception e) {
//                        }
//                        Toast.makeText(MapsActivity.this, "あなたのチーム名は " + team_name, Toast.LENGTH_LONG).show();
                        input_password();
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    private void input_password() {
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
        editText.setText(password);

        new AlertDialog.Builder(MapsActivity.this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("パスワードを入力してください")
                .setView(editText)  //setViewにてビューを設定
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        password = editText.getText().toString();
                        if( password.length() == 0 ) password = "none";

                        http_login();
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }


    private void http_login() {
        Log.d("team_name", team_name);
        Log.d("password", password);
        File file = new File(MapsActivity.this.getFilesDir(), "team_name");
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write((team_name + ",").getBytes());
            out.close();
        } catch (Exception e) {
        }

        file = new File(MapsActivity.this.getFilesDir(), "password");
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write((password + ",").getBytes());
            out.close();
        } catch (Exception e) {
        }

//        login開始
        startLoginFlag = true;
        String geturl;
        geturl = "http://pbl.jp/td/login/index.php?team_name=" + team_name + "&password=" + password + "&user_name=" + id;
        getstart(geturl);


    }

    private void getstart(String geturl) {
        Http ht  = new Http(geturl, true);
        Log.d("Get",geturl);
        ht.execute();
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
        mMap.setMyLocationEnabled(true);
        CameraPosition camerapos = new CameraPosition.Builder()
                .target(new LatLng(33.845579, 132.765734)).zoom(15.5f).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos)); // 地図の中心の変更する
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

//        jsonをぱーすしてStringに変換
        public String[] parse_json(String _result){
            try {
                JSONObject json = new JSONObject(_result);
                Log.d("HTTP REQ", String.valueOf(json));
                for( int i = 0; i < 100; i++ ){
                    members[i] = json.getString("user"+i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return members;
        }

        public void set_members(String _result){
            String members = "";
            try {
                JSONObject json = new JSONObject(_result);
                for( int i = 0; i < 100; i++ ){
                    members += json.getString("user"+i);
                    members += " ";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            membersText.setText(team_name + "のメンバー: " + members);
        }

        String _result;
        String team_members[] = new String[256];
        @Override
        protected void onPostExecute(String result){ //自動的に呼ばれるので自分で呼び出さないこと！
            if(flag){
                if( startLoginFlag ){ //戻り値を利用する(login.phpを呼び出す)場合
                    startLoginFlag = false;
                    //        memberの取得用
                    memberFlag = true;
                    getstart("http://pbl.jp/td/getMembers/index.php?team_name=" + team_name);
                    if (result.length() > 0) { // 通信失敗時は result が "" となり、split でエラーになるので回避
                        _result = result;
                        Log.d("login_result", _result);
                        if(_result.contains("ok")){
                            loginBtn.setText(team_name+": "+ id);
                            Toast.makeText(MapsActivity.this, "login成功 ", Toast.LENGTH_LONG).show();
                        }else {
                            memberFlag = false;
                            team_name = "";
                            password = "";
                            Toast.makeText(MapsActivity.this, "login失敗 ", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else if( memberFlag ) { //戻り値を利用する(getMembers.phpを呼び出す)場合
                    memberFlag = false;
                    getLocationFlag = true;
                    // statusView.setText(result);
                    if (result.length() > 0) { // 通信失敗時は result が "" となり、split でエラーになるので回避
                        _result = result;
                        Log.d("getresult", _result);

                        String[] users = parse_json(_result);
                        set_members(_result);
                    }
                }
                else if( getLocationFlag ) { //戻り値を利用する(getloc.phpを呼び出す)場合
                    // statusView.setText(result);
                    if (result.length() > 0) { // 通信失敗時は result が "" となり、split でエラーになるので回避
                        _result = result;
                        Log.d("_result", _result);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                for (int i = 0; i < idcnt; i++) {
//                                    marker[i].remove();
//                                }
                                String[] array = _result.split("\n");
                                idcnt = array.length;
                                if (idcnt > 1) idcnt--;

                                for (int i = 0; i < idcnt; i++) {
                                    String[] locaStr = array[i].split(",");
                                    latlng[i] = new LatLng(Double.valueOf(locaStr[3]), Double.valueOf(locaStr[4]));
                                    String _id = locaStr[0];
                                    String jikan = locaStr[2].substring(0, 2) + ":" + locaStr[2].substring(2, 4) + ":" + locaStr[2].substring(4);
                                    float[] results = new float[1];
                                    String kyori = "―";
                                    if (locaStr[0].equals(id)) {
                                        kyori = "自分";
                                    } else if (gpsService != null && gpsService.lat > 0) {
                                        Location.distanceBetween(gpsService.lat, gpsService.lng,
                                                Double.valueOf(locaStr[3]), Double.valueOf(locaStr[4]), results);
                                        if (results != null && results.length > 0) {
                                            kyori = "距離:" + String.valueOf((int) ((double) results[0] + 0.5)) + "m";
                                        }
                                    }
                                    Paint w_paint = new Paint();
                                    w_paint.setAntiAlias(true);
                                    w_paint.setColor(col[i % col.length]);
                                    w_paint.setTextSize(60);
                                    String txt = jikan + " " + _id + " " + kyori;
                                    w_paint.getTextBounds(txt, 0, txt.length(), new Rect());
                                    Paint.FontMetrics fm = w_paint.getFontMetrics();//フォントマトリックス
                                    int mtw = (int) w_paint.measureText(txt);//幅
                                    int fmHeight = (int) (Math.abs(fm.top) + fm.bottom);//高さ
                                    Bitmap bmp = Bitmap.createBitmap(mtw, fmHeight, Bitmap.Config.ARGB_8888);
                                    Canvas cv = new Canvas(bmp);

    //自分の通った経路を表示
                                    if (kyori.equals("自分")) {
                                        if (firstLineFlag) {
    //                                        スタート位置
                                            MarkerOptions options = new MarkerOptions()
                                                    .position(latlng[i])
                                                    .title("スタート");
                                            marker[i] = mMap.addMarker(options);
                                            firstLineFlag = false;
                                        } else {
                                            PolylineOptions popt = new PolylineOptions();
                                            popt.add(latlng[i - 1]); // ひとつ前の緯度経度
                                            popt.add(latlng[i]); // 今の緯度経度
                                            popt.color(0x8000B3FD);  //ARGBカラーを指定 (Aは透明度)
                                            popt.width(20);
                                            Polyline polyline = mMap.addPolyline(popt);
                                        }
                                    } else {
                                        cv.drawText(txt, 0, Math.abs(fm.ascent), w_paint);

                                        //                                マーカー設置、条件でアイコン変更。
                                        if (i < 5) {
                                            MarkerOptions options = new MarkerOptions().position(latlng[i]).icon(BitmapDescriptorFactory.fromResource(R.drawable.test));
                                            marker[i] = mMap.addMarker(options);
                                        } else if (i == 5) {
                                            MarkerOptions options = new MarkerOptions().position(latlng[i]).icon(BitmapDescriptorFactory.fromResource(R.drawable.test1));
                                            marker[i] = mMap.addMarker(options);
                                        }
                                        //                              それ以外のときはデフォルト
                                        else {
                                            MarkerOptions options = new MarkerOptions().position(latlng[i]);
                                            marker[i] = mMap.addMarker(options);
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    public void run() {
        while( thread != null ) {
            result = "";
            String geturl = "";
            Http ht;

            if(getLocationFlag) {
//                geturl = "http://pbl.jp/td/getLocation/index.php?user_names=";
                geturl = "http://pbl.jp/td/getLocations/index.php?team_name="+ team_name;
                Log.d("Get",geturl);
            }
            ht  = new Http(geturl, true);
            Log.d("Get",geturl);
            ht.execute();
            try{
                Thread.sleep(5000);
            }catch(Exception e){}
        }
    }
    // 位置情報許可の確認
    public void checkPermission() {
        if(  ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            // 許可されているときの処理 locationManager登録

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