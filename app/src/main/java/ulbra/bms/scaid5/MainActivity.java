package ulbra.bms.scaid5;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import ulbra.bms.scaid5.ulbra.bms.scaid5.models.clsAlertas;
import ulbra.bms.scaid5.ulbra.bms.scaid5.models.clsEstabelecimentos;

//adicionar a implementação de um monte de coisa
public class MainActivity extends ActionBarActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnMarkerClickListener{

        private boolean segueUsuario;
        private GoogleApiClient mGoogleApiClient;
        private Location mLocalAtual;
        private Location mLocalUltCarga;
        private GoogleMap objMapa;
        private ArrayList<clsAlertas> alertasCarregados;
        private ArrayList<clsEstabelecimentos> estabelecimentosCarregados;
        com.google.android.gms.location.LocationListener mLocationListener;

        private boolean carregaMarcadores()
        {
            if(!clsJSONget.temInternet()) {
                Toast.makeText(this, "Sem acesso a internet, as informações podem estar desatualizadas", Toast.LENGTH_LONG).show();
                return false;
            }
            else {

                alertasCarregados = clsAlertas.carregaAlertas(mLocalAtual);
                estabelecimentosCarregados = clsEstabelecimentos.estabelecimentosPorRaio(1, mLocalAtual);
                //TODO sessão de testes dos métodos ,deu pau
                // clsAlertas a = new clsAlertas(0,-29.332299, -49.751436,"ulbra",0,0);
                //a.cadastraAlerta();
                //


                //foreach do java
                for (clsAlertas percorre : alertasCarregados) {
                    // .icon personaliza o ícone,
                    //adiciona o marcador ver https://developers.google.com/maps/documentation/android/marker#customize_the_marker_image
                    objMapa.addMarker(new MarkerOptions().position(percorre.latlonAlerta).title(percorre.descricaoAlerta).icon(BitmapDescriptorFactory.fromResource(R.drawable.common_signin_btn_icon_focus_light)));
                }
                for (clsEstabelecimentos percorre : estabelecimentosCarregados) {
                    // .icon personaliza o ícone,
                    //adiciona o marcador ver https://developers.google.com/maps/documentation/android/marker#customize_the_marker_image
                    objMapa.addMarker(new MarkerOptions().position(percorre.latlonEstabelecimento).title(percorre.nomeEstabelecimento).icon(BitmapDescriptorFactory.fromResource(R.drawable.common_signin_btn_icon_focus_dark)));
                }
                return true;
            }
    }


    @Override
    /* ativado quando o mapa estiver instanciado */
    public void onMapReady(GoogleMap map) {
        //passa para um objeto local o googleMap instanciado
        objMapa=map;
        //ativa botão de localizar minha posição
        objMapa.setMyLocationEnabled(true);
        //amarra evento de clique no marcador
        objMapa.setOnMarkerClickListener(this);
    }
    //ativa com o click de um marcador do mapa
    @Override
    public boolean onMarkerClick(Marker marker) {
        //pequeno texto exibido rapidamente Toast.makeText(this, "marcador "+marker.getTitle()+" selecionado", Toast.LENGTH_SHORT).show();
        //pop-up ao clicar em alguma marcação
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setTitle("Alerta");
        dlgAlert.setIcon(R.drawable.common_signin_btn_icon_focus_light);
        dlgAlert.setMessage("Este Marcador é " + marker.getTitle());
        dlgAlert.setPositiveButton("Denunciar", null);
        dlgAlert.setNeutralButton("Voltar",null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        if(mGoogleApiClient!=null && mLocationListener !=null)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,mLocationListener);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //TODO executa antes de suspender a aplicação, utilizar para salvar os objetos iniciados
        super.onSaveInstanceState(savedInstanceState);
    }


//ativado após o retorno da activity ao foco principal
    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which==DialogInterface.BUTTON_POSITIVE) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                    else {
                        finish();
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Localização Desativada");
            builder.setMessage("Este aplicativo utiliza sua localização com Alta Precisão (GPS), deseja habilitar agora?");
            //se o malandro pressionar fora do AlertDialog, fecha o aplicativo
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });

            builder.setPositiveButton("Sim", dialogClickListener).setNegativeButton("Não",dialogClickListener);
            builder.create().show();
        }
        else
        {
            if(mGoogleApiClient==null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
                //conecta o googleApiClient, provocando o início do método abaixo
                mGoogleApiClient.connect();
            }
            else {
                solicitaLocalizacao();
            }
            if(objMapa==null)
            {
                //prepara o mapa como objeto, provoca onmapready
                MapFragment mapFragment = (MapFragment) getFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            }
        }
    }
    private void moveCamera()
    {
        try
        {
            LatLng localInicial = new LatLng(mLocalAtual.getLatitude(), mLocalAtual.getLongitude());
            //desloca a visualização do mapa para a coordenada informada
            objMapa.animateCamera(CameraUpdateFactory.newLatLngZoom(localInicial, 17));
        }
        catch (NullPointerException e)
        {
            Log.d(null,e.getMessage());
        }
    }
    private void solicitaLocalizacao()
    {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, (
                        new LocationRequest()
                                .setInterval(10000)
                                .setFastestInterval(5000)
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
                ,mLocationListener);
    }
    public void btnSigaMeClick(View a)
    {
        segueUsuario = !segueUsuario;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocalAtual = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        mLocalUltCarga=mLocalAtual;
        if(mLocalAtual != null)
        {
            moveCamera();
            carregaMarcadores();
        }
        //registra pedido de atualização da localização
        mLocationListener = new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //disparado a cada atualização na localização

                mLocalAtual = location;
                if (segueUsuario)
                    moveCamera();
                if(mLocalAtual.distanceTo(mLocalUltCarga)>100)
                {
                    if(carregaMarcadores())
                    {
                        mLocalUltCarga=mLocalAtual;
                    }
                }
            }
        };
        solicitaLocalizacao();
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

    //métodos implementados pelo googleApiclient

    @Override
    public void onConnectionSuspended(int i) {    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {   }
}
