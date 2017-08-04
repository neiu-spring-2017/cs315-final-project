package foodlogapplication;
import java.sql.*;

/**
 *
 * @author austin
 */
public class FoodLogDb
{
    public FoodLogDb()
	{
		//init stuff
	}

    protected Connection getConn() throws SQLException
    {
        Connection conn = DriverManager.getConnection(
                    DBConfig.HOST + DBConfig.DATABASE +
                    "?user=" + DBConfig.USERNAME +
                    "&password=" + DBConfig.PASS
            );
        return conn;
    }

    public void deleteEntry(String table, int id) throws SQLException
    {
        int response = deleteId(table, id);
        if (response == 1) {
                System.out.println("\nSuccessfully deleted entry!\n");
        } else {
                System.out.println("\nError deleting entry.\n");
        }
    }
    
    public int deleteWhere(String table, String where) throws SQLException
    {
        Connection conn = getConn();
        Statement stmt = conn.createStatement();

        String sql = "DELETE FROM " + table + " " + where;
        System.out.println(sql);
        return stmt.executeUpdate(sql);
    }
    
    public ResultSet selectView(String view, int id) throws SQLException
    {
        Connection conn = getConn();
        Statement stmt = conn.createStatement();
        
        String sql = "SELECT * FROM " + view + " WHERE id=" + id;
        return stmt.executeQuery(sql);
    }

    public int insertEntry(String table, String[] cols, String[] vals, String[] types) throws SQLException
    {
        int result = insertTo(table, cols, vals, types);
        if (result <= 0) {
                System.out.println("\nSuccessfully inserted entry!\n");
        } else {
                System.out.println("\nError inserting entry.\n");
        }
        return result;
    }

    public ResultSet selectAll(String table) throws SQLException
    {
        Connection conn = getConn();
        String[] cols = new String[0];
        ResultSet rs = selectFrom(table, cols, "");
        //printResults(rs);
        return rs;
    }

    public int deleteId(String table, int id) throws SQLException
    {
        Connection conn = getConn();
        Statement stmt = conn.createStatement();

        String sql = "DELETE FROM " + table + " WHERE id='" + id + "'";

        return stmt.executeUpdate(sql);
    }

    public int insertTo(String table, String[] cols, String[] val, String[] types) throws SQLException
    {
        Connection conn = getConn();
        Statement stmt = conn.createStatement();

        String sql = "INSERT INTO " + table + " (";

        String values = "";
        String columns = "";
        for (int i = 0; i < val.length; i++) {
            columns += cols[i];
            if (types[i].equals("string"))
                values += "'" + val[i] + "'";
            else//last 2 columns are integers
                values += val[i];
            if (i != val.length - 1) {
                columns += ", ";
                values += ", ";
            } else {
                columns += ")";
                values += ")";
            }
        }
        sql += columns + " VALUES(" + values;
        System.out.println(sql);
        stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        int genKey = -1;
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            genKey = rs.getInt(1);
        }
        return genKey;
    }
    
    public int updateEntry(String table, String[] cols, String[] val, String[] types, String where) throws SQLException
    {
        Connection conn = getConn();
        Statement stmt = conn.createStatement();

        String sql = "UPDATE " + table + " set ";

        for (int i = 0; i < val.length; i++) {
            sql += cols[i] + "=";
            if (types[i].equals("string"))
                sql += "'" + val[i] + "'";
            else //last 2 columns are integers
                sql += val[i];
            if (i != val.length - 1) {
                sql += ", ";
            }
        }
        sql += where;
        System.out.println(sql);
        return stmt.executeUpdate(sql);
    }

    public ResultSet selectFrom(String table, String[] cols, String clause) throws SQLException
    {
        Connection conn = getConn();
        Statement stmt = conn.createStatement();

        String sql = "SELECT ";
        if (cols.length > 0) {
                for (int i = 0; i < cols.length; i++) {
                        sql += cols[i];
                        if (i != cols.length - 1) {
                                sql += ", ";
                        } else {
                                sql += " ";
                        }
                }
        } else {
                sql += "* ";
        }
        sql += "FROM " + table;
        if (clause.length() > 0) {
                sql += " " + clause;
        }
        System.out.println(sql);
        //Execute statement and return a resultset
        return stmt.executeQuery(sql);
    }

    /**
     *  Helper method to print ResultSet
     * @param rs
     * @return 
     * @throws java.sql.SQLException
     */
    public String printResults(ResultSet rs) throws SQLException
    {
        //Get the meta data for the table
        ResultSetMetaData rm = rs.getMetaData();
        String[] colNames = new String[rm.getColumnCount()];
        String retString = "";
        //Display data in console for now
        System.out.print("\n| ");
        for (int i = 1; i <= colNames.length; i++) {
                colNames[i - 1] = rm.getColumnName(i);
                String tempF = "%-" + colNames[i - 1].length() + "s | ";
                System.out.printf(tempF, colNames[i-1]);
                retString += "| " + colNames[i-1] + " |";
        }
        retString += "\n";
        System.out.print("\n-------------------------------------------------------------------------\n");
        //Loop through result rows
        while (rs.next()) {
                System.out.print("| ");
                //Grab print values for each column
                for (String name : colNames) {
                        String tempF = "%-" + name.length() + "s | ";
                        String tempN = rs.getString(name);
                        if (tempN.length() > name.length())
                                tempN = tempN.substring(0, name.length() - 2) + "..";
                        System.out.printf(tempF, tempN);
                        retString += "| " + tempN + " |";
                }
                retString += "\n";
                System.out.println();
        }
        return retString;
    }
}
