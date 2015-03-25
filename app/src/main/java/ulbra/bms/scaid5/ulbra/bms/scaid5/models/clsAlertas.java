package ulbra.bms.scaid5.ulbra.bms.scaid5.models;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import ulbra.bms.scaid5.controllers.clsJSONget;
import ulbra.bms.scaid5.controllers.clsJSONpost;

/**
 * Criador por Bruno em 13/03/2015.
 */
public class clsAlertas {

    public int idUsuario;
    public LatLng latlonAlerta;
    public int tipoAlerta;
    public String descricaoAlerta;
    public int riscoAlerta;

    public clsAlertas(int id, double latitude, double longitude,String descricao,int tipo,int risco)
    {
        this.idUsuario=id;
        this.latlonAlerta= new LatLng(latitude, longitude);
        this.descricaoAlerta= descricao;
        this.tipoAlerta=tipo;
        this.riscoAlerta=risco;
    }

    public static ArrayList<clsAlertas> carregaAlertas(int raio, Location local)
    {
        ArrayList<clsAlertas> retorno = new ArrayList<>();
        clsJSONget executor= new clsJSONget();
        JSONArray recebido = null;
        JSONObject loop;

        executor.execute("http://scaws.azurewebsites.net/api/clsAlertas?raioLongoemKM=" + raio + "&lat=" + local.getLatitude() + "&lon=" + local.getLongitude());

        try {
            recebido = executor.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d(null, e.getMessage());
        }

        try {
            if (recebido != null)
            {
                for (int i = 0; i < recebido.length(); i++)
                {
                    loop = recebido.getJSONObject(i);
                    retorno.add(new clsAlertas(loop.getInt("idAlerta"),loop.getDouble("latitudeAlerta"),loop.getDouble("longitudeAlerta"),loop.getString("descricaoAlerta"),loop.getInt("tipoAlerta"),loop.getInt("riscoAlerta")));
                }
            }
        } catch (JSONException |NullPointerException e) {
            Log.d(null, e.getMessage());
        }
        return retorno;
    }

    public static boolean denunciaAlerta(int idAlerta)
    {
        return clsJSONpost.executaPost("http://scaws.azurewebsites.net/api/clsAlertas?idAlerta=" + idAlerta);
    }

    public boolean cadastraAlerta()
    {
        return clsJSONpost.executaPost("http://scaws.azurewebsites.net/api/clsAlertas?idUsuario=" + this.idUsuario + "&lat=" + this.latlonAlerta.latitude + "&lon=" + this.latlonAlerta.longitude + "&tipo=" + this.tipoAlerta + "&descricao=" + Uri.encode(this.descricaoAlerta) + "&risco=" + this.riscoAlerta);
    }

}
