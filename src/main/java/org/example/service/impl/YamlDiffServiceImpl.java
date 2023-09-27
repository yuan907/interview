package org.example.service.impl;


import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import groovy.util.logging.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.example.service.YamlDiffService;
import org.example.util.FtpUtil;
import org.example.vo.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

/**
 * @Author: yuanchao
 * @DATE: 2023/4/23 10:19
 */
@Service
@Slf4j
public class YamlDiffServiceImpl implements YamlDiffService {

    private static final Logger logger = LoggerFactory.getLogger(YamlDiffServiceImpl.class);


    private String url = "192.168.52.129";
    private int port = 21;
    private String username = "test";
    private String password = "test";

    private String oldUrl = "C:/Users/Administrator/Desktop/test/old";
    private String newUrl = "C:/Users/Administrator/Desktop/test/new";

    private String oldFtpUrl = "/opt/test/old";
    private String newFtpUrl = "/opt/test/new";

    private String ADD_TAG = "+++++++++";
    private String DELETE_TAG = "---------";
    private String UPDATE_TAG = " =======> ";

    public static final String properties = "properties";

    public static final String yaml = "yaml";


    @Override
    public ConfigVO test() throws IOException {
        //定义返回的结果集
        ConfigVO result = new ConfigVO();
        List<ConfigUpdateVO> updateList = new ArrayList<>();
        List<ConfigAddVO> addList = new ArrayList<>();
        List<ConfigDelVO> delList = new ArrayList<>();

        FTPClient ftpClient = null;
        try {
            ftpClient = FtpUtil.getFtpClient(url, port, username, password);
        } catch (Exception e) {
            logger.error("连接服务器失败：{}",e.getMessage());
        }
        FTPFile[] ftpOldFiles = FtpUtil.getFTPDirectoryFiles(ftpClient, oldFtpUrl);
        FTPFile[] ftpNewFiles = FtpUtil.getFTPDirectoryFiles(ftpClient, newFtpUrl);


        File[] oldFileList = new File(oldUrl).listFiles();
        File[] newFileList = new File(newUrl).listFiles();
        //比较文件差异

        for (File newFile : newFileList) {
            boolean updateFlag = false;

            ConfigUpdateVO configUpdateVO = new ConfigUpdateVO();
            Map<String, Object> add = new HashMap<>();
            List<String> del = new ArrayList<>();
            configUpdateVO.setDel(del);
            configUpdateVO.setAdd(add);

            //获取有变动的 配置文件
            for (File oldFile : oldFileList) {
                if (newFile.getName().equals(oldFile.getName())) {
                    updateFlag = true;

                    Map<String, Object> oldFileMap = getConfigFileMap(oldFile);
                    Map<String, Object> newFileMap = getConfigFileMap(newFile);

                    //获取当前文件新增和删除的key
                    getAddAndDelList(oldFileMap, newFileMap, null, configUpdateVO);

                    Map<String, Object> updateResultMap = new LinkedHashMap<>();
                    getUpdateKeys(oldFileMap, newFileMap, null, updateResultMap);
                    configUpdateVO.setUpdate(updateResultMap);

                    if (!add.isEmpty() || !del.isEmpty() || !updateResultMap.isEmpty()) {
                        configUpdateVO.setConfigFileName(newFile.getName());
                    }

                }
            }
            if (null != configUpdateVO.getConfigFileName() && !"".equals(configUpdateVO.getConfigFileName())) {
                updateList.add(configUpdateVO);
            }

            //获取新增的配置文件
            if (!updateFlag) {
                ConfigAddVO configAddVO = new ConfigAddVO();
                configAddVO.setConfigFileName(newFile.getName());
                addList.add(configAddVO);
            }
        }

        //获取删除的配置文件
        for (File oldFile : oldFileList) {
            boolean tag = false;
            for (File newFile : newFileList) {
                if (newFile.getName().equals(oldFile.getName())) {
                    tag = true;
                    continue;
                }
            }
            if (!tag) {
                ConfigDelVO configDelVO = new ConfigDelVO();
                configDelVO.setConfigFileName(oldFile.getName());
                delList.add(configDelVO);
            }
        }

        result.setUpdateList(updateList);
        result.setAddList(addList);
        result.setDelList(delList);

        return result;
    }

