package org.example.service.impl;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Unit test for simple App.
 */
public class AppTest {


    private String ADD_TAG = "++";
    private String DELETE_TAG = "--";
    private String UPDATE_TAG = " => ";


    private String OLD_PATH = "C:/Users/Administrator/Desktop/test/old";
    private String NEW_PATH = "C:/Users/Administrator/Desktop/test/new";

    private String OUT_PATH = "C:/Users/Administrator/Desktop/test/update";

    @Test
    public void test() throws IOException {
        tt(OLD_PATH, NEW_PATH);
    }


    /**
     * @description 非核心逻辑
     */
    public void tt(String oldPath, String newPath) throws IOException {
        Yaml yaml = getYaml();
        File[] oldFile = new File(oldPath).listFiles();
        File[] newFile = new File(newPath).listFiles();
        if (!new File(OUT_PATH).exists()) {
            new File(OUT_PATH).mkdirs();
        }
        for (File nFile : newFile) {
            String updateFilePath = OUT_PATH + File.separator + nFile.getName();
            boolean find = false;
            for (File oFile : oldFile) {
                if (nFile.getName().equals(oFile.getName())) {
                    find = true;
                    List<Map<String, Object>> diffList = diff(oFile, nFile, yaml);
                    if (!diffList.isEmpty()) {
                        yaml.dumpAll(diffList.iterator(), new FileWriter(updateFilePath));
                    }
                    continue;
                }
            }
            if (!find) {
                FileChannel sourceChannel = null;
                FileChannel destChannel = null;
                try {
                    sourceChannel = new FileInputStream(nFile).getChannel();
                    destChannel = new FileOutputStream(updateFilePath + "--新增配置文件").getChannel();
                    destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
                } finally {
                    sourceChannel.close();
                    destChannel.close();
                }
            }
        }

        for (File oFile : oldFile) {
            String updateFilePath = OUT_PATH + File.separator + oFile.getName();
            boolean find = false;
            for (File nFile : newFile) {
                if (nFile.getName().equals(oFile.getName())) {
                    find = true;
                    continue;
                }
            }
            if (!find) {
                FileChannel sourceChannel = null;
                FileChannel destChannel = null;
                try {
                    sourceChannel = new FileInputStream(oFile).getChannel();
                    destChannel = new FileOutputStream(updateFilePath + "--删除配置文件").getChannel();
                    destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
                } finally {
                    sourceChannel.close();
                    destChannel.close();
                }
            }
        }
    }

    /**
     * @description 核心逻辑
     * @author gang.tu
     */
    public List<Map<String, Object>> diff(File oldFile, File newFile, Yaml yaml) throws FileNotFoundException {
        Map<String, Object> oldMap = yaml.load(new FileInputStream(oldFile));
        Map<String, Object> newMap = yaml.load(new FileInputStream(newFile));
        List<Map<String, Object>> all = new ArrayList<>();
        Map<String, Object> updateMap = new LinkedHashMap<>();
        all.add(getAdd(oldMap, newMap));
        all.add(getDelete(oldMap, newMap));
        deal(oldMap, newMap, null, updateMap);
        all.add(updateMap);
        all = all.stream().filter(e -> !e.isEmpty()).collect(Collectors.toList());
        return all;
    }


    private Yaml getYaml() {
        DumperOptions options = new DumperOptions();
        options.setIndentWithIndicator(true);
        options.setIndicatorIndent(2);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(options);
    }


    private void deal(Map<String, Object> oldMap, Map<String, Object> newMap, String parentKey, Map<String, Object> resultMap) {
        Set<String> update = getUpdateKeys(oldMap.keySet(), newMap.keySet());
        Map<String, Object> updateMap;
        if (parentKey == null) {
            extracted(oldMap, newMap, update, resultMap);
        } else {
            updateMap = (Map<String, Object>) resultMap.get(parentKey);
            extracted(oldMap, newMap, update, updateMap);

        }
    }

    private void extracted(Map<String, Object> oldMap, Map<String, Object> newMap, Set<String> update, Map<String, Object> updateMap) {
        for (String key : update) {
            Object oldValue = oldMap.get(key);
            Object newValue = newMap.get(key);
            if (Objects.equals(oldValue, newValue)) {
                continue;
            }
            if (oldValue.getClass() == newValue.getClass()) {
                if (newValue instanceof Map) {
                    updateMap.put(key, new LinkedHashMap<>());
                    deal((Map<String, Object>) oldValue, (Map<String, Object>) newValue, key, updateMap);
                    if (((Map<String, Object>) updateMap.get(key)).isEmpty()) {
                        updateMap.remove(key);
                    }
                } else if (newValue instanceof List) {
                    if (!Objects.equals(oldValue, newValue)) {
                        updateMap.put(DELETE_TAG + key, oldValue);
                        updateMap.put(ADD_TAG + key, newValue);
                    }
                } else {
                    if (!Objects.equals(oldValue, newValue)) {
                        updateMap.put(key, oldValue + UPDATE_TAG + newValue);
                    }
                }
            } else {
                updateMap.put(DELETE_TAG + key, oldValue);
                updateMap.put(ADD_TAG + key, newValue);
            }
        }
    }

    private void dd() {

    }

    private Map<String, Object> getDelete(Map<String, Object> oldMap, Map<String, Object> newMap) {
        Set<String> delete = getDeleteKeys(oldMap.keySet(), newMap.keySet());
        return getResultMap(delete, oldMap, DELETE_TAG);
    }

    private Map<String, Object> getAdd(Map<String, Object> oldMap, Map<String, Object> newMap) {
        Set<String> add = getAddKeys(oldMap.keySet(), newMap.keySet());
        return getResultMap(add, newMap, ADD_TAG);
    }

    private Map<String, Object> getResultMap(Set<String> keys, Map<String, Object> map, String tag) {
        Map<String, Object> res = new LinkedHashMap<>();
        for (String key : keys) {
            res.put(tag + key, map.get(key));
        }
        return res;
    }

    private void aaa(Map<String, Object> map1, int level, String parentKey) {
        MapNode mapNode = new MapNode();
        Set<String> keys = new HashSet<>();
        Set<String> strings = map1.keySet();
        Collection<Object> values = map1.values();

        for (Map.Entry<String, Object> entry : map1.entrySet()) {
            String key = entry.getKey();
            mapNode.setLevel(level);
            keys.add(key);
            Object value = entry.getValue();
        }
    }


    static class MapNode<K, V> {
        private int level;
        private String parentKey;
        private Set<String> keys;
        Collection<Object> values;

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getParentKey() {
            return parentKey;
        }

        public void setParentKey(String parentKey) {
            this.parentKey = parentKey;
        }

        public Set<String> getKeys() {
            return keys;
        }

        public void setKeys(Set<String> keys) {
            this.keys = keys;
        }

        public Collection<Object> getValues() {
            return values;
        }

        public void setValues(Collection<Object> values) {
            this.values = values;
        }
    }


    private Set<String> getAddKeys(Set<String> source, Set<String> target) {
        if (source == null) {
            return target;
        }
        Set<String> t = new HashSet<>(target);
        t.removeAll(source);
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

    private Set<String> getUpdateKeys(Set<String> source, Set<String> target) {
        if (source == null) {
            return target;
        }
        Set<String> t = new HashSet<>(target);
        t.retainAll(source);
        return t;
    }

}