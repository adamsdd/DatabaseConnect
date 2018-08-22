/**
 * @author Dawid Adamczyk #adamsdd
 */

package pl.adamsdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class DatabaseConnect extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;
	
	private String driverName = "oracle.jdbc.driver.OracleDriver";
	private String url = "przykladowyURLdoBazyDanych";
	private String user = "login";
	private String pass = "password";
	private Connection con;
	private Statement stmt;
	private ResultSet rs;
	private ResultSetMetaData metaData;
	private int columnCount;
	private String[] colName;
	@SuppressWarnings("rawtypes")
	private List rows;
	
	public DatabaseConnect()
	{
		try {
			Class.forName(driverName);
			System.out.println("Get connection...");
			con = DriverManager.getConnection(url, user, pass);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void executeQuery(String query) throws SQLException 
	{
		if (stmt == null)
			stmt = con.createStatement();

		stmt.execute(query);
		rs = stmt.getResultSet();
		metaData = rs.getMetaData();
		columnCount = metaData.getColumnCount();
		colName = new String[columnCount];
		
		for (int i = 1; i <= columnCount; i++) 
			colName[i-1] = metaData.getColumnName(i);

		rows = new ArrayList();
		while (rs.next()) 
		{
			List row = new ArrayList();
			for (int i = 1; i <= columnCount; i++) 
			{
				row.add(rs.getObject(i));
			}
			rows.add(row);
		}

		fireTableChanged(null);
	}
	
	/*
	 * @Pomocnicza metoda zamykajaca polaczenie
	 */
	public void close()
	{
		try {	
			if(stmt != null)
				stmt.close();
			if(con != null)
				con.close();

		} catch (SQLException e) {
			System.exit(1);
		}
	}
	
	public int getColumnCount()
	{
		if(colName == null)
			return 1;
		else
			return colName.length;
	}
	
	public int getRowCount()
	{
		if(rows == null)
			return 0;
		else
			return rows.size();
	}
	
	@Override
	public String getColumnName(int col) 
	{
		if(colName == null)
			return "";
		else
			return colName[col];
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int col)
	{
		Class c = null;
		String type;
		
		try {
			type = metaData.getColumnClassName(col+1);
			c = Class.forName(type);
		} catch (Exception e) {
			return super.getColumnClass(col);
		} 
		
		return c;
	}
	
	@Override
	public boolean isCellEditable(int row, int col)
	{
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	public Object getValueAt(int i, int j)
	{
		List row = (List)rows.get(i);
		return row.get(j);
	}
	
	@Override
	public void setValueAt(Object value, int i, int j)
	{

	}
}
