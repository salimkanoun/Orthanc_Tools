/**
Copyright (C) 2017 VONGSALAT Anousone & KANOUN Salim

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public v.3 License as published by
the Free Software Foundation;

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.petctviewer.orthanc.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

public class SendFilesToRemote {

	public static final String OPTION_FTP = "FTP";
	public static final String OPTION_SFTP = "SFTP";
	public static final String OPTION_WEBDAV = "WEBDAV";

	private String filePath;
	private String chosenOption;
	private String remotePath;
	private String remoteFileName;
	private String serverAdress;
	private int port;
	private String login;
	private String pwd;

	public SendFilesToRemote(String option, String remotePath, String remoteFileName, String filePath, String serverAdress, int port, String login, String pwd){
		switch (option) {
		case OPTION_FTP:
			chosenOption = OPTION_FTP;
			break;
		case OPTION_SFTP:
			chosenOption = OPTION_SFTP;
			break;
		case OPTION_WEBDAV:
			chosenOption = OPTION_WEBDAV;
			break;
		default:
			chosenOption = OPTION_FTP;
			break;
		}
		this.filePath = filePath;
		this.remotePath = remotePath;
		if(remotePath.charAt(remotePath.length() - 1) != '/'){
			this.remotePath = remotePath + '/';
		}
		this.remoteFileName = remoteFileName;
		this.serverAdress = serverAdress;
		this.port = port;
		this.login = login;
		this.pwd = pwd;
	}

	public void export() throws IOException{
		FileInputStream fis = null;
		try{
			File localFile = new File(this.filePath);
			switch (chosenOption) {
			case OPTION_FTP:
				FTPClient client = new FTPClient();

				client.connect(this.serverAdress, this.port);
				client.login(this.login, this.pwd);
				client.enterLocalPassiveMode();
				client.setFileType(FTPClient.BINARY_FILE_TYPE);

				String remoteFile = this.remotePath + this.remoteFileName;
				fis = new FileInputStream(localFile);
				client.storeFile(remoteFile, fis);
				client.logout();
				break;
			case OPTION_SFTP:
				StandardFileSystemManager manager = new StandardFileSystemManager();
				manager.init();

				FileObject localFileSFTP = manager.resolveFile(this.filePath);
				FileObject remoteFileSFTP = manager.resolveFile("sftp://" + this.login + ":" + 
						this.pwd + "@" + this.serverAdress + ":" + this.port + this.remotePath + this.remoteFileName);
				// Copy local file to sftp server
				remoteFileSFTP.copyFrom(localFileSFTP, Selectors.SELECT_SELF);
				manager.close();
				break;
			case OPTION_WEBDAV:
				fis = new FileInputStream(localFile);
				Sardine sardine = SardineFactory.begin(this.login, this.pwd);
				sardine.put(this.serverAdress + this.remotePath + this.remoteFileName, fis);
				break;
			default:
				break;
			}
		}finally{
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
