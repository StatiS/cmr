package my.cmr;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;

import java.sql.*;

/**
 * Created by Jan Boznar on 01/02/2017.
 */
public class DbHelper {
    private SimpleJDBCConnectionPool connectionPool;
    TableQuery tqClients = null;
    TableQuery tqMeetings = null;
    TableQuery tqConclusions = null;

    //singleton init
    private static DbHelper instance = null;

    private boolean productionMode = true;

    protected DbHelper() {
        //exists only to defeat constructor
    }
    public static DbHelper getInstance() {
        if(instance == null) {
            instance = new DbHelper();
            instance.dbConnect();
        }
        return instance;
    }

    public SQLContainer getContainer(String tableName) {
        //return SQLcontainer for specified table
        tqClients = new TableQuery(tableName, connectionPool);
        //no versionColumn for protecting against multi user simultaneous writes yet, for write support, bind to pk
        tqClients.setVersionColumn("ID");
        try {
            return new SQLContainer(tqClients);
        } catch (SQLException e) {
            return null;
        }
    }

    private void dbConnect() {
        //establish connection pool to database via hsqldb jdbc driver
        try {
            //init connection pool, url also disables hsqldb write delay
            //productionMode declares if database is remote or local
            if (productionMode){
                connectionPool = new SimpleJDBCConnectionPool(
                        "org.hsqldb.jdbc.JDBCDriver", "jdbc:hsqldb:hsql://localhost:9001/xdb;hsqldb.write_delay=false",
                        "SA", "", 1, 5);
            } else {
                connectionPool = new SimpleJDBCConnectionPool(
                        "org.hsqldb.jdbc.JDBCDriver", "jdbc:hsqldb:file:./src/main/java/resources/db/cmrDB;hsqldb.write_delay=false",
                        "SA", "", 1, 5);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
