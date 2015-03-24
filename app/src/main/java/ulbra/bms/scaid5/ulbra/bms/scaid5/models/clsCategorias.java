package ulbra.bms.scaid5.ulbra.bms.scaid5.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import ulbra.bms.scaid5.clsJSONget;

/**
 * Criador por Bruno em 18/03/2015.
 */

public class clsCategorias {
    private int idCategoria;
    private String nomeCategoria;
    private clsCategorias(int id,String nome)
    {
        this.idCategoria=id;
        this.nomeCategoria=nome;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public static clsCategorias carregaCategorias()
    {
        clsCategorias retorno = null;
        clsJSONget executor = new clsJSONget();
        JSONArray recebido = null;
        JSONObject loop;

        executor.execute("http://scaws.azurewebsites.net/api/clsCategorias");

        try {
            recebido = executor.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d(null, e.getMessage());
        }

        if(recebido!=null) {
            try {
                loop = recebido.getJSONObject(0);
                retorno = new clsCategorias(loop.getInt("idCategoria"), loop.getString("nomeCategoria"));

            } catch (JSONException e) {
                Log.d(null, e.getMessage());
            }
        }
        return retorno;
    }
}
