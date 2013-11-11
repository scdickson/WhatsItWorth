//Sam Dickson
//MySQL Database Connection Class
//Sep. 22, 2011

import java.util.ArrayList;
import java.util.Scanner;
import java.sql.*;

public class DBConnection
{
        protected Connection c = null;
        private String username, url, password, database;
        private int port = -1;
        private boolean detailedError = false;
        
        public DBConnection(String url, int port, String username, String password)
        {
                this.url = url;
                this.port = port;
                this.username = username;
                this.password = password;
        }
        
        public DBConnection(String url, String username, String password)
        {
                this.url = url;
                port = -1;
                this.username = username;
                this.password = password;
        }
        
        public void detailedErrorOn(boolean on)
        {
                detailedError = on;
        }
        
        public void connect(String database)
        {
                try
                {
                        if(port > 0)
                        {
                                url = "jdbc:mysql://" + url + ":" + port + "/" + database;
                        }
                        else
                        {
                                url = "jdbc:mysql://" + url + "/" + database;
                        }
                        Class.forName ("com.mysql.jdbc.Driver").newInstance();
                        c = DriverManager.getConnection(url, username, password);
                        //getConnectionStatus();
                }
                catch(Exception e)
                {
                        if(true)
                        {
                                e.printStackTrace();
                        }
                        else
                        {
                                System.out.println("\n**Error Connecting To " + url + "**\n");
                        }
                }
        }
        
        public void disconnect()
        {
                try
                {
                        c.close();
                        c = null;
                        //getConnectionStatus();
                }
                catch(Exception e)
                {
                        if(detailedError)
                        {
                                e.printStackTrace();
                        }
                        else
                        {
                                System.out.println("\n**Error Disconnecting**\n");
                        }
                }
        }
        
        public void getConnectionStatus()
        {
                if(c!=null)
                {
                System.out.println("++++++++++++++++++++++++++++++\nConnection Established.\n" + "SQL Server Path: " + url + "\nConnected as " + username + "\n++++++++++++++++++++++++++++++");
                }
                else
                {
                        System.out.println("++++++++++++++++++++++++++++++\nConnection Closed.\n++++++++++++++++++++++++++++++");
                }
        }
        
        
        public void changeConnectionVariables(String url, String username, String password, String database)
        {
                if(c!=null)
                {
                        disconnect();
                }
                
                this.url = url;
                this.username = username;
                this.password = password;
                this.database = database;
                connect(this.database);
        }
        
        public ArrayList<String> query(String query)
        {
                ArrayList<String> data = new ArrayList<String>();
                query.replace("\"","'");
                
                String substr = query.substring(0, 6);
                substr = substr.toUpperCase();
                if(substr.contains("CREATE") || substr.contains("ALTER") || substr.contains("INSERT") || substr.contains("REPLACE") || substr.contains("UPDATE") || substr.contains("DELETE"))
                {
                        queryWriteDB(query);
                }
                else
                {
                        data = queryReadDB(query);
                }        
                return data;
        }
        
        
        private ArrayList<String> queryReadDB(String query)
        {
                ArrayList<String> data = new ArrayList<String>();
                try
                {
                        
                        Statement statement = c.createStatement();
                        ResultSet rs = statement.executeQuery(query);
                        //System.out.println("-----------------------------");
                        data = returnResult(rs);
                        statement.close();
                }
                catch(Exception e)
                {
                        if(detailedError)
                        {
                                e.printStackTrace();
                        }
                        else
                        {
                                System.out.println("\n**Error Creating Query (Read): " + query + "**\n");
                        }
                }
                
                return data;
        }
        
        private void queryWriteDB(String query)
        {
                try
                {
                        
                        Statement statement = c.createStatement();
                        int val = statement.executeUpdate(query);
                        //System.out.println("-----------------------------");
                        statement.close();
                }
                catch(Exception e)
                {
                        if(detailedError)
                        {
                                e.printStackTrace();
                        }
                        else
                        {
                                System.out.println("\n**Error Creating Query (Write): " + query + "**\n");
                        }
                }
                
        }
        
        private String getRealColumnName(String columnName)
        {
                columnName = columnName.replace("_", " ");
                columnName = columnName.toUpperCase();
                return columnName;
        }
        
        
        private ArrayList<String> returnResult(ResultSet rs)
        {
                ArrayList<String> data = new ArrayList<String>();
                try
                {        
                                ResultSetMetaData rsmd = rs.getMetaData();                
                                while(rs.next())
                                {
                                        for (int i = 1; i <= rsmd.getColumnCount(); i++)
                                        {                                        
                                                data.add(getRealColumnName(rsmd.getColumnName(i) + ": ") + rs.getString(i));
                                        }
                                        data.add(" ");
                                }
                }
                catch(Exception e)
                {
                        if(detailedError)
                        {
                                e.printStackTrace();
                        }
                        else
                        {
                                System.out.println("\n**Error Returning Data**\n");
                        }
                }
                return data;
        }
        }
