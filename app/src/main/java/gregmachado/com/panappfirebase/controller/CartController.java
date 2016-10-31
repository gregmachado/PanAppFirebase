package gregmachado.com.panappfirebase.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import gregmachado.com.panappfirebase.helper.CartDB;


/**
 * Created by gregmachado on 02/10/16.
 */

public class CartController {

    private SQLiteDatabase db;
    private CartDB database;

    public CartController(Context context){
        database = new CartDB(context);
    }

    public String insert(String id, String name, String image, Double price, String type, String bakeryId, int itens, int units){
        ContentValues values;
        long resultado;

        db = database.getWritableDatabase();
        values = new ContentValues();
        values.put(CartDB.ID, id);
        values.put(CartDB.NAME, name);
        values.put(CartDB.IMAGE, image);
        values.put(CartDB.PRICE, price);
        values.put(CartDB.TYPE, type);
        values.put(CartDB.BAKERY_ID, bakeryId);
        values.put(CartDB.ITENS_SALE, itens);
        values.put(CartDB.UNIT, units);

        resultado = db.insert(CartDB.TABLE, null, values);
        db.close();

        if (resultado ==-1)
            return "Erro ao inserir registro";
        else
            return "Registro Inserido com sucesso";

    }

    public Cursor load(){
        Cursor cursor;
        String[] fields =  {database.ID,database.NAME,database.IMAGE,database.PRICE,
                database.TYPE,database.BAKERY_ID,database.ITENS_SALE,database.UNIT};
        db = database.getReadableDatabase();
        cursor = db.query(database.TABLE, fields, null, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public void deleteAll(){
        db = database.getWritableDatabase();
        db.delete(CartDB.TABLE,null,null);
        db.close();
    }
}