    private Map<String, Object> getConfigFileMap(File file) throws IOException {
        Map<String, Object> map = null;
        if (file.getName().contains(properties)) {
            Properties propertiesLoader = new Properties();
            propertiesLoader.load(new FileInputStream(file));
            map = (Map) propertiesLoader;
        } else if (file.getName().contains(yaml)) {
            Yaml yamlLoader = getYaml();
            map = yamlLoader.load(new FileInputStream(file));
        }
        return map;
    }

    @Override
    public void test2() throws FileNotFoundException {

        Yaml yaml = getYaml();

        File oldFile = new File("C:/Users/Administrator/Desktop/test/old/smart-delivery-admin-server-dev.yaml");
        File newFile = new File("C:/Users/Administrator/Desktop/test/new/smart-delivery-admin-server-dev.yaml");


        ConfigVO result = new ConfigVO();
        List<ConfigUpdateVO> updateList = new ArrayList<>();


        Map<String, Object> oldFileMap = yaml.load(new FileInputStream(oldFile));
        Map<String, Object> newFileMap = yaml.load(new FileInputStream(newFile));

        ConfigUpdateVO configUpdateVO = new ConfigUpdateVO();
        Map<String, Object> add = new HashMap<>();
        List<String> del = new ArrayList<>();
        configUpdateVO.setDel(del);
        configUpdateVO.setAdd(add);

        getAddAndDelList(oldFileMap, newFileMap, null, configUpdateVO);

        result.setUpdateList(updateList);
    }

    private void getAddAndDelList(Map<String, Object> oldFileMap, Map<String, Object> newFileMap, String parnteKey, ConfigUpdateVO configUpdateVO) {
        MapDifference<String, Object> difference = Maps.difference(oldFileMap, newFileMap);


        if (!difference.areEqual()) {
            Map<String, MapDifference.ValueDifference<Object>> differenceMap = difference.entriesDiffering();

            Set<String> keySet = differenceMap.keySet();

            for (String key : keySet) {
                if (oldFileMap.get(key).getClass() == newFileMap.get(key).getClass()) {
                    if (oldFileMap.get(key) instanceof Map) {
                        Map<String, Object> oldKeyMap = (Map<String, Object>) oldFileMap.get(key);
                        Map<String, Object> newKeyMap = (Map<String, Object>) newFileMap.get(key);

                        Set<String> keySet1 = oldKeyMap.keySet();
                        Set<String> keySet2 = newKeyMap.keySet();
                        Sets.SetView<String> difference1 = Sets.difference(keySet1, keySet2);
                        Sets.SetView<String> difference2 = Sets.difference(keySet2, keySet1);

                        if (difference1.size() > 0) {
                            //删除的键值对
                            for (String delKey : difference1) {
                                if (null != parnteKey && !"".equals(parnteKey)) {
                                    configUpdateVO.getDel().add(parnteKey + "." + key + "." + delKey);
                                    System.out.println("删除的：" + parnteKey + "." + key + "." + delKey);
                                } else {
                                    configUpdateVO.getDel().add(key + "." + delKey);
                                    System.out.println("删除的：" + key + "." + delKey);
                                }
                            }

                        }
                        if (difference2.size() > 0) {
                            //新加的键值对
                            for (String addKey : difference2) {
                                if (null != parnteKey && !"".equals(parnteKey)) {
                                    configUpdateVO.getAdd().put(parnteKey + "." + key + "." + addKey, newKeyMap.get(addKey));
                                    System.out.println("新增的：" + parnteKey + "." + key + "." + addKey + "：" + newKeyMap.get(addKey));
                                } else {
                                    configUpdateVO.getAdd().put(key + "." + addKey, newKeyMap.get(addKey));
                                    System.out.println("新增的：" + key + "." + addKey + "：" + newKeyMap.get(addKey));
                                }
                            }

                        }
                        if (null != parnteKey && !"".equals(parnteKey)) {
                            getAddAndDelList(oldKeyMap, newKeyMap, parnteKey + "." + key, configUpdateVO);
                        } else {
                            getAddAndDelList(oldKeyMap, newKeyMap, key, configUpdateVO);
                        }


                    }
                } //为list类型的待定
                else if (oldFileMap.get(key) instanceof List) {
                    if (!Objects.equals(oldFileMap.get(key), newFileMap.get(key))) {
                        if (null != parnteKey && !"".equals(parnteKey)) {
                            configUpdateVO.getAdd().put(parnteKey + "." + key, newFileMap.get(key));
                            configUpdateVO.getDel().add(parnteKey + "." + key);
                        } else {
                            configUpdateVO.getAdd().put(key, newFileMap.get(key));
                            configUpdateVO.getDel().add(key);
                        }
                    }
                } else {
                    configUpdateVO.getAdd().put(key, newFileMap.get(key));
                    configUpdateVO.getDel().add(key);
                }
            }
        }
    }


