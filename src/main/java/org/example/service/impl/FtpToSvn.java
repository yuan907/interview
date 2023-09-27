package org.example.service.impl;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class FtpToSvn {
    public static void ftpToSvn(String ftpHost, String ftpUsername, String ftpPassword, String ftpBasePath, String svnUrl, String svnUsername, String svnPassword, String svnCommitMessage) {
        // 创建并连接FTP客户端
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(ftpHost);
            ftpClient.login(ftpUsername, ftpPassword);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            // 获取FTP服务器上指定目录下的所有文件
            FTPFile[] ftpFiles = ftpClient.listFiles(ftpBasePath);
            // 创建要提交的文件列表
            List<File> fileList = new ArrayList<>();
            for (FTPFile ftpFile : ftpFiles) {
                if (ftpFile.isFile()) {
                    // 下载文件
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ftpClient.retrieveFile(ftpBasePath + "/" + ftpFile.getName(), outputStream);
                    // 创建要提交的文件
                    File file = File.createTempFile("ftp-to-svn-", ".tmp");
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(outputStream.toByteArray());
                    fileOutputStream.close();
                    fileList.add(file);
                }
            }
            // 提交文件到SVN
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(svnUsername, svnPassword.toCharArray());
            DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
            SVNClientManager svnClientManager =SVNClientManager.newInstance(options, authManager);
            SVNCommitClient svnCommitClient = svnClientManager.getCommitClient();
            SVNURL svnUrlObj = SVNURL.parseURIEncoded(svnUrl);
            SVNCommitInfo svnCommitInfo = svnCommitClient.doCommit(
                    fileList.toArray(new File[fileList.size()]), false, svnCommitMessage, null, null, false, true, SVNDepth.INFINITY);
            System.out.println("SVN commit finished: " + svnCommitInfo.getNewRevision());
        } catch (IOException | SVNException e) {
            e.printStackTrace();
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        String ftpHost = "192.168.52.129";
        String ftpUsername = "test";
        String ftpPassword = "test";
        String ftpBasePath = "/opt/test";
        String svnUrl = "https://DESKTOP-54I4QNA/svn/test/";
        String svnUsername = "yuanchao";
        String svnPassword = "yuanchao66";
        String svnCommitMessage = "Commit message------test";
        ftpToSvn(ftpHost, ftpUsername, ftpPassword, ftpBasePath, svnUrl, svnUsername, svnPassword, svnCommitMessage);
    }
}