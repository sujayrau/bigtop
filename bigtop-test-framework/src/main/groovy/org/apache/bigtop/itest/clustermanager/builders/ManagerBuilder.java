package org.apache.bigtop.itest.clustermanager.builders;

import java.util.NoSuchElementException;

import org.apache.bigtop.itest.clustermanager.distributions.VersionAClusterManager;


public class ManagerBuilder {
	
	public static ClusterManager getContext(String distribution) throws NoSuchElementException {
		if (distribution.equals("versionA")) {
			return new VersionAClusterManager();
		}		
		else {
			throw new NoSuchElementException("Distribution is invalid");
		}
	}

}