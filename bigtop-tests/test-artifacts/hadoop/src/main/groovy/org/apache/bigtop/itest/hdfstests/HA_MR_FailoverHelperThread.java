package org.apache.bigtop.itest.hdfstests;

import static org.junit.Assert.assertTrue;

import org.apache.bigtop.itest.shell.Shell;

// Thread class that runs teragen, terasort, teravalidate sequence
public class HA_MR_FailoverHelperThread implements Runnable {
	Thread t;
	String name;
	boolean done = false;
	boolean genFail = false;
	boolean sortFail = false;
	boolean validateFail = false;
	boolean exception = false;
	private static final String HADOOP_HOME = System.getenv("HADOOP_HOME");
	private static Shell shThread = new Shell("/bin/bash -s");
	private static String date = shThread.exec("date").getOut().get(0).replaceAll("\\s","").replaceAll(":","");
	private static String terasortInput = "terasortInput" + date;
	private static String teravalInput = "teravalInput" + date;
	private static String teravalOutput = "teravalOutput" + date;
	String exceptionString = "";

	public HA_MR_FailoverHelperThread(String n) {
		t = new Thread(this, n);
		name = n;
	}

	public void run() {
		try { 
			while (TestHA_MR_Failover.noActiveNN) { }
			shThread.exec("hadoop jar " + HADOOP_HOME + "/hadoop-examples.jar teragen 10000000 " + terasortInput);
			if (shThread.getRet() != 0) {
				shThread.exec("hadoop fs -rmr " + terasortInput);
				genFail = true;
				done = true;
				assertTrue("teragen failed", false);
			}

			while (TestHA_MR_Failover.noActiveNN) { }
			shThread.exec("hadoop jar " + HADOOP_HOME + "/hadoop-examples.jar terasort " + terasortInput + " " + teravalInput);
			if (shThread.getRet() != 0) {
				shThread.exec("hadoop fs -rmr " + terasortInput, "hadoop fs -rmr " + teravalInput);
				sortFail = true;
				done = true;
				assertTrue("terasort failed", false);
			}

			while (TestHA_MR_Failover.noActiveNN) { }
			shThread.exec("hadoop jar " + HADOOP_HOME + "/hadoop-examples.jar teravalidate " + teravalInput + " " + teravalOutput);
			if (shThread.getRet() != 0) {
				shThread.exec("hadoop fs -rmr " + terasortInput, "hadoop fs -rmr " + teravalInput, "hadoop fs -rmr " + teravalOutput);
				validateFail = true;
				done = true;
				assertTrue("teravalidate failed", false);
			}
		}

		catch(Exception e) {
			exception = true;
			exceptionString = e.getMessage();
		}

		// clean up
		shThread.exec("hadoop fs -rmr " + terasortInput, "hadoop fs -rmr " + teravalInput, "hadoop fs -rmr " + teravalOutput);
		done = true;
	}  

}