package org.apache.bigtop.itest.clustermanager.builders;



public class ClusterManagerFactory {
	
	public ClusterManagerFactory() {
		
	}
	
	public ClusterManager buildContext(String distribution) {
		return ManagerBuilder.getContext(distribution);
	}
}