    private void getUpdateKeys(Map<String, Object> oldFileMap, Map<String, Object> newFileMap, String parentKey, Map<String, Object> updateResultMap) {
        Set<String> update = getUpdateKeySet(oldFileMap.keySet(), newFileMap.keySet());
        Map<String, Object> updateMap;
        if (parentKey == null) {
            extracteUpdateMap(oldFileMap, newFileMap, update, updateResultMap);
        } else {
            updateMap = (Map<String, Object>) updateResultMap.get(parentKey);
            extracteUpdateMap(oldFileMap, newFileMap, update, updateMap);
        }
    }

    private void extracteUpdateMap(Map<String, Object> oldMap, Map<String, Object> newMap, Set<String> update, Map<String, Object> updateMap) {
        for (String key : update) {
            Object oldValue = oldMap.get(key);
            Object newValue = newMap.get(key);
            if (Objects.equals(oldValue, newValue)) {
                continue;
            }
            if (oldValue.getClass() == newValue.getClass()) {
                if (newValue instanceof Map) {
                    updateMap.put(key, new LinkedHashMap<>());
                    getUpdateKeys((Map<String, Object>) oldValue, (Map<String, Object>) newValue, key, updateMap);
                    if (((Map<String, Object>) updateMap.get(key)).isEmpty()) {
                        updateMap.remove(key);
                    }
                } else if (newValue instanceof List) {
                    if (!Objects.equals(oldValue, newValue)) {
                        updateMap.put(key, oldValue);
                        updateMap.put(key, newValue);
                    }
                } else {
                    if (!Objects.equals(oldValue, newValue)) {
                        updateMap.put(key, oldValue + UPDATE_TAG + newValue);
                    }
                }
            } else {
//                updateMap.put(DELETE_TAG + key, oldValue);
//                updateMap.put(ADD_TAG + key, newValue);
            }
        }
    }


    private Map<String, Object> getResultMap(Set<String> keys, Map<String, Object> map) {
        Map<String, Object> res = new LinkedHashMap<>();
        for (String key : keys) {
            res.put(key, map.get(key));
        }
        return res;
    }

    private Set<String> getAddKeys(Set<String> source, Set<String> target) {
        if (source == null) {
            return target;
        }
        Set<String> t = new HashSet<>(target);
        t.removeAll(source);
        return t;
    }

    private Set<String> getUpdateKeySet(Set<String> source, Set<String> target) {
        if (source == null) {
            return target;
        }
        Set<String> t = new HashSet<>(target);
        t.retainAll(source);
        return t;
    }

    private Set<String> getDeleteKeys(Set<String> source, Set<String> target) {
        if (target == null) {
            return source;
        }
        Set<String> s = new HashSet<>(source);
        s.removeAll(target);
        return s;
    }

    private Yaml getYaml() {
        DumperOptions options = new DumperOptions();
        options.setIndentWithIndicator(true);
        options.setIndicatorIndent(2);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(options);
    }


}
