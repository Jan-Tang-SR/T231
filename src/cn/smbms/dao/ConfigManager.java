package cn.smbms.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConfigManager {
	private static ConfigManager cm;
	private ConfigManager(){
		
	}
	private static class ConfigInstance{
		private static ConfigManager INSTANCE = new ConfigManager();
	}
	public static ConfigManager getInstance(){
		cm = ConfigInstance.INSTANCE;
		return cm;
	}
	
	private static String driver;
	private static String url;
	private static String user;
	private static String password;
	
	public void loadProperty(){
		Properties params=new Properties();
		String configFile = "database.properties";
		InputStream is=BaseDao.class.getClassLoader().getResourceAsStream(configFile);
		try {
			params.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		driver=params.getProperty("driver");
		url=params.getProperty("url");
		user=params.getProperty("user");
		password=params.getProperty("password");
	}
	
	public Connection getConnection(){
		Connection conn = null;
		try{
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, password);
		}catch(Exception ex){
			
		}
		return conn;
	}
}
