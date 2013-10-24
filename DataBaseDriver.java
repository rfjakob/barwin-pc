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
	
	public DataBaseDriver(String fileName, boolean reset) throws SQLException {
		this(fileName);
		
		if (reset) {
			reset();
		}
	}
		
	private void setup(String fileName) throws SQLException {
		connection = DriverManager.getConnection("jdbc:sqlite:"+ fileName);
	}
	
	public void setTimeOut(int timeout) {
		this.timeout = timeout;
	}
	
	public void reset() throws SQLException {
		File dbFile = new File(fileName);
		if (dbFile.exists()) {
			dbFile.delete();
		}
		
		setup(fileName);

		Statement statement = connection.createStatement();
		statement.setQueryTimeout(timeout);

		statement.executeUpdate("create table cocktailgeneration("
				+ "number integer primary key, "
				+ "time datetime default current_timestamp, "
				+ "generation blob"
				+ ")");
		
		statement.close();
	}
	
	public void insert(int generationNumber, CocktailGeneration generation) throws SQLException {
		try {
			PreparedStatement statement = connection.prepareStatement("insert into cocktailgeneration (number, generation) values (?, ?)");
			statement.setObject(1, generationNumber);
			statement.setObject(2, serialize(generation));
		
			statement.execute();
		} catch (SQLException | IOException e) {
			throw new SQLException("Insertion failed", e);
		}
	}
	
	public CocktailGeneration select(int generationNumber) throws SQLException {
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement("select generation from cocktailgeneration where number=?");
		
			statement.setObject(1, generationNumber);
		
			ResultSet rs = statement.executeQuery();
				
			if (rs.next()) {
				ObjectInputStream ois = new ObjectInputStream(
						new ByteArrayInputStream(
								rs.getBytes("generation")
								)
						);
            
				return (CocktailGeneration) ois.readObject();
			}
		} catch (SQLException | IOException | ClassNotFoundException e) {
			throw new SQLException("select failed", e);
		}
		return null;
	}
	
	public int getLastGenerationNumber() throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select max(number) from cocktailgeneration");
		
		ResultSet rs = statement.executeQuery();
		
		if (rs.next()) {
			return rs.getInt("max(number)");
		}
		
		return 0;
	}
	
	private byte[] serialize(CocktailGeneration cocktailGeneration) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		
		oos.writeObject(cocktailGeneration);
		oos.flush();
		oos.close();
		bos.close();
		
		return bos.toByteArray();
	}

}
