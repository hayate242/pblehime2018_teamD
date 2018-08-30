package com.example.owner.googlemapstest;

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
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class GPSService extends Service implements LocationListener {
    String id = "";
    int minTime = 10000;
    int minDistance = 0;
    boolean isTracking = false;
    int hour = 0, minute = 0 , second = 0;
    double lat = 0, lng = 0, alt = 0, acc = 0;
    double prev_lat = 0, prev_lng = 0;
    float speed, bearing;
    private LocationManager manager;
    private final IBinder binder = new GPSServiceBinder();
    int firstLocationChangedHour = 0;
    int firstLocationChangedMin = 0;
    int firstLocationChangedsec = 0;

    Boolean firstLocationChangedFlag = true;

    /* Service Setup Methods */
    @Override
    public void onCreate() {
        Toast.makeText(this, "GPSService onCreate", Toast.LENGTH_SHORT).show();
        manager = (LocationManager)getSystemService(LOCATION_SERVICE);
        IntentFilter filter = new IntentFilter();
        startTracking(minTime);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "GPSService onStratCommand", Toast.LENGTH_SHORT).show();
        return START_REDELIVER_INTENT;
    }

    public void startTracking(int _minTime) {
        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) return;
        minTime = _minTime;
        if( isTracking ) manager.removeUpdates(this);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
        isTracking = true;
        Toast.makeText(this, "Starting GPW every " + (minTime / 1000) + " sec.", Toast.LENGTH_SHORT).show();
    }

    public void stopTracking() {
        if( !isTracking ) return;
        isTracking = false;
        manager.removeUpdates(this);
        hour = minute = second = 0;
        lat = lng = alt = acc = 0.0f;
        speed = bearing = 0.0f;
    }

    @Override
    public void onDestroy() {
        manager.removeUpdates(this);
        manager.removeGpsStatusListener((GpsStatus.Listener)this);
    }

    /* Service Access Methods */
    public class GPSServiceBinder extends Binder {
        GPSService getService() {
            return GPSService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
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
                byte bodyByte[] = new byte[16384];

                int len, readPos = 0;
                while(true){
                    len=in.read(bodyByte, readPos, 16384 - readPos);
                    if( len <0 ) break;
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
            }
        }
    }

    /**
     * 現在日時をyyyy/MM/dd HH:mm:ss形式で取得する.<br>
     */
    public void getNowTime(){
        String temp = "";
//        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final DateFormat df = new SimpleDateFormat("HHmmss");
        final Date date = new Date(System.currentTimeMillis());
        Log.d("currentTime", String.valueOf(date));
        temp = df.format(date);
        firstLocationChangedHour = Integer.parseInt(temp.substring(0,2));
        firstLocationChangedMin = Integer.parseInt(temp.substring(2,4));
        firstLocationChangedsec = Integer.parseInt(temp.substring(4,6));
    }


    /* LocationListener Methods */
    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "location");

        if(firstLocationChangedFlag){
            getNowTime();
            firstLocationChangedFlag = false;
        }
        Calendar cal = Calendar.getInstance();
        // cal.setTimeInMillis(location.getTime());
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
        second = cal.get(Calendar.SECOND);
        lat = location.getLatitude();
        lng = location.getLongitude();
        alt = location.getAltitude();
        acc = location.getAccuracy();
        speed = location.hasSpeed() ? location.getSpeed() : Float.NaN;
        bearing = location.hasBearing() ? location.getBearing() : Float.NaN;

        float[] results = new float[1];
        Location.distanceBetween(prev_lat, prev_lng, lat, lng, results); //前回からの移動距離

        String time = "00000" + (hour * 10000 + minute * 100 + second);
        time = time.substring(time.length()-6);

        if(get_ID()){
            Http ht = new Http("http://pbl.jp/td/setLocation/index.php?" +
                    id + "," + time + "," + lat + ","  + lng + "," + alt+ "," + acc + "," + speed, false);
            ht.execute();
            Log.d("Get","http://pbl.jp/td/setLocation/index.php?" +
                    id + "," + time + "," + lat + ","  + lng + "," + alt+ "," + acc + "," + speed);
        }

        int sh = hour - firstLocationChangedHour;
        int sm = minute - firstLocationChangedMin;
        int ss = second - firstLocationChangedsec;

        double calory = 8*(sh+(double)sm/(double)60+(double)ss/(double)3600)*1.05*60;

//        統計情報を追加
        double dist = MapsActivity.latlngcreate(lat,lng,calory);





        prev_lat = lat;
        prev_lng = lng;
    }

    private Boolean get_ID() {
        File file = new File(this.getFilesDir(), "settings"); //ファイル名を指定
        if( file.exists() ){ //すでにファイルが存在している場合
            try{
                RandomAccessFile f = new RandomAccessFile(file, "r");
                byte[] bytes = new byte[(int)f.length()];
                f.readFully(bytes);
                f.close();
                String[] array = new String(bytes).split(","); // , で分割する(拡張性を考慮)
                id = array[0];
            } catch (Exception e) {
                return false;
            }
        }
        Log.d("get_ID", id);
        return true;
    }

    @Override
    public void onProviderDisabled(String provider) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }
}