package com.dataStore.data;

public class FileData {

	private String fileName = "";
	private int numberOfDownloads = 0;
	private boolean isTorrentCreated = false ;
	
	FileData(String fileName){
		this.fileName = fileName;		
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getNumberOfDownloads() {
		return numberOfDownloads;
	}
	public void setNumberOfDownloads(int numberOfDownloads) {
		this.numberOfDownloads = numberOfDownloads;
	}
	public boolean isTorrentCreated() {
		return isTorrentCreated;
	}
	public void setTorrentCreated(boolean isTorrentCreated) {
		this.isTorrentCreated = isTorrentCreated;
	}
	
}
