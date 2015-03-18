package ulbra.bms.scaid5;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Criador por Bruno em 16/03/2015.
 */
public class clsUsuarios {
    public int idUsuario;
    public String nomeUsuario;
    public String emailUsuario;
    public String senhaUsuario;

    public clsUsuarios(String nome, String email,String senha)
    {
        this.nomeUsuario=nome;
        this.emailUsuario=email;
        this.senhaUsuario=senha;
    }
    public clsUsuarios(int id,String nome, String email,String senha)
    {
        this.idUsuario=id;
        this.nomeUsuario=nome;
        this.emailUsuario=email;
        this.senhaUsuario=senha;
    }

    public boolean cadastraUsuario()
    {
        return clsJSONpost.executaPost("http://scaws.azurewebsites.net/api/clsUsuarios?nome="+Uri.encode(this.nomeUsuario)+"&email="+Uri.encode(this.emailUsuario)+"&senha="+Uri.encode(this.senhaUsuario));
    }

    public static clsUsuarios carregaUsuario(String nomeOuEmail,String senha)
    {
        clsUsuarios retorno = null;
        clsJSONget executor = new clsJSONget();
        JSONArray recebido = null;
        JSONObject loop;

        executor.execute("http://scaws.azurewebsites.net/api/clsUsuarios?nomeouEmail="+Uri.encode(nomeOuEmail)+"&senha="+Uri.encode(senha));

        try {
            recebido = executor.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d(null, e.getMessage());
        }
        if(recebido!=null) {
            try {
                loop = recebido.getJSONObject(0);
                retorno = new clsUsuarios(loop.getInt("idUsuario"), loop.getString("nomeUsuario"), loop.getString("emailUsuario"), loop.getString("senhaUsuario"));

            } catch (JSONException e) {
                Log.d(null, e.getMessage());
            }
        }
        return retorno;
    }
}
