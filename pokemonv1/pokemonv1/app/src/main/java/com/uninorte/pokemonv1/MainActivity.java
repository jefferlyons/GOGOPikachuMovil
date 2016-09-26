package com.uninorte.pokemonv1;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final String TAG = "Etiquetas";
    private static final int MY_PERMISSIONSLOCATION = 1;
    private static final int MINUTOS = 5;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private AlertDialog alert;
    private GoogleMap googleMap;
    private CameraUpdate mCamera;
    private boolean estadoRed;
    private ArrayList<LatLng> posiciones;
    private ArrayList<DataPokemon> pokemon;
    //private ArrayList<Bitmap> imagenPunto;
    private ArrayList<DataImages> dataImages;
    private int num = 0;
    private boolean estado = false;
    private boolean consulta = false;
    private boolean imgDesc = false;
    private Bitmap imagenDes;
    private Map<String, Integer> ImgPos = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        FlowManager.init(new FlowConfig.Builder(this).build());
        estadoRed = verificarEstadoRed();

        dataImages = (ArrayList<DataImages>) new Select().from(DataImages.class).queryList();
        pokemon = (ArrayList<DataPokemon>) new Select().from(DataPokemon.class).queryList();


        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        TimerTask timerTask = new timer();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, MINUTOS * 60 * 1000);

        if(pokemon.isEmpty()){
            new GetPokemon().execute();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            Log.d(TAG, "Connect");
        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONSLOCATION);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended" + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed" + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng posicionP = new LatLng(location.getLatitude(), location.getLongitude());
        mCamera = CameraUpdateFactory.newLatLngZoom(posicionP, 18);
        googleMap.animateCamera(mCamera);
        if(!estado){
            if (!consulta){
                posiciones = null;
               new GetData().execute(location.getLatitude(),location.getLongitude());
                consulta = true;
                //imagenPunto = new ArrayList<Bitmap>();
            }
            if (posiciones != null && pokemon!= null){
                if(dataImages.size()<posiciones.size()){
                    if (!imgDesc){
                        //num = (int) (Math.random()*9+1);
                        String direccion = pokemon.get(num).imgfront;
                        new CargarImagenes().execute(direccion);
                        num++;
                        imgDesc = true;
                    }
                    if(imagenDes != null){
                        imgDesc = false;
                        //imagenPunto.add(imagenDes);
                        ByteArrayOutputStream baos=new ByteArrayOutputStream();
                        imagenDes.compress(Bitmap.CompressFormat.PNG,100, baos);
                        byte [] b=baos.toByteArray();
                        String temp=Base64.encodeToString(b, Base64.DEFAULT);
                        new DataImages(num,temp).save();
                        dataImages = (ArrayList<DataImages>) new Select().from(DataImages.class).queryList();
                        imagenDes = null;
                    }
                }

                else{
                    puntosAleatorios();
                    estado = true;
                }
            }
        }
        else{
            for (int a=0; a<posiciones.size();a++){
                float dis = (float) Math.pow(Math.pow(posiciones.get(a).latitude-posicionP.latitude,2) + Math.pow(posiciones.get(a).longitude-posicionP.longitude,2),0.5)*10000;
                if(dis < 1.1){
                    int idPoke = ImgPos.get(posiciones.get(a).toString());
                    for(int b = 0; b<pokemon.size();b++){
                        if (pokemon.get(b).idPo == idPoke){
                            Toast.makeText(this, "Tiene cerca al pokemon: "+pokemon.get(b).name,Toast.LENGTH_LONG).show();
                        }
                    }

                }
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONSLOCATION: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        ActivarGps();
                    } else {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                        googleMap.getUiSettings().setZoomGesturesEnabled(false);
                        googleMap.getUiSettings().setScrollGesturesEnabled(false);
                        googleMap.setMyLocationEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);


                    }
                } else {

                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        onConnected(Bundle.EMPTY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alert != null)
            alert.dismiss();
    }

    private void ActivarGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public boolean verificarEstadoRed() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean res = true;
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(this, "La red no esta disponible", Toast.LENGTH_LONG).show();
            res = false;
        }
        return res;
    }

    public void puntosAleatorios() {
        if (posiciones != null) {
            googleMap.clear();
            ImgPos.clear();
            for (int j = 0; j < posiciones.size(); j++) {
                int k = (int) (Math.random()*9+1);
                String img = dataImages.get(k).data2;
                byte [] encodeByte= Base64.decode(img,Base64.DEFAULT);
                Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                googleMap.addMarker(new MarkerOptions().position(posiciones.get(j)).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                ImgPos.put(posiciones.get(j).toString(),pokemon.get(k).idPo);
            }
        }
    }

    public Bitmap obtImagen(String direccionWeb) {

        try {
            URL imageUrl = new URL(direccionWeb);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.connect();
            Bitmap imagen = BitmapFactory.decodeStream(conn.getInputStream());
            return imagen;
        } catch (IOException e) {
            return null;
        }
    }

    protected String getData(String web) {
        try {
            String direccion = web;
            URL url = new URL(direccion);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputText = "";
            boolean st = true;
            while (st) {
                String val = reader.readLine();
                if (val == null) {
                    st = false;
                } else {
                    inputText = inputText + val;
                }
            }
            reader.close();
            return inputText;
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }

    }

    public void onClickMochila(View view) {
        Log.d(TAG, "CLICK EN LA MOCHILA");
    }

    private class GetData extends AsyncTask<Double, Void, Void> {

        @Override
        protected Void doInBackground(Double... doubles) {
            posiciones = new ArrayList<LatLng>();
            String dirWeb = "http://190.144.171.172/function3.php?lat=" + doubles[0] + "&lng=" + doubles[1];
            String response = getData(dirWeb);
            if (response != null) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        posiciones.add(new LatLng(object.getDouble("lt"), object.getDouble("lng")));
                    }
                    //puntosAleatorios(posiciones);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class GetPokemon extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            //pokemon = new ArrayList<Pokemon>();
            String dirWebPoke = "https://raw.githubusercontent.com/FTorrenegraG/Pokemon_json_example/master/example.json";
            String responsePoke = getData(dirWebPoke);
            if (responsePoke != null) {
                try {
                    JSONArray jsonArray1 = new JSONArray(responsePoke);
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject object1 = jsonArray1.getJSONObject(j);
                        DataPokemon poke = new DataPokemon(object1.getInt("Id"), object1.getString("Name"), object1.getString("Type"), object1.getInt("Total"), object1.getInt("HP"),
                                object1.getInt("Attack"), object1.getInt("Defense"), object1.getInt("Sp. Atk"), object1.getInt("Sp. Def"), object1.getInt("Speed"), object1.getInt("ev_id"), object1.getString("ImgFront"), object1.getString("ImgBack"), object1.getString("GifFront"), object1.getString("GifBack"), object1.getString("ImgUrl"));
                        poke.save();
                        //pokemon.add(poke);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pokemon = (ArrayList<DataPokemon>) new Select().from(DataPokemon.class).queryList();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    private class CargarImagenes extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String url = strings[0];
            imagenDes = obtImagen(url);
            return null;
        }
    }

    private class timer extends TimerTask {

        @Override
        public void run() {
            estado = false;
            consulta = false;
            imgDesc = false;
            Log.d(TAG,"Por cambiar puntos");

        }
    }

}

