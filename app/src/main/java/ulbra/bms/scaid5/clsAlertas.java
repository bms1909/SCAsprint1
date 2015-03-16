package ulbra.bms.scaid5;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Bruno on 13/03/2015.
 */
public class clsAlertas {

    public int idUsuario;
    public LatLng latlonAlerta;
    public int tipoAlerta;
    public String descricaoAlerta;
    public int riscoAlerta;

    public clsAlertas(int id, double latitude, double longitude,String descricao)
    {
        this.idUsuario=id;
        this.latlonAlerta= new LatLng(latitude, longitude);
        this.descricaoAlerta= descricao;
    }

    //TODO carregar do WebService
    public static ArrayList<clsAlertas> carregaAlertas()
    {
        ArrayList<clsAlertas> retorno = new ArrayList<>();
        retorno.add(new clsAlertas(7,-29.459916,-49.922834,"InicioQuadra"));
        retorno.add(new clsAlertas(7,-29.4578,-49.924315,"neoclin"));
        retorno.add(new clsAlertas(7,-29.459561,-49.923907,"escoteiros"));
        retorno.add(new clsAlertas(7,-29.442516,-49.904445,"vinho sandy"));
        return retorno;
    }
}
