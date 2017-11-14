package com.ece651group8.uwaterloo.ca.ece_651_group8.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ece651group8.uwaterloo.ca.ece_651_group8.R;
import com.ece651group8.uwaterloo.ca.ece_651_group8.bean.Pid;
import com.ece651group8.uwaterloo.ca.ece_651_group8.bean.Token;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "rpm.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;


    // the DAO object we use to access the SimpleData table
    private Dao<Token, Integer> tokenDao = null;
    private RuntimeExceptionDao<Token, Integer> tokenRuntimeDao = null;

    private Dao<Pid, Integer> pidDao = null;
    private RuntimeExceptionDao<Pid, Integer> pidRuntimeDao = null;



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Token.class);
            TableUtils.createTable(connectionSource, Pid.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

        // here we try inserting data in the on-create as a test
       // RuntimeExceptionDao<Token, Integer> dao = getTokenDao();
       // RuntimeExceptionDao<Pid, Integer> dao2 = getPidDao();

//        String tokenValue = "9c2b365463b83c16c24f3458318069bb77f7fece";
//        // create some entries in the onCreate
//        Token token = new Token(tokenValue);
//        dao.create(token);
//
//        Log.i(DatabaseHelper.class.getName(), "created new entries in onCreate: " + tokenValue);
    }



    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Token.class, true);
            TableUtils.dropTable(connectionSource, Pid.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
     * value.
     */
    public Dao<Token, Integer> getDao() throws SQLException {
        if (tokenDao == null) {
            tokenDao = getDao(Token.class);
        }
        return tokenDao;
    }

    public Dao<Pid, Integer> getDao2() throws SQLException {
        if (pidDao == null) {
            pidDao = getDao(Pid.class);
        }
        return pidDao;
    }
    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<Token, Integer> getTokenDao() {
        if (tokenRuntimeDao == null) {
            tokenRuntimeDao = getRuntimeExceptionDao(Token.class);
        }
        return tokenRuntimeDao;
    }

    public RuntimeExceptionDao<Pid, Integer> getPidDao() {
        if (pidRuntimeDao == null) {
            pidRuntimeDao = getRuntimeExceptionDao(Pid.class);
        }
        return pidRuntimeDao;
    }



    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        tokenDao = null;
        tokenRuntimeDao = null;
        pidDao = null;
        pidRuntimeDao = null;
    }
}
