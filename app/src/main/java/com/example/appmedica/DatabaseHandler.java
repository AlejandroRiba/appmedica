package com.example.appmedica;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import androidx.annotation.NonNull;


public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MyDatabase";

    // Nombres de tabla y columnas
    private static final String TABLE_USER = "Usuario";

    // Columnas de la tabla 1
    private static final String KEY_NAME_1 = "name";
    private static final String EDAD = "edad";
    private static final String TIPO_BLOOD = "sangre";
    private static final String CONTACTO = "contacto";
    private static final String DESPERTAR = "despertar";
    private static final String DORMIR = "dormir";
    private static final String DESAYUNO = "desayuno";
    private static final String COMIDA = "comida";
    private static final String CENA = "cena";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tablas
        String createTable1 = "CREATE TABLE " + TABLE_USER + "("
                + KEY_NAME_1 + " VARCHAR PRIMARY KEY,"
                + EDAD + " INTEGER,"
                + TIPO_BLOOD + " VARCHAR,"
                + CONTACTO + " VARCHAR,"
                + DESPERTAR + " VARCHAR,"
                + DORMIR + " VARCHAR,"
                + DESAYUNO + " VARCHAR,"
                + COMIDA + " VARCHAR,"
                + CENA + " VARCHAR)";

        // Ejecutar las sentencias SQL de creación
        db.execSQL(createTable1);
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldversion, int newversion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }
    @SuppressLint("Range")
    public String consultaAdulto(){
        SQLiteDatabase db = getReadableDatabase();
        String nombreUsuario;
        Cursor cursor = db.rawQuery("SELECT name FROM Usuario LIMIT 1", null);
        if (cursor.moveToFirst()) {
            nombreUsuario = cursor.getString(cursor.getColumnIndex(KEY_NAME_1));
        } else {
            // Si el cursor está vacío, asignar "Usuario" a nombreUsuario
            nombreUsuario = "Usuario";
        }

        cursor.close();
        db.close();
        return nombreUsuario;
    }
    // Métodos para manipular la base de datos según sea necesario (inserción, actualización, eliminación, consulta)
    public void agregarPersona(String nombre, Integer edad, String contacto, String sangretip, String hora1, String hora2, String hora3, String hora4, String hora5){
        SQLiteDatabase db = getWritableDatabase();
        if(db != null){
            ContentValues values = new ContentValues();
            values.put(KEY_NAME_1, nombre);
            values.put(EDAD, edad);
            values.put(TIPO_BLOOD, sangretip);
            values.put(CONTACTO, contacto);
            values.put(DESPERTAR, hora1);
            values.put(DORMIR, hora2);
            values.put(DESAYUNO, hora3);
            values.put(COMIDA, hora4);
            values.put(CENA, hora5);
            db.insert(TABLE_USER, null, values);
            db.close();
        }
    }


    public void eliminarTodosLosUsuarios() {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            // Ejecuta la operación de eliminación sin especificar una cláusula WHERE
            db.execSQL("DELETE FROM Usuario");
            db.close();
        }
    }

    public boolean actualizarUsuario(String nombreAnterior, String nuevoNombre, int edad, String tipoBlood, String contacto, String despertar, String dormir, String desayuno, String comida, String cena) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Asignar valores a ContentValues
        contentValues.put(KEY_NAME_1, nuevoNombre); // Agregar el nuevo nombre
        contentValues.put(EDAD, edad);
        contentValues.put(TIPO_BLOOD, tipoBlood);
        contentValues.put(CONTACTO, contacto);
        contentValues.put(DESPERTAR, despertar);
        contentValues.put(DORMIR, dormir);
        contentValues.put(DESAYUNO, desayuno);
        contentValues.put(COMIDA, comida);
        contentValues.put(CENA, cena);

        // Actualizar el registro en la base de datos usando el nombre anterior
        int result = db.update(TABLE_USER, contentValues, KEY_NAME_1 + "=?", new String[]{nombreAnterior});

        db.close();
        return result > 0; // Devuelve true si se actualizó al menos un registro
    }



    @SuppressLint("Range")
    public Usuario consultaDatos() {
        SQLiteDatabase db = getReadableDatabase();
        Usuario usuario = new Usuario(); // Suponiendo que tienes una clase Usuario para representar los datos de la tabla
        Cursor cursor = db.rawQuery("SELECT * FROM Usuario LIMIT 1", null);
        if (cursor.moveToFirst()) {
            usuario.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME_1)));
            usuario.setAge(cursor.getInt(cursor.getColumnIndex(EDAD)));
            usuario.setBlood(cursor.getString(cursor.getColumnIndex(TIPO_BLOOD)));
            usuario.setContact(cursor.getString(cursor.getColumnIndex(CONTACTO)));
            usuario.setH1(cursor.getString(cursor.getColumnIndex(DESPERTAR)));
            usuario.setH2(cursor.getString(cursor.getColumnIndex(DORMIR)));
            usuario.setH3(cursor.getString(cursor.getColumnIndex(DESAYUNO)));
            usuario.setH4(cursor.getString(cursor.getColumnIndex(COMIDA)));
            usuario.setH5(cursor.getString(cursor.getColumnIndex(CENA)));
            // Aquí continúa asignando otros campos de Usuario según tus columnas en la tabla
        }

        cursor.close();
        db.close();
        return usuario;
    }

}
