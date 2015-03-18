package ulbra.bms.scaid5;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Criador por Bruno em 17/03/2015.
 */
public class clsJSONget extends AsyncTask<String,Void,JSONArray> {

    @Override
    protected JSONArray doInBackground(String... params) {
        JSONArray retorno = null;

        StringBuilder builder = new StringBuilder();
        for(String s : params)
        {
            builder.append(s);
        }

        String url=builder.toString();
        try {
            ByteArrayOutputStream intermediario = new ByteArrayOutputStream();  //intermediario para transformar o url em stream
            URL link = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) link.openConnection();

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // faz o download
            conn.connect();

            //conversao de inputstream para string
            IOUtils.copy(conn.getInputStream(), intermediario);
            String conteudo =  intermediario.toString();
            if (conteudo.startsWith("{"))
            {
                builder= new StringBuilder();
                builder.append("[");
                builder.append(conteudo);
                builder.append("]");
                conteudo=builder.toString();
            }
            try {
                retorno = new JSONArray(conteudo); //converte os dados recebidos de uma string para um objeto manipulável
            } catch (JSONException e) {
                Log.d(null, e.getMessage());
            }

        } catch (IOException o) {
            Log.d(null,o.getMessage());
        }
        return retorno;
    }
}
