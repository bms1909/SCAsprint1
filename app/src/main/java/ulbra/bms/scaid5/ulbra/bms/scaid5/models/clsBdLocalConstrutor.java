package ulbra.bms.scaid5.ulbra.bms.scaid5.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Criado por Bruno on 02/04/2015.
 */
public class clsBdLocalConstrutor extends SQLiteOpenHelper {
    private static final String NOME_BD = "teste";
    private static final int VERSAO_BD = 1;

    public clsBdLocalConstrutor(Context context) {
        super(context, NOME_BD, null, VERSAO_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //sem _ antes do id, auto incremento não funciona
        db.execSQL("CREATE TABLE temp" +
                "(" +
                "_id integer primary key autoincrement," +
                "comando text not null" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table cache;");
        onCreate(db);
    }
}
