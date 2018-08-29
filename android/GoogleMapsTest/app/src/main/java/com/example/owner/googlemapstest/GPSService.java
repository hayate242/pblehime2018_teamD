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
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

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

    /* LocationListener Methods */
    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "location");
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

        Http ht = new Http("http://pbl.jp/td/setLocation/index.php?" +
                id + "," + time + "," + lat + ","  + lng + "," + alt+ "," + acc + "," + speed, false);
        ht.execute();
        Log.d("Get","http://pbl.jp/td/setLocation/index.php?" +
                id + "," + time + "," + lat + ","  + lng + "," + alt+ "," + acc + "," + speed);

        prev_lat = lat;
        prev_lng = lng;
    }

    @Override
    public void onProviderDisabled(String provider) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }
}