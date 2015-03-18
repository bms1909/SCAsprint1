package ulbra.bms.scaid5;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Criador por Bruno em 17/03/2015.
 */
public class clsJSONpost extends AsyncTask<String,Void,Boolean> {

    public static boolean executaPost(String URL)
    {
        clsJSONpost executor= new clsJSONpost();

        executor.execute(URL);
        try {
            return executor.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d(null, e.getMessage());
        }
        return false;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        StringBuilder builder = new StringBuilder();
        for(String s : params)
        {
            builder.append(s);
        }
        String url=builder.toString();
            try {
                URL link = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) link.openConnection();

                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                // faz o download
                conn.connect();

                //conversao de inputstream para string
                ByteArrayOutputStream intermediario = new ByteArrayOutputStream();  //intermediario para transformar o url em stream
                IOUtils.copy(conn.getInputStream(), intermediario);
                String conteudo =  intermediario.toString();
                return Boolean.getBoolean(conteudo);

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }

        return false;
    }
}

