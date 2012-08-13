package org.apache.bigtop.itest.clustermanager.builders;

public class Daemon {
	
	private String name;
	private String pid;
	
	public Daemon() {
		
	}
	
	public Daemon(String name) {
		this.setName(name);
	}
	
	public Daemon(String name, String pid) {
		this.setName(name);
		this.setPid(pid);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the pid
	 */
	public String getPid() {
		return pid;
	}

	/**
	 * @param pid the pid to set
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}

}
