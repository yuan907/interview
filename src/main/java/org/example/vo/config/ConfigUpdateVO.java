package org.example.vo.config;

import java.util.List;
import java.util.Map;

/**
 * @Author: yuanchao
 * @DATE: 2023/4/23 11:21
 */
public class ConfigUpdateVO {

    //配置文件名称
    private String configFileName;

    private Map<String, Object> add;

    private List<String> del;
    private Map<String, Object> update;


    public String getConfigFileName() {
        return configFileName;
    }

    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }

    public Map<String, Object> getAdd() {
        return add;
    }

    public void setAdd(Map<String, Object> add) {
        this.add = add;
    }

    public List<String> getDel() {
        return del;
    }

    public void setDel(List<String> del) {
        this.del = del;
    }

    public Map<String, Object> getUpdate() {
        return update;
    }

    public void setUpdate(Map<String, Object> update) {
        this.update = update;
    }
}
