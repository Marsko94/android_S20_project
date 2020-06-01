package com.rezwan_cs.androidassignment
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.annotation.Nullable
class DBOpenHelper(@Nullable context:Context):SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    private val create_query1 = ("CREATE TABLE " + PLACE_TABLE_NAME + " ( " +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NAME + " text not null,"
            + COL_ID_NUMBER + " text not null,"
            + COL_LATITUDE + " real not null,"
            + COL_LONGITUDE + " real not null"
            + ");")
    private val updrade_query1 = "DROP TABLE IF EXISTS " + PLACE_TABLE_NAME
    internal var context:Context
    init{
        this.context = context
    }
    override fun onCreate(db:SQLiteDatabase) {
        db.execSQL(create_query1)
    }
    override fun onUpgrade(db:SQLiteDatabase, oldVersion:Int, newVersion:Int) {
        db.execSQL(updrade_query1)
        this.onCreate(db)
    }
    companion object {
        val DB_NAME = "Android_assignment_Database"
        val PLACE_TABLE_NAME = "android_assignment_place_table"
        val COL_ID = "_id"
        val COL_ID_NUMBER = "_id_number"
        val COL_NAME = "_name"
        val COL_LATITUDE = "_latitude"
        val COL_LONGITUDE = "_longitude"
        val DB_VERSION = 1
    }
}