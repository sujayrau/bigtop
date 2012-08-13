package org.apache.bigtop.itest.clustermanager.distributions;

import org.apache.bigtop.itest.clustermanager.builders.*;

public class BigtopClusterManager extends ClusterManager {

	@Override
	public void stopHDFS(Host hostname) {
		try {
			hostname.execCmd("for service in /etc/init.d/hadoop-hdfs-*; do sudo $service stop; done", true , true);
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startHDFS(Host hostname) {
		try {
			hostname.execCmd("for service in /etc/init.d/hadoop-hdfs-*; do sudo $service start; done", true, true);
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void restartHDFS(Host hostname) {
		stopHDFS(hostname);
		startHDFS(hostname);
	}

	@Override
	public void stopMR(Host hostname) {
		// TODO Auto-generated method stub
	}

	@Override
	public void startMR(Host hostname) {
		// TODO Auto-generated method stub
	}

	@Override
	public void restartMR(Host hostname) {
		// TODO Auto-generated method stub
	}

	@Override
	public void startDaemon(String daemon, Host hostname) {
		// TODO Auto-generated method stub
	}

	@Override
	public void stopDaemon(String daemon, Host hostname) {
		// TODO Auto-generated method stub
	}

	@Override
	public void restartDaemon(String daemon, Host hostname) {
		// TODO Auto-generated method stub
	}

	@Override
	public void startRegionServer(Host hostname) {
		// TODO Auto-generated method stub
	}

	@Override
	public void stopRegionServer(Host hostname) {
		// TODO Auto-generated method stub
	}

	@Override
	public void restartRegionServer(Host hostname) {
		// TODO Auto-generated method stub
	}

	@Override
	public void startHMaster(Host hostname) {
		// TODO Auto-generated method stub
	}

	@Override
	public void stopHMaster(Host hostname) {
		// TODO Auto-generated method stub
	}

	@Override
	public void restartHMaster(Host hostname) {
		// TODO Auto-generated method stub
	}

	@Override
	public void startNameNode(Host hostname) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopNameNode(Host hostname) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restartNameNode(Host hostname) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startDataNode(Host hostname) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopDataNode(Host hostname) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restartDataNode(Host hostname) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopJobTracker(Host hostname) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startJobTracker(Host hostname) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restartJobTracker(Host hostname) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopTaskTracker(Host hostname) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startTaskTracker(Host hostname) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restartTaskTracker(Host hostname) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startHBase(Host hostname) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopHBase(Host hostname) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restartHBase(Host hostname) {
		// TODO Auto-generated method stub
		
	}

}
