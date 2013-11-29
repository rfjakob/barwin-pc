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
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DataBaseDriver {
	
	private static HashMap<String, DataBaseDriver> files;
	
	private int timeout = 20;
	private String fileName;
	private Connection connection;
	private final Lock lock;
	
	private final String dataBaseFolder = "dataBases/";

	private DataBaseDriver(String fileName) throws SQLException {
		this.fileName = dataBaseFolder + fileName;
		setupConnection(this.fileName);
		this.lock = new ReentrantLock();
	}
	
	public static DataBaseDriver getInstance(String fileName) throws SQLException {
		if (files == null) {
			files = new HashMap<String, DataBaseDriver>();
		}
		if (!files.containsKey(fileName)) {
			files.put(fileName, new DataBaseDriver(fileName));
		}
		DataBaseDriver retDriver = files.get(fileName);
		
		return retDriver;
	}
	
	public void setup(String fileName, boolean resetTable, String evolutionStackName) throws SQLException {
		evolutionStackName = spaceToUnderscore(evolutionStackName);
		
		// check if the table exists
		PreparedStatement statement = connection.prepareStatement("SELECT name FROM sqlite_master WHERE type='table' AND name='" + evolutionStackName + "'");
		
		lock.lock();
		
		ResultSet rs = statement.executeQuery();
		
		lock.unlock();
		
		if (!rs.next()) {
				Statement otherStatement = connection.createStatement();
				otherStatement.setQueryTimeout(timeout);

				lock.lock();
				
				otherStatement.executeUpdate("create table " + evolutionStackName + "("
						+ "number integer primary key, "
						+ "time datetime default current_timestamp, "
						+ "generationManager blob"
						+ ")");
				
				otherStatement.close();
				
				lock.unlock();
		}
		rs.close();
		statement.close();
				
		if (resetTable) {
			resetTable(evolutionStackName);
		}		
	}
		
	private void setupConnection(String fileName) throws SQLException {
		connection = DriverManager.getConnection("jdbc:sqlite:"+ fileName + ".sqlite3");
	}
	
	public void setTimeOut(int timeout) {
		this.timeout = timeout;
	}
	
	/*
	 * deprecated
	 */
	public void resetFile() throws SQLException {
		File dbFile = new File(fileName + ".sqlite3");
		if (dbFile.exists()) {
			dbFile.delete();
		}
		
		setupConnection(fileName);
	}
	
	public void resetTable(String evolutionStackName) throws SQLException {
		evolutionStackName = spaceToUnderscore(evolutionStackName);
		
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(timeout);

		lock.lock();
		
		statement.executeUpdate("DELETE FROM " + evolutionStackName);
		
		statement.close();
		
		lock.unlock();
	}
	
	public void insertOrUpdate(String evolutionStackName, int generationNumber, CocktailGenerationManager generationManager) throws SQLException {
		evolutionStackName = spaceToUnderscore(evolutionStackName);
		
		if (wasGenerationStoredBefore(evolutionStackName, generationNumber)) {
			update(evolutionStackName, generationNumber, generationManager);
		} else {
			insert(evolutionStackName, generationNumber, generationManager);
		}
	}
	
	public void insert(String evolutionStackName, int generationNumber, CocktailGenerationManager generationManager) throws SQLException {
		evolutionStackName = spaceToUnderscore(evolutionStackName);
		
		try {
			PreparedStatement statement = connection.prepareStatement("insert into " + evolutionStackName + " (number, generationManager) values (?, ?)");
			statement.setObject(1, generationNumber);
			statement.setObject(2, serialize(generationManager));
		
			lock.lock();
			
			statement.execute();
			statement.close();
			
			lock.unlock();
			
		} catch (SQLException | IOException e) {
			throw new SQLException("Insertion failed " + e.getMessage());
		}
	}
	
	public void update(String evolutionStackName, int generationNumber, CocktailGenerationManager generationManager) throws SQLException {
		evolutionStackName = spaceToUnderscore(evolutionStackName);
		
		try {
			PreparedStatement statement = connection.prepareStatement("update " + evolutionStackName + " set generationManager = ? where number = " + generationNumber);
			statement.setObject(1, serialize(generationManager));
		
			lock.lock();
			
			statement.execute();
			statement.close();
			
			lock.unlock();
			
		} catch (SQLException | IOException e) {
			throw new SQLException("Insertion failed " + e.getMessage());
		}
	}
	
	public CocktailGenerationManager select(String evolutionStackName, int generationNumber) throws SQLException {
		evolutionStackName = spaceToUnderscore(evolutionStackName);
		
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement("select generationManager from " + evolutionStackName + " where number=" + generationNumber);
		
			lock.lock();
			
			ResultSet rs = statement.executeQuery();
				
			lock.unlock();
			
			if (rs.next()) {
				ObjectInputStream ois = new ObjectInputStream(
						new ByteArrayInputStream(
								rs.getBytes("generationManager")
								)
						);
            
				return (CocktailGenerationManager) ois.readObject();
			}
			rs.close();
			statement.close();
			
		} catch (SQLException | IOException | ClassNotFoundException e) {
			throw new SQLException("select failed - " + e.getMessage());
		}
		return null;
	}
	
	public boolean wasGenerationStoredBefore(String evolutionStackName, int generationNumber) throws SQLException {
		evolutionStackName = spaceToUnderscore(evolutionStackName);
		
		PreparedStatement statement = connection.prepareStatement("select exists(Select 1 from " + evolutionStackName + " where number = " + generationNumber + ")");
		
		lock.lock();
		
		ResultSet rs = statement.executeQuery();
		
		lock.unlock();
		
		if (rs.next()) {
			return rs.getBoolean(1);
		}
		rs.close();
		statement.close();
		
		throw new SQLException("An error occured while trying check if saved before");
	}
	
	/*
	 * returns the last generation number or -1 if no generation was saved yet
	 */
	public int getLastGenerationNumber(String evolutionStackName) throws SQLException {
		evolutionStackName = spaceToUnderscore(evolutionStackName);
		
		// first check if there is a generationnumber
		if (!wasGenerationStoredBefore(evolutionStackName, 0)) {
			return -1;
		}
		
		// there is a generation number - now give back the number
		PreparedStatement statement = connection.prepareStatement("select max(number) from " + evolutionStackName);
		
		lock.lock();
		
		ResultSet rs = statement.executeQuery();
		
		lock.unlock();
		
		if (rs.next()) {
			return rs.getInt("max(number)");
		}
		rs.close();
		statement.close();
		
		throw new SQLException("An error occured while trying to get the last generationnumber");
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

	public void delete(String evolutionStackName) throws SQLException {
		evolutionStackName = spaceToUnderscore(evolutionStackName);
		
		// for some reason I need to close the connection and open it again
		// I think there is some error in the database implementation, but it seems to work...
		
		connection.close();
		setupConnection(fileName);
		
		PreparedStatement statement = connection.prepareStatement("drop table if exists " + evolutionStackName);
		
		lock.lock();
		
		statement.execute();
		statement.close();
		
		lock.unlock();
	}
	
	private String spaceToUnderscore(String s) {
		return s.replaceAll(" ", "_");
	}
	
	private String UnderscoreToString(String s) {
		return s.replaceAll("_", " ");
	}
}
