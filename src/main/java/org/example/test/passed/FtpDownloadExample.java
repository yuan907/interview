package org.example.test.passed;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FtpDownloadExample {
    private static final int BUFFER_SIZE = 4096;

    public static void main(String[] args) {
        String server = "192.168.52.129";
        int port = 21;
        String user = "test";
        String password = "test";
        String remotePath = "/opt/test";
        String localPath = "/local/tmp";

        FTPClient ftpClient = new FTPClient();

        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            // Download the specified remote path 
            downloadDirectory(ftpClient, remotePath, localPath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Delete local files 
        try {
            Files.walk(Paths.get(localPath))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Download the specified remote directory and its sub-directories to local. 
     */
    public static void downloadDirectory(FTPClient ftpClient, String remotePath, String localPath) throws IOException {
        FTPFile[] ftpFiles = ftpClient.listFiles(remotePath);
        for (FTPFile file : ftpFiles) {
            String remoteFilePath = remotePath + "/" + file.getName();
            String localFilePath = localPath + "/" + file.getName();
            if (!file.isDirectory()) {
                downloadFile(ftpClient, remoteFilePath, localFilePath);
            } else if (!file.getName().equals(".") && !file.getName().equals("..")) {
                downloadDirectory(ftpClient, remoteFilePath, localPath);
            }
        }
    }

    /**
     * Download a single file from the FTP server. 
     */
    public static void downloadFile(FTPClient ftpClient, String remoteFilePath, String localFilePath) throws IOException {
        File localFile = new File(localFilePath);
        FileOutputStream outputStream = new FileOutputStream(localFile);
        try {
            ftpClient.retrieveFile(remoteFilePath, outputStream);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}  