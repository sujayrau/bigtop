package org.apache.bigtop.itest.clustermanager.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.bigtop.itest.shell.Shell;

public class Host {

	private String hostname;
	private String rack;
	private String ip;
	private String serviceID = "";
	private LinkedList<Daemon> daemons = new LinkedList<Daemon>();
	private static Shell sh = new Shell("/bin/bash -s");
	private String localhost = (String) sh.exec("hostname").getOut().get(0);
	ArrayList<String> daemonTypes = new ArrayList<String>();


	public Host() {	
		Collections.addAll(daemonTypes,"namenode","datanode", "zookeeper", "yarn", "jobtracker", "tasktracker", "secondarynamenode", "regionserver", "master");
	}

	public Host(String hostname) {	
		this.hostname = hostname;
	}

	public Host(String hostname, String ip, String rack) {
		this.rack = rack;
		this.hostname = hostname;
		this.ip = ip;
	}

	public void setRack(String rack) {
		this.rack = rack;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setServiceID(String serviceID) {
		this.serviceID = serviceID;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getRack() {
		return rack;
	}

	public String getHostname() {
		return hostname;
	}

	public String getIp() {
		return ip;
	}

	public String getServiceID() {
		return serviceID;
	}
	
	public void killDaemon(String daemon_name) {
		for(Iterator<Daemon> it = daemons.iterator(); it.hasNext() ; ) {
			Daemon d = it.next();
			String pid = d.getPid();
			if (d.getName().equalsIgnoreCase(daemon_name)) {
				it.remove();
				execCmd("sudo kill -9 " + pid);
			}
		}
	}

	/**
	 * Executes command on host via ssh and automatically refreshes the daemons of the host.
	 */
	public List<String> execCmd(String string) {
		if (hostname.equalsIgnoreCase(localhost)) {
			sh.exec(string);
		}
		else {
			String ssh_cmd = "ssh " + getHostname() + " " + string;
			sh.exec(ssh_cmd); 
		}
		refreshDaemons();
		return sh.getOut();
	}
	
	/**
	 * string - string representing command to be run
	 * refresh - toggles whether daemons are refreshed after command is run
	 * quoteWrap - toggles whether string is wrapped in single quotes before being run via ssh
	 */
	public List<String> execCmd(String string, boolean refresh, boolean quoteWrap) {
		if (hostname.equalsIgnoreCase(localhost)) {
			sh.exec(string);
		}
		else {
			String ssh_cmd;
			if (quoteWrap) {
				ssh_cmd = "ssh " + getHostname() + " '" + string + "'";
			}
			else {
				ssh_cmd = "ssh " + getHostname() + " " + string;
			}
			sh.exec(ssh_cmd); 
		}
		if (refresh) {
			refreshDaemons();
		}
		return sh.getOut();
	}
	
	/**
	 * Refreshes list of daemons on host.
	 */
	public void refreshDaemons() {
		for (String daemonType : daemonTypes) {	
			execCmd("ps aux | grep " + daemonType + " | grep -v grep | tr -s ' ' | cut -d ' ' -f2", false, false);
			if (sh.getOut().size() > 0) {
				boolean isNewDaemon = true;
				String currentPid = (String) sh.getOut().get(0);
				for (Daemon d : daemons) {
					if (d.getName().equals(daemonType)) {
						isNewDaemon = false;
						if (!d.getPid().equals(currentPid)) {
							d.setPid(currentPid);
						}
					}
				}

				if (isNewDaemon) {
					Daemon newDaemon = new Daemon(daemonType, currentPid);
					System.out.println("Adding " + daemonType + ":" + currentPid);
					daemons.add(newDaemon);
				}
			}

			else {
				for(Iterator<Daemon> it = daemons.iterator(); it.hasNext() ; ) {
					Daemon d = it.next();
					if (d.getName().equals(daemonType)) {
						it.remove();
					}
				}
			}
		}
	}	

	public List<Daemon> getDaemons() {
		refreshDaemons();
		return daemons;
	}

	public void printDaemons() {
		for (Daemon d : daemons) {
			System.out.println(d.getName() + " " + d.getPid());
		}
	}
}