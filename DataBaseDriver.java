package genBot2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseDriver {
	
	private int timeout = 20;
	private String fileName;
	private Connection connection;

	public DataBaseDriver(String fileName) throws SQLException {
		this.fileName = fileName;
		setup(fileName);
	}
	
	public DataBaseDriver(String fileName, boolean reset, String evolutionStackName) throws SQLException {
		this(fileName);
		
		if (reset) {
			reset(evolutionStackName);
		}
	}
		
	private void setup(String fileName) throws SQLException {
		connection = DriverManager.getConnection("jdbc:sqlite:"+ fileName + ".sqlite3");
	}
	
	public void setTimeOut(int timeout) {
		this.timeout = timeout;
	}
	
	public void reset(String evolutionStackName) throws SQLException {
		File dbFile = new File(fileName + ".sqlite3");
		if (dbFile.exists()) {
			dbFile.delete();
		}
		
		setup(fileName);

		Statement statement = connection.createStatement();
		statement.setQueryTimeout(timeout);

		statement.executeUpdate("create table " + evolutionStackName + "("
				+ "number integer primary key, "
				+ "time datetime default current_timestamp, "
				+ "generationManager blob"
				+ ")");
		
		statement.close();
	}
	
	public void insert(String evolutionStackName, int generationNumber, CocktailGenerationManager generationManager) throws SQLException {
		try {
			PreparedStatement statement = connection.prepareStatement("insert into ? (number, generationManager) values (?, ?)");
			statement.setObject(1, evolutionStackName);
			statement.setObject(2, generationNumber);
			statement.setObject(3, serialize(generationManager));
		
			statement.execute();
		} catch (SQLException | IOException e) {
			throw new SQLException("Insertion failed", e);
		}
	}
	
	public CocktailGenerationManager select(String evolutionStackName, int generationNumber) throws SQLException {
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement("select generationManager from ? where number=?");
		
			statement.setObject(1, evolutionStackName);
			statement.setObject(2, generationNumber);
		
			ResultSet rs = statement.executeQuery();
				
			if (rs.next()) {
				ObjectInputStream ois = new ObjectInputStream(
						new ByteArrayInputStream(
								rs.getBytes("generationManager")
								)
						);
            
				return (CocktailGenerationManager) ois.readObject();
			}
		} catch (SQLException | IOException | ClassNotFoundException e) {
			throw new SQLException("select failed", e);
		}
		return null;
	}
	
	public int getLastGenerationNumber(String evolutionStackName) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select max(number) from ?");
		
		statement.setObject(1, evolutionStackName);
		
		ResultSet rs = statement.executeQuery();
		
		if (rs.next()) {
			return rs.getInt("max(number)");
		}
		
		return 0;
	}
	
	private byte[] serialize(CocktailGenerationManager cocktailGenerationManager) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		
		oos.writeObject(cocktailGenerationManager);
		oos.flush();
		oos.close();
		bos.close();
		
		return bos.toByteArray();
	}

}
