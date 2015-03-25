package ulbra.bms.scaid5.ulbra.bms.scaid5.models;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Criado por Bruno on 24/03/2015.
 * implementa uma instância singleton da classe GoogleApiClient, utilizada para garantir o acesso a localização em todas as
 * activities e permitir que todas registrem seus listeners no mesmo objeto.
 */
public class clsApiClientSingleton {

    private static final clsApiClientSingleton INSTANCE = new clsApiClientSingleton();
    private GoogleApiClient mGoogleApiClient;


    private clsApiClientSingleton() {
    }

    //tornam a classe singleton, ou seja, só uma instância no programa inteiro
    public static clsApiClientSingleton getInstance(Context contexto, LocationListener mLocationListener) {
        if (INSTANCE.mGoogleApiClient == null) {
            criaApiClient(contexto, mLocationListener);
        }

        return INSTANCE;
    }

    private static void criaApiClient(Context contexto, final LocationListener mLocationListener) {
        //contexto é pai de toda a activity, sendo o mesmo para o aplicativo inteiro
        INSTANCE.mGoogleApiClient = new GoogleApiClient.Builder(contexto)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        solicitaLocalizacao(mLocationListener);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d("", "vish");
                    }
                })
                .addApi(LocationServices.API)
                .build();
        //conecta o googleApiClient, provocando o início do método abaixo
        INSTANCE.mGoogleApiClient.connect();
    }

    private static void solicitaLocalizacao(LocationListener mLocationListener) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                INSTANCE.mGoogleApiClient, (
                        new LocationRequest()
                                .setInterval(10000)
                                .setFastestInterval(5000)
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
                , mLocationListener);
    }

    public void retomaLocalizacao(Context contexto, LocationListener mLocationListener) {
        if (mGoogleApiClient == null)
            criaApiClient(contexto, mLocationListener);

        solicitaLocalizacao(mLocationListener);
    }

    public void suspendeLocalizacao(LocationListener mLocationListener) {
        if (mGoogleApiClient != null && mLocationListener != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
    }
}
