package ulbra.bms.scaid5.controllers;

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
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ulbra.bms.scaid5.R;
import ulbra.bms.scaid5.ulbra.bms.scaid5.models.clsAlertas;
import ulbra.bms.scaid5.ulbra.bms.scaid5.models.clsApiClientSingleton;
import ulbra.bms.scaid5.ulbra.bms.scaid5.models.clsEstabelecimentos;

//adicionar a implementação de um monte de coisa
public class MainActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public final StringBuilder a = new StringBuilder();
    public GoogleMap objMapa;
    private boolean segueUsuario;
    private Location mLocalAtual;
    private Location mLocalUltimaCargaMarcadores;
    private LocationListener mLocationListener;
    private clsApiClientSingleton mGerenciadorApiClient;


    private void moveCamera(GoogleMap meumapalindo) {
        try
        {
            a.append("5");
            //desloca a visualização do mapa para a coordenada informada

            meumapalindo.animateCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(mLocalAtual.getLatitude(), mLocalAtual.getLongitude())), 17), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                }

                //ativado se o usuário interromper a movimentação da camera
                @Override
                public void onCancel() {
                    segueUsuario = false;
                }
            });
        } catch (NullPointerException e) {
            Log.d("erro ao mover camera", e.getMessage());
        }
    }

    public boolean carregaMarcadores() {
        a.append("6");
        if (objMapa != null) {
            if (!clsJSONget.temInternet()) {
                Toast.makeText(this, "Sem acesso a internet, as informações podem estar desatualizadas", Toast.LENGTH_LONG).show();
                return false;
            } else {
                //carrega as listas de objetos alertas e estabelecimentos do webService
                //foreach do java
                for (clsAlertas percorre : clsAlertas.carregaAlertas(1, mLocalAtual)) {
                    // .icon personaliza o ícone,
                    //adiciona o marcador ver https://developers.google.com/maps/documentation/android/marker#customize_the_marker_image
                    objMapa.addMarker(new MarkerOptions().position(percorre.latlonAlerta).title(percorre.descricaoAlerta).icon(BitmapDescriptorFactory.fromResource(R.drawable.common_signin_btn_icon_focus_light)));
                }
                for (clsEstabelecimentos percorre : clsEstabelecimentos.estabelecimentosPorRaio(1, mLocalAtual)) {
                    objMapa.addMarker(new MarkerOptions().position(percorre.latlonEstabelecimento).title(percorre.nomeEstabelecimento).icon(BitmapDescriptorFactory.fromResource(R.drawable.common_signin_btn_icon_focus_dark)));
                }
                return true;
            }
        }
        return false;
    }


    @Override
    /* ativado quando o mapa estiver instanciado */
    public void onMapReady(GoogleMap map) {
        a.append("3");

        //passa para um objeto local o googleMap instanciado
        objMapa=map;
        /*objMapa.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
          @Override
          public void onCameraChange(CameraPosition cameraPosition) {
              Location location = new Location("Test");
              location.setLatitude(cameraPosition.target.latitude);
              location.setLongitude(cameraPosition.target.longitude);
              location.setTime(new Date().getTime());
              //TODO carregar marcadores conforme movimentação da camera
          }
      });*/

        //ativa botão de localizar minha posição
        objMapa.setMyLocationEnabled(true);
        //listener do botão de minha localização
        objMapa.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                segueUsuario = true;
                return false;
            }
        });
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
        return false;
    }

    @Override
    protected void onDestroy() {
        mGerenciadorApiClient.suspendeLocalizacao(mLocationListener);
        mGerenciadorApiClient = null;
        mLocalAtual = null;
        mLocalUltimaCargaMarcadores = null;
        objMapa = null;
        mLocationListener = null;
        a.append("0");
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        a.append("1");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //cria o listener local de localização e implementa o método de monitoramento do mesmo
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                a.append("4");
                mLocalAtual = location;
                //confere se a camera deve ser movida
                if (segueUsuario) {
                    moveCamera(objMapa);
                }
                //se não houver nenhuma carga de dados anterior, executa
                if (mLocalUltimaCargaMarcadores == null) {
                    if (carregaMarcadores())
                        mLocalUltimaCargaMarcadores = mLocalAtual;
                }
                //compara a distância da última carga de dados realizada com a atual, em metros
                else if (mLocalAtual.distanceTo(mLocalUltimaCargaMarcadores) > 300) {
                    mLocalUltimaCargaMarcadores = mLocalAtual;
                    carregaMarcadores();
                }

            }
        };

    }
    @Override
    protected void onStop()
    {
        if (mLocationListener != null && mGerenciadorApiClient != null)
            mGerenciadorApiClient.suspendeLocalizacao(mLocationListener);
        super.onStop();
    }

    //ativado após o retorno da activity ao foco principal
    @Override
    protected void onPostResume()
    {
        a.append("2");
        super.onPostResume();
        //configura se o método movecamera deve ser acionado ao mudar a localização
        segueUsuario = true;

        //confere se o GPS está ligado
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //cria uma caixa de diálogo caso o GPS esteja desligado
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

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
            //obtem uma instancia singleton do objeto, registrando seu próprio listener
            mGerenciadorApiClient = clsApiClientSingleton.getInstance(this, mLocationListener);
            if(objMapa==null)
            {
                //prepara o mapa como objeto, provoca onmapready
                MapFragment mapFragment = (MapFragment) getFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            }
        }
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
            Toast.makeText(this, "você clicou em configurações, aguarde novas atualizações!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }


}
