package com.abda.spring.lib;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

public class FileUtil {
	
	public static File getFile(String filePath) {
		File file = null;
		file = new File(filePath).getAbsoluteFile();
		return file;
	}
	
	public static File[] getFiles(String directory) {
		File file = new File(directory);
		return file.listFiles();
	}
	
	public static void createIfDoesntExist(String path) {
		File theDir = new File(path);
		if (!theDir.exists()){
		    theDir.mkdirs();
		}
	}
	
	public static String myDocumentDir(String folderName) {
		return FileSystemView.getFileSystemView().getDefaultDirectory().toString() + folderName;
	}

	public static String getFileName(String fileName) {
		return fileName.split("\\.")[0];
	}
}