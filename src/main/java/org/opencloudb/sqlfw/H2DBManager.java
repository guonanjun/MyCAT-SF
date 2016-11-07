package org.opencloudb.sqlfw;
import org.opencloudb.config.model.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * H2DB Server Manager
 *
 * @author zagnix
 * @version 1.0
 * @create 2016-10-20 16:13
 */

public class H2DBManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(H2DBManager.class);
    private static final String h2dbURI = "jdbc:h2:"+SystemConfig.getHomePath()+"/h2db/db_sqlfw";// H2 database;
    private static final String dbName = "db_sqlfw";
    private final String sqlRecordTableName = "sql_record";
    private final String sqlBackListTableName = "sql_blacklist";
    private final String sqlReporterTableName = "sql_reporter";
    private static  final String user = "sa";
    private static  final String key = "";
    private  Connection h2DBConn;

    private static final H2DBManager h2DBManager = new H2DBManager();

    private H2DBManager() {
        init();
    }

    /**
     * 初始化 H2DB 中的 db_sqlfw数据连接
     */
    private void init(){
        Connection conn = null;
        Statement stmt = null;
        try {
            try {
                Class.forName("org.h2.Driver");// H2 Driver
            } catch (Exception e) {
                e.printStackTrace();
            }
            conn = DriverManager.getConnection(h2dbURI, user, key);

            /**
             * 判断表是否存在，存在就不执行创建了。
             */

            /**
             * table:sql_backlist
             * DROP TABLE IF EXISTS sql_blacklist;
             * CREATE TABLE sql_blacklist(sql_id INT PRIMARY KEY, sql VARCHAR(255));
             *
             * table:sql_reporter
             * DROP TABLE IF EXISTS sql_reporter;
             * CREATE TABLE sql_reporter(sql VARCHAR(255) PRIMARY KEY, sql_msg VARCHAR(255),count INT);
             *
             * table:sql_record
             * DROP TABLE IF EXISTS sql_record;
             * CREATE TABLE sql_record(original_sql VARCHAR(255) PRIMARY KEY, modified_sql VARCHAR(255),result_rows
             *                                           BIGINT,exe_times BIGINT,start_time BIGINT,end_time BIGINT);
             */
            String createSqlBlacklist = "CREATE TABLE sql_blacklist(sql_id INT PRIMARY KEY, sql VARCHAR(255))";
            String createSqlReporter = "CREATE TABLE sql_reporter(sql VARCHAR(255) PRIMARY KEY, sql_msg VARCHAR(255),count INT);";

            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("show tables");


            if(!rs.next()){
                stmt.execute(createSqlBlacklist);
                stmt.execute(createSqlReporter);
            }

            rs.close();



            rs.close();

            rs = stmt.executeQuery("select * from sql_blacklist limit 1");

            if (!rs.next()){
                stmt.execute("DROP TABLE IF EXISTS sql_blacklist");
                stmt.execute(createSqlBlacklist);
            }

            rs.close();
            rs = stmt.executeQuery("select * from sql_reporter limit 1");
            if (!rs.next()){
                stmt.execute("DROP TABLE IF EXISTS sql_reporter");
                stmt.execute(createSqlReporter);
            }
            rs.close();
        }catch (SQLException sqle) {
            LOGGER.error(sqle.getMessage());
        }finally {
                try {
                    if(stmt != null) {
                        stmt.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }

        h2DBConn = conn;
    }


    public static H2DBManager getH2DBManager() {
        return h2DBManager;
    }

    /**
     * 返回H2DB一个数据库连接
     * @return
     */

    public  Connection getH2DBConn() {
        return h2DBConn;
    }
    public String getSqlRecordTableName() {
        return sqlRecordTableName;
    }
    public String getSqlBackListTableName() {
        return sqlBackListTableName;
    }
    public String getSqlReporterTableName() {
        return sqlReporterTableName;
    }

    /**
     * 关闭H2DB数据库连接
     */
    public  void closeH2DBCon(){
        if (h2DBConn != null){
            try {
                h2DBConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 执行H2DB数据库删除操作
     * @param sql
     * @return
     */
    public boolean delete(String sql){
        boolean flag = false;
        try {
            Statement stmt = h2DBConn.createStatement();
            flag = stmt.execute(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }
}
