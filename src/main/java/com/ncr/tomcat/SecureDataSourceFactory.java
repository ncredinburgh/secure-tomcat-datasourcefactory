package com.ncr.tomcat;

import java.util.Properties;

import javax.naming.Context;
import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.DataSourceFactory;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;

public class SecureDataSourceFactory extends DataSourceFactory {

	@Override
	public DataSource createDataSource(Properties properties, Context context, boolean XA) throws Exception {
		Decryptor decryptor = new ConfigurableDecryptor(properties);
		PoolConfiguration poolProperties = SecureDataSourceFactory.parsePoolProperties(properties);
        poolProperties.setPassword(decryptor.decrypt(poolProperties.getPassword()));
		
		return super.createDataSource(properties, context, XA);
	}
}
