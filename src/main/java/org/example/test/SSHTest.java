package org.example.test;
import com.jcraft.jsch.*;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
/**
 * @Author: yuanchao
 * @DATE: 2023/4/28 11:00
 */
public class SSHTest {
    public static void main(String[] args) {
        String user = "root"; // 远程服务器用户名
        String password = "root"; // 远程服务器密码
        String host = "192.168.52.129"; // 远程服务器地址
        int port = 22; // 远程服务器端口
        String remoteDirectoryPath = "/opt/test/new"; // 远程服务器指定目录路径
        List<String> fileNames = getFileNamesFromRemoteDirectory(user, password, host, port, remoteDirectoryPath); // 获取远程服务器指定目录下所有文件名
        List<Map<String, Object>> filesContent = new ArrayList<>();
        for (String fileName : fileNames) {
            String remoteFilePath = remoteDirectoryPath + "/" + fileName;
            String fileContent = getFileContentFromRemoteFile(user, password, host, port, remoteFilePath);
            Yaml yaml = getYaml();
            Map<String, Object> yamlContent = yaml.load(fileContent);
            filesContent.add(yamlContent);
        }
        System.out.println(filesContent); // 打印文件内容列表
    }
    private static Yaml getYaml() {
        DumperOptions options = new DumperOptions();
        options.setIndentWithIndicator(true);
        options.setIndicatorIndent(2);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(options);
    }
    // 通过SSH协议获取远程服务器指定目录下所有文件名
    public static List<String> getFileNamesFromRemoteDirectory(String user, String password, String host, int port, String remoteDirectoryPath) {
        List<String> fileNames = new ArrayList<>();
        Session session = null;
        ChannelSftp channelSftp = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls(remoteDirectoryPath);
            for (ChannelSftp.LsEntry lsEntry : fileList) {
                String fileName = lsEntry.getFilename();
                if (!".".equals(fileName) && !"..".equals(fileName)) {
                    String filePath = remoteDirectoryPath + "/" + fileName;
                    if (lsEntry.getAttrs().isDir()) {
                        // 如果是子文件夹，递归遍历
                        List<String> subFileNames = getFileNamesFromRemoteDirectory(user, password, host, port, filePath);
                        fileNames.addAll(subFileNames);
                    } else {
                        // 如果是文件，直接添加到集合中
                        fileNames.add(fileName);
                    }
                }
            }
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
        return fileNames;
    }
    // 通过SSH协议获取远程服务器指定文件的内容
    public static String getFileContentFromRemoteFile(String user, String password, String host, int port, String remoteFilePath) {
        Session session = null;
        ChannelSftp channelSftp = null;
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            inputStream = channelSftp.get(remoteFilePath);
            outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toString("UTF-8");
        } catch (JSchException | SftpException | IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
        return null;
    }
}