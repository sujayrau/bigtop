package org.apache.bigtop.itest.clustermanager.builders;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.bigtop.itest.clustermanager.adapters.*;
import org.apache.bigtop.itest.shell.Shell;
import org.apache.hadoop.conf.Configuration;


public abstract class ClusterManager implements ClusterAdapter, HBaseAdapter, HDFSAdapter, MRAdapter {
	private static Shell shHDFS = new Shell("/bin/bash -s", "hdfs");
	private static Shell sh = new Shell("/bin/bash -s");
	private static final String localhost = (String) sh.exec("hostname").getOut().get(0);
	private LinkedList<Host> clusterHosts = new LinkedList<Host>();
	private Configuration conf;
	private boolean isHA = false;
	private boolean onNamenode = false;
	private LinkedList<Host> namenodes = new LinkedList<Host>();
	private LinkedList<Host> datanodes = new LinkedList<Host>();
	private static final String HADOOP_HOME = System.getenv("HADOOP_HOME");
	private static final String HADOOP_CONF_DIR = System.getenv("HADOOP_CONF_DIR");
	
	public ClusterManager() {
		assertNotNull("HADOOP_HOME is null", HADOOP_HOME);
		assertNotNull("HADOOP_CONF_DIR is null", HADOOP_CONF_DIR);
		initialize();
	}
	
	/**
	 * Initializes the manager by finding daemons and hosts belonging to cluster.
	 */
	public void initialize() {
		conf = new Configuration();
		
		// adding configuration files
		org.apache.hadoop.fs.Path core_path = new org.apache.hadoop.fs.Path(HADOOP_CONF_DIR + "/core-site.xml");
		org.apache.hadoop.fs.Path hdfs_path = new org.apache.hadoop.fs.Path(HADOOP_CONF_DIR + "/hdfs-site.xml");
		org.apache.hadoop.fs.Path mapred_path = new org.apache.hadoop.fs.Path(HADOOP_CONF_DIR + "/mapred-site.xml");
		conf.addResource(core_path);
		conf.addResource(hdfs_path);
		conf.addResource(mapred_path);

		// High Availability Cluster
		if (conf.get("dfs.ha.namenodes.ha-nn-uri") != null) {
			isHA = true;
			Host nn1 = new Host();
			Host nn2 = new Host();
			try {
				nn1.setHostname(conf.get("dfs.namenode.http-address.ha-nn-uri.nn1").split(":")[0]);
				nn1.setServiceID(conf.get("dfs.ha.namenodes.ha-nn-uri").split(",")[0]);
				nn2.setHostname(conf.get("dfs.namenode.http-address.ha-nn-uri.nn2").split(":")[0]);
				nn2.setServiceID(conf.get("dfs.ha.namenodes.ha-nn-uri").split(",")[1]);
			}
			catch (NullPointerException e) {
				e.printStackTrace();
			}
			
			namenodes.add(nn1);
			namenodes.add(nn2);
		}
		
		else {
			Host nn = new Host();
			Pattern p = Pattern.compile("hdfs://(.+?):");
			String namenode = conf.get("fs.defaultFS");
			Matcher m1 = p.matcher(namenode);
			if (m1.find()) {
				nn.setHostname(m1.group(1));
			}
			namenodes.add(nn);
		}

		shHDFS.exec("hdfs dfsadmin -printTopology");
		List<String> clusterInfo = (List<String>) shHDFS.getOut();
		
		// Parsing of printTopology to determine cluster nodes
		boolean inRack = false;
		String currentRack = "";
		String currentIp;
		String currentHost;
		for (int i = 0; i < clusterInfo.size(); i++) {
			if (!inRack && clusterInfo.get(i).toLowerCase().contains("rack:")) {
				inRack = true;
				currentRack = clusterInfo.get(i).split("/")[1];
			}
			else if (!clusterInfo.get(i).isEmpty()) {
				
				String[] node = clusterInfo.get(i).trim().split(" ");
				boolean isNN = false;
				currentIp = node[0];
				
				currentHost = node[1].substring(1, node[1].indexOf(")"));
				for (Host nn : namenodes) {
					if (nn.getHostname().equals(currentHost)) {
						nn.setIp(currentIp);
						nn.setRack(currentRack);
						isNN = true;
					}
				}
				if (!isNN) {
					Host dataNode = new Host(currentHost, currentIp, currentRack);
					dataNode.refreshDaemons();
					datanodes.add(dataNode);
					clusterHosts.add(dataNode);
					isNN = false;
				}
			}
		}

		for (Host nn : namenodes) {
			nn.refreshDaemons();
			clusterHosts.add(nn);
			if (nn.getHostname().equals(localhost)) {
				onNamenode = true;
			}
		}

	}
	
