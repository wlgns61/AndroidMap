package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AppComponentFactory;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Objects;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    TextView textView;
    BroadcastReceiver br;
    Spinner menu;
    String picked_date = getNow();
    @NonNull NaverMap myNaverMap;
    List<Marker> markerlist = new ArrayList<>();
    int picked_time = 25;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        textView = (TextView)findViewById(R.id.textView);
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        //스피너 만들기
        menu = (Spinner)findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.items,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        menu.setPrompt("MENU");
        menu.setAdapter(adapter);

        menu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==1){
                    picked_date = getNow();
                    showDate();
                    menu.setSelection(0);
                }
                if(i==2){
                    showHourPicker();
                    menu.setSelection(0);
                }
                if(i==3){
                    msgBox();
                    menu.setSelection(0);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Button button = (Button)findViewById(R.id.button3);
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                infomsgBox();
            }
        });

        br = new BootReceiver();
        IntentFilter filter = new
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("com.example.androidproject.BootReceiver");
        this.registerReceiver(br, filter);

        mapFragment.getMapAsync(this);
        Intent intent= new Intent();
        intent.setAction("com.example.androidproject.BootReceiver");

        sendBroadcast(intent);

    }



    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        myNaverMap = naverMap;

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setScaleBarEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setLocationButtonEnabled(true);
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    public void showDate() {

        Calendar cal = Calendar.getInstance();

        int year = cal.get(cal.YEAR);
        int month = cal.get(cal.MONTH);
        int date = cal.get(cal.DATE);
        picked_time = 25;

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1 += 1;
                String MM = Integer.toString(i1);
                if (MM.length() == 1) {
                    MM = '0' + MM;
                }
                String dd = Integer.toString(i2);
                if (dd.length() == 1) {
                    dd = '0' + dd;
                }
                picked_date = i + "-" + MM + "-" + dd;
                Log.d("date", "                                                    " +  picked_date);

                Log.d("getDataBase", picked_date + " inputed!");
                getDataBase(picked_date);
            }
        } ,year, month, date);
        datePickerDialog.setMessage("Choose date");
        datePickerDialog.show();
    }

    public void showHourPicker() {

        final Dialog d = new Dialog(MapsActivity.this);
        d.setTitle("Choose time");
        d.setContentView(R.layout.numberpicker);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(23);
        np.setMinValue(00);
        d.show();
        np.setWrapSelectorWheel(true);
        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                picked_time = np.getValue();
                Log.d("showHourPicker", String.valueOf(picked_time));
                getDataBase(picked_date);
                d.dismiss();
            }
        });
    }

    public void getDataBase(String picked_date){
        if(picked_time == 25){
            Log.d("getDataBase", "picked_date: " + picked_date);
            String[] columns =  new String[]{ "latitude", "longitude", "time"};
            String where = "date=?";
            Cursor c = getContentResolver().query(MyContentProvider.CONTENT_URI, columns, where,
                    new String[]{picked_date}, null, null);
            Log.d("getDataBase", "Cursor start");
            if (!c.moveToNext()) {
                Toast.makeText(this,"위치 정보가 존재하지 않습니다.",Toast.LENGTH_LONG).show();
                Log.d("getDataBase", "c is null");
                c.close();
            } else {
                Log.d("getDataBase", "Cursor is not null");
                textView.setText(picked_date + "의 위치정보");
                c.moveToPrevious();
                DeleteMarker();
                while (c.moveToNext()){
                    Log.d("getDataBase", "Cursor in while");
                    double latitude = c.getDouble(0);
                    double longitude = c.getDouble(1);
                    String time = c.getString(2);
                    CreateMarker(latitude, longitude, time, picked_date);
                }
                c.close();
            }
        }
        else{
            String strTime = String.valueOf(picked_time);
            if(strTime.length()==1)
                strTime = "0" + strTime;

            Log.d("getDataBaseandtime", "picked_date: " + picked_date + "  picked_time: " + picked_time);
            String[] columns =  new String[]{ "latitude", "longitude", "time"};
            String where = "date=? and time like ?";
            Cursor c = getContentResolver().query(MyContentProvider.CONTENT_URI, columns, where,
                    new String[]{picked_date, strTime + '%'}, null, null);
            Log.d("getDataBase", "Cursor start");
            if (!c.moveToNext()) {
                Log.d("getDataBase", "c is null");
                Toast.makeText(this,"위치 정보가 존재하지 않습니다.",Toast.LENGTH_LONG).show();
                c.close();
            } else {
                Log.d("getDataBase", "Cursor is not null");
                c.moveToPrevious();
                DeleteMarker();
                while (c.moveToNext()){
                    Log.d("getDataBase", "Cursor in while");
                    double latitude = c.getDouble(0);
                    double longitude = c.getDouble(1);
                    String time = c.getString(2);
                    CreateMarker(latitude, longitude, time, picked_date);
                }
                c.close();
            }
        }

    }

    public void CreateMarker(double latitude, double longitude, String time, String date){
        Marker marker = new Marker();
        marker.setPosition(new LatLng(latitude, longitude));
        marker.setHideCollidedMarkers(true);
        marker.setMap(myNaverMap);
        InfoWindow infoWindow = new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getBaseContext()) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return "날짜: "+ date + " \n시간: " + time;
            }
        });

        myNaverMap.setOnMapClickListener((coord, point) -> {
            infoWindow.close();
        });

        Overlay.OnClickListener listener = overlay -> {
            if (marker.getInfoWindow() == null) {
                // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                infoWindow.open(marker);
            } else {
                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close();
            }
            return true;
        };

        marker.setOnClickListener(listener);
        markerlist.add(marker);
    }

    public void DeleteMarker(){
        for ( Marker marker : markerlist ) {
            marker.setMap(null);
        }
        markerlist.clear();
    }

    public void msgBox(){
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
        alert_confirm.setMessage("마커를 전부 닫겠습니까?").setCancelable(false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"마커를 모두 닫습니다.",
                                Toast.LENGTH_SHORT).show();
                        DeleteMarker();
                        // 'YES'
                    }
                }).setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'No'
                        return;
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }

    public void infomsgBox(){
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
        alert_confirm.setMessage("제작자\n\n2015112241 유지훈").setCancelable(false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }



    public String getNow(){
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd", Locale.KOREA);
        Date currentDate = new Date ();
        String date = mSimpleDateFormat.format ( currentDate );
        return date;
    }
}
