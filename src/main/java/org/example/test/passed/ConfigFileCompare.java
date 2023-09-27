package org.example.test.passed;

import org.example.test.respDTO.CompareRespDTO;
import org.example.test.respDTO.CompareResult;
import org.example.test.util.FtpDownloadUtil;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: yuanchao
 * @DATE: 2023/4/27 13:13
 */
public class ConfigFileCompare {

    private static final Logger logger = LoggerFactory.getLogger(ConfigFileCompare.class);


    private static final String oldFilePath = "/opt/test/old";

    private static final String newFilePath = "/opt/test/new";

    private static final String localSavePath = "/local/tmp";

    private static final String yaml = ".yaml";
    private static final String properties = ".properties";
    private static final String xml = ".xml";

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    @Test
    public void configFileCompare() throws Exception {
        CompareResult result = new CompareResult();

        List<CompareRespDTO> compareResult = new ArrayList<>();

        String timeStamp = sdf.format(System.currentTimeMillis());
        String saveOldPath = buildSavePath(timeStamp, oldFilePath);
        String saveNewPath = buildSavePath(timeStamp, newFilePath);

        try {
            initSavePath(saveOldPath);
            initSavePath(saveNewPath);

            FtpDownloadUtil.ftpDownLoad("192.168.52.129", "test", "test", oldFilePath, saveOldPath);
            FtpDownloadUtil.ftpDownLoad("192.168.52.129", "test", "test", newFilePath, saveNewPath);

            File[] oldFileList = new File(saveOldPath).listFiles();
            File[] newFileList = new File(saveNewPath).listFiles();

            //获取新增和删除的文件集合
            List<String> deleteFileList = findChanges(oldFileList, newFileList);
            List<String> addFileList = findChanges(newFileList, oldFileList);

            //比较单个配置文件的内容
            for (File oldFile : oldFileList) {
                for (File newFile : newFileList) {
                    if (oldFile.getName().equals(newFile.getName())) {
                        CompareRespDTO compareRespDTO = compareConfigFile(oldFile, newFile);
                        compareResult.add(compareRespDTO);
                    }
                }
            }

            result.setCompareList(compareResult);
            result.setAddFileList(addFileList);
            result.setDeleteFileList(deleteFileList);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        } finally {
            deleteTmpFile(saveOldPath);
            deleteTmpFile(saveNewPath);
        }
    }

    private CompareRespDTO compareConfigFile(File oldFile, File newFile) throws IOException {
        //判断文件是yaml、property还是xml

        CompareRespDTO compareRespDTO = new CompareRespDTO();

        String fileName = oldFile.getName();

        compareRespDTO = setFileType(compareRespDTO, fileName);
        Map<String, Object> oldConfigFileMap = getConfigFileMap(oldFile);
        Map<String, Object> newConfigFileMap = getConfigFileMap(newFile);

        JSONObject oldJson = new JSONObject(oldConfigFileMap);
        JSONObject newJson = new JSONObject(newConfigFileMap);

        compareRespDTO.setOldHeader(oldFile.getName());
        compareRespDTO.setNewHeader(newFile.getName());
        compareRespDTO.setFileName(oldFile.getName());
        compareRespDTO.setPrevData(oldJson);
        compareRespDTO.setNewData(newJson);
        return compareRespDTO;
    }

    private CompareRespDTO setFileType(CompareRespDTO compareRespDTO, String fileName) {
        if (fileName.contains(properties)) {
            compareRespDTO.setProperties(true);
        } else if (fileName.contains(yaml)) {
            compareRespDTO.setYaml(true);
        } else if (fileName.contains(xml)) {
            compareRespDTO.setXML(true);
        }
        return compareRespDTO;
    }


    private Map<String, Object> getConfigFileMap(File file) throws IOException {
        Map<String, Object> map = null;
        String fileName = file.getName();
        if (fileName.contains(properties)) {
            Properties propertiesLoader = new Properties();
            propertiesLoader.load(new FileInputStream(file));
            map = (Map) propertiesLoader;
        } else if (fileName.contains(yaml)) {
            Yaml yamlLoader = getYaml();
            map = yamlLoader.load(new FileInputStream(file));
        } else if (fileName.contains(xml)) {

        }
        return map;
    }

    private Yaml getYaml() {
        DumperOptions options = new DumperOptions();
        options.setIndentWithIndicator(true);
        options.setIndicatorIndent(2);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(options);
    }


    private void deleteTmpFile(String localPath) throws IOException {
        Files.walk(Paths.get(localPath))
                .sorted((path1, path2) -> -path1.compareTo(path2))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        logger.error("删除临时文件{}时异常：{}", localPath, e.getMessage());
                        throw new RuntimeException(e);
                    }
                });
    }

    private List<String> findChanges(File[] oldFileList, File[] newFileList) {
        List<String> changedFileList = new ArrayList<>();

        for (File oldFile : oldFileList) {
            boolean tag = false;
            for (File newFile : newFileList) {
                if (newFile.getName().equals(oldFile.getName())) {
                    tag = true;
                    continue;
                }
            }
            if (!tag) {
                changedFileList.add(oldFile.getName());
            }
        }

        return changedFileList;
    }

    private String buildSavePath(String timeStamp, String path) {
        String[] split = path.split("/");

        String savePath = localSavePath + "/" + timeStamp + "/" + split[split.length - 1];
        return savePath;
    }

    private void initSavePath(String savePath) {
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

}
