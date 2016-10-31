package gregmachado.com.panappfirebase.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gregmachado on 02/10/16.
 */

public class CartDB extends SQLiteOpenHelper {
    private static final String NAME_DB = "database.db";
    public static final String TABLE = "cart";
    public static final String ID = "_id";
    public static final String NAME = "product_name";
    public static final String PRICE = "product_price";
    public static final String IMAGE = "product_image";
    public static final String TYPE = "product_type";
    public static final String BAKERY_ID = "bakery_id";
    public static final String ITENS_SALE = "itens_sale";
    public static final String UNIT = "units";
    private static final int VERSION = 10;

    public CartDB(Context context){
        super(context, NAME_DB,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE "+ TABLE +"( "
                + ID + " integer primary key,"
                + NAME + " text,"
                + IMAGE + " text,"
                + PRICE + " real,"
                + BAKERY_ID + " integer,"
                + ITENS_SALE + " integer,"
                + UNIT + " integer,"
                + TYPE + " text"
                +" )";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
}
