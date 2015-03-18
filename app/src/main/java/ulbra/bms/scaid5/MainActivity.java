package ulbra.bms.scaid5;

import android.app.AlertDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

//adicionar a implementação de um monte de coisa
public class MainActivity extends ActionBarActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnMarkerClickListener{

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleMap objMapa;
    private ArrayList<clsAlertas> alertasCarregados;
    private ArrayList<clsEstabelecimentos> estabelecimentosCarregados;

    private void carregaMarcadores()
    {
        alertasCarregados = clsAlertas.carregaAlertas(mLastLocation);
        estabelecimentosCarregados = clsEstabelecimentos.estabelecimentosPorRaio(1,mLastLocation);
       //TODO sessão de testes dos métodos

        if (alertasCarregados.size()==0||estabelecimentosCarregados.size()==0)
        {
            Toast.makeText(this, "Não foi possível fazer o download das informações atualizadas, se o erro persistir, confira sua conexão com a internet e abra o aplicativo novamente", Toast.LENGTH_LONG).show();
        }
        else
        {
            //foreach do java
            for (clsAlertas percorre : alertasCarregados)
            {
                // .icon personaliza o ícone,
                //adiciona o marcador ver https://developers.google.com/maps/documentation/android/marker#customize_the_marker_image
                objMapa.addMarker(new MarkerOptions().position(percorre.latlonAlerta).title(percorre.descricaoAlerta).icon(BitmapDescriptorFactory.fromResource(R.drawable.common_signin_btn_icon_focus_light)));
            }
            for (clsEstabelecimentos percorre : estabelecimentosCarregados)
            {
                // .icon personaliza o ícone,
                //adiciona o marcador ver https://developers.google.com/maps/documentation/android/marker#customize_the_marker_image
                objMapa.addMarker(new MarkerOptions().position(percorre.latlonEstabelecimento).title(percorre.nomeEstabelecimento).icon(BitmapDescriptorFactory.fromResource(R.drawable.common_signin_btn_icon_focus_dark)));
            }
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
//Métodos criados automaticamente abaixo, onCreate e onConnected foram modificados
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            //referencia o <fragment> do xml e obtém o mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

            //constroi o objeto GoogleApiclient para obter localização do usuário
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //conecta o googleApiClient, provocando o início do método abaixo
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        LatLng localInicial = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        //desloca a visualização do mapa para a coordenada informada
        objMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(localInicial,17));

        //carrega os itens do mapa
        carregaMarcadores();
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
    public void onConnectionFailed(ConnectionResult connectionResult) {    }
}