	/**
	 * Returns hosts in cluster
	 */
	public List<Host> getCluster() {
		return clusterHosts;
	}
	/**
	 * Returns namenodes in cluster
	 */
	public List<Host> getNamenodes() {
		return namenodes;
	}
	/**
	 * Returns datanodes in cluster
	 */
	public List<Host> getDatanodes() {
		return datanodes;
	}
	/**
	 * Performs kill -9 of specified daemon on specified machine. Cluster Daemons: NameNode, DataNode, JobTracker, TaskTracker, SecondaryNameNode, HRegionServer, HMaster
	 */
	public void killDaemon(String daemon, Host hostname) {
		assertTrue(hostname != null);
		hostname.killDaemon(daemon);
	}
	/**
	 * Returns true if specified daemon is running on specified machine. Cluster Daemons: NameNode, DataNode, JobTracker, TaskTracker, SecondaryNameNode, HRegionServer, HMaster
	 */
	public boolean isDaemonRunning(String daemon, Host hostname) {
		assertTrue(hostname != null);
		boolean isRunning = false;
    	for (Daemon d : hostname.getDaemons()) {
    		if (d.getName().equalsIgnoreCase(daemon)) {
    			isRunning = true;
    		}
    	}
		return isRunning;
	}
	/**
	 * Returns List<Daemon> containing which of the following processes are running on the host: NameNode, DataNode, JobTracker, TaskTracker, SecondaryNameNode, HRegionServer, HMaster
	 */
	public List<Daemon> runningDaemons(Host hostname) {
		assertTrue(hostname != null);
		return hostname.getDaemons();
	}
	/**
	 * Stalls thread until specified daemon is running on specified machine or timeout value in milliseconds is reached.
	 * @throws Exception 
	 */
	public void waitUntilStarted(String daemon, Host hostname, long timeout) throws Exception {
		waitUntilHelper(daemon, hostname, timeout, true);
	}
	/**
	 * Stalls thread until specified daemon is stopped on specified machine or timeout value is reached.
	 * @throws Exception 
	 */
	public void waitUntilStopped(String daemon, Host hostname, long timeout) throws Exception {
		waitUntilHelper(daemon, hostname, timeout, false);
	}
	
	private void waitUntilHelper(String daemon, Host hostname, long timeout, boolean isStarting) throws Exception {
		assertTrue(hostname != null);
		long endTime = System.currentTimeMillis() + timeout;
		boolean done = false;
	    while (!done) {
	        if (System.currentTimeMillis() > endTime) {
	            throw new Exception("Timeout value reached");
	        }
	        if (isStarting) {
	        	done = isDaemonRunning(daemon, hostname);
	        }
	        else {
	        	done = !isDaemonRunning(daemon, hostname);
	        }
	    }	
	}
	
	/**
	 * Runs shell command on specified machine in cluster. Output is output of command.
	 */
	public List<String> runShellCommand(String command, Host hostname) {
		assertTrue(hostname != null);
		return hostname.execCmd(command);
	}
	
	public List<String> runShellCommand(String command, Host hostname, boolean refresh, boolean quoteWrap) {
		assertTrue(hostname != null);
		return hostname.execCmd(command, refresh, quoteWrap);
	}
	/**
	 * Returns whether cluster is in safemode or not.
	 */
	public boolean getSafemodeStatus() {
		sh.exec("hdfs dfsadmin -safemode get");
		for (String each : (List<String>) sh.getOut()) {
			if (each.contains("OFF")) {
				return false;
			}
			if (each.contains("ON")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Puts cluster in safemode.
	 */
	public boolean enterSafemode() {
		shHDFS.exec("hdfs dfsadmin -safemode enter");
		return shHDFS.getRet() == 0;
	}
	/**
	 * Takes cluster out of safemode.
	 */
	public boolean leaveSafemode() {
		shHDFS.exec("hdfs dfsadmin -safemode leave");
		return shHDFS.getRet() == 0;
	}
	/**
	 * Stalls thread until cluster is out of safemode.
	 * @throws InterruptedException 
	 */
	public void waitUntilLeftSafemode() throws InterruptedException {
		while (getSafemodeStatus()) {
			System.out.println("Waiting...");
			Thread.sleep(3000);
		}
	}
	
	/**
	 * Perform High Availability namenode failover.
	 */
	public void failover() {
		String standby = getStandbyNN().getServiceID();
		String active = "";
		Host active_host = null;
		for (Host h : namenodes) {
			if (!h.getServiceID().equalsIgnoreCase(standby)) {
				active = h.getServiceID();
				active_host = h;
			}
		}
		if (onNamenode) {
			shHDFS.exec("hdfs haadmin -failover " + active + " " + standby);
		}
		else {
			runShellCommand("sudo -u hdfs hdfs haadmin -failover " + active + " " + standby, active_host, false, false);
		}
	}
	/**
	 * Get hostname of active namenode.
	 */
	public Host getActiveNN() {
		for (Host h : namenodes) {
			try {
				if (runShellCommand("sudo -u hdfs hdfs haadmin -getServiceState " + h.getServiceID(), h, false, false).get(0).equals("active")) {	
					return h;
				}
			}
			catch (Exception e) {
				
			}
		}
		return null;
	}
	/**
	 * Get hostname of standby namenode.
	 */
	public Host getStandbyNN() {
		for (Host h : namenodes) {
			try {
				if (runShellCommand("sudo -u hdfs hdfs haadmin -getServiceState " + h.getServiceID(), h, false, false).get(0).equals("standby")) {
					return h;
				}
			}
			catch (Exception e){
				
			}
		}
		return null;
	}
	/**
	 * Set value of configuration property.
	 */
	public void setConfiguration(String property, String value) {
		conf.set(property, value);
	}
	/**
	 * Get value of configuration property.
	 */
	public String getConfiguration(String property) {
		return conf.get(property);
	}
}
