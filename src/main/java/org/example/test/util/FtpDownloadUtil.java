package org.example.test.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Author: yuanchao
 * @DATE: 2023/4/27 9:58
 */
public class FtpDownloadUtil {

    private static final Logger logger = LoggerFactory.getLogger(FtpDownloadUtil.class);

    private static final int BUFFER_SIZE = 4096;

    private static final int port = 21;

    public static void ftpDownLoad(String server, String user, String password, String remotePath, String localPath) {

        FTPClient ftpClient = initFTPClient(server, user, password);

        try {
            downloadDirectory(ftpClient, remotePath, localPath);
        } catch (IOException e) {
            logger.error("调用FTP下载文件时异常：{}", e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                logger.error("关闭FTP连接时异常：{}", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static FTPClient initFTPClient(String server, String user, String password) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            return ftpClient;
        } catch (IOException e) {
            logger.error("连接FTP时异常：{}", e.getMessage());
            e.printStackTrace();
        }
        return ftpClient;
    }

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
