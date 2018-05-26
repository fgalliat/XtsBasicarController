package com.xtase.classpath.net.ssh;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

import com.mindbright.jca.security.SecureRandom;
import com.mindbright.ssh2.SSH2ConsoleRemote;
import com.mindbright.ssh2.SSH2SFTP;
import com.mindbright.ssh2.SSH2SFTP.FileHandle;
import com.mindbright.ssh2.SSH2SFTPClient;
import com.mindbright.ssh2.SSH2SimpleClient;
import com.mindbright.ssh2.SSH2Transport;
import com.mindbright.util.RandomSeed;
import com.mindbright.util.SecureRandomAndPad;

public class SSHSession {

	protected String host;
	protected String user;
	protected SSH2Transport transport;
	protected SSH2ConsoleRemote console;
	protected SSH2SFTPClient sftp;

	public SSHSession(String host) throws Exception {

		if (host.equals("<box>")) {
//			if (Network.localIPPingClassC("192.168.1.135", 22)) {
//				host = "192.168.1.135";
//			} else {
//				host = BoxIPFetcher.getBoxAddr();
//			}
			host = com.xtase.classpath.net.Network.getHomeIP();
		}

		this.host = host;
	}

	// add an open method ????
	public void open(String user, String passwd) throws Exception {
		this.user = user;
		int port = 22;
		/*
		 * Connect to the server and authenticate using plain password
		 * authentication (if other authentication method needed check other
		 * constructors for SSH2SimpleClient).
		 */
		Socket serverSocket = new Socket(this.host, port);
		transport = new SSH2Transport(serverSocket, createSecureRandom());
		SSH2SimpleClient client = new SSH2SimpleClient(transport, this.user,
				passwd);

		/* Create the remote console to use for command execution. */
		console = new SSH2ConsoleRemote(client.getConnection());

		// ##################################################

		/*
		 * Create SFTP client which is used for the file transfer. This instance
		 * can be used multiple times to transfer many files although this
		 * exampel only uses it once. We use asynchronous mode for the
		 * SSH2SFTPClient beacuse it is much faster.
		 */
		boolean isBlocking = false;
		sftp = new SSH2SFTPClient(client.getConnection(), isBlocking);

		/**
		 * Opens a directory on the server. This must be done before one can get
		 * a list of files contained in the directory.
		 * 
		 * @param path
		 *            name of directory to open
		 * 
		 * @return A handle to the open directory.
		 */
		// sftp.opendir(path);

	}

	public void copyTo(File localFile, String remoteFile) throws Exception {
		FileInputStream fin = new FileInputStream(localFile);
		copyTo(fin, remoteFile);
		fin.close();
	}

	public void copyTo(InputStream localInput, String remoteFile)
			throws Exception {
		FileHandle handle = sftp.open(remoteFile, SSH2SFTP.SSH_FXF_WRITE
				| SSH2SFTP.SSH_FXF_CREAT | SSH2SFTP.SSH_FXF_TRUNC,
				new SSH2SFTP.FileAttributes());

		sftp.writeFully(handle, localInput);
	}

	public void copyFrom(String remoteFile, File localFile) throws Exception {
		FileHandle handle = sftp.open(remoteFile, SSH2SFTP.SSH_FXF_READ,
				new SSH2SFTP.FileAttributes());
		FileOutputStream fout = new FileOutputStream(localFile);
		sftp.readFully(handle, fout);
		fout.flush();
		fout.close();
	}

	public void copyFrom(String remoteFile, OutputStream localOuput)
			throws Exception {
		FileHandle handle = sftp.open(remoteFile, SSH2SFTP.SSH_FXF_READ,
				new SSH2SFTP.FileAttributes());
		sftp.readFully(handle, localOuput);
	}

	public String getUser() {
		return this.user;
	}

	public String getHost() {
		return this.host;
	}

	public int execCmd(String cmd, PrintStream out, PrintStream err)
			throws Exception {
		console.command(cmd, out, err);
		/* Retrieve the exit status of the command (from the remote end). */
		int exitStatus = console.waitForExitStatus();
		/*
		 * NOTE: at this point System.out will be closed together with the
		 * session channel of the console
		 */
		return exitStatus;
	}

	public String execCmdStr(String cmd) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		console.command(cmd, out, out);
		/* Retrieve the exit status of the command (from the remote end). */
		/* int exitStatus = */console.waitForExitStatus();
		return new String(baos.toByteArray());
	}

	public void execCmdInThread(String cmd, PrintStream out, PrintStream err)
			throws Exception {
		console.command(cmd, out, err);
	}

	public void close() {
		try {
			sftp.terminate();
		} catch (Exception ex) {
		}
		try {
			transport.normalDisconnect("User disconnects");
		} catch (Exception ex) {
		}
	}

	protected static SecureRandomAndPad createSecureRandom() {
		/*
		 * NOTE, this is how it should be done if you want good randomness,
		 * however good randomness takes time so we settle with just some
		 * low-entropy garbage here.
		 * 
		 * RandomSeed seed = new RandomSeed("/dev/random", "/dev/urandom");
		 * byte[] s = seed.getBytesBlocking(20); return new
		 * SecureRandomAndPad(new SecureRandom(s));
		 */
		byte[] seed = RandomSeed.getSystemStateHash();
		return new SecureRandomAndPad(new SecureRandom(seed));
	}

}