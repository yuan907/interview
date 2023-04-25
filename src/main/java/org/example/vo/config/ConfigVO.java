package org.example.vo.config;

import org.example.vo.config.ConfigAddVO;
import org.example.vo.config.ConfigDelVO;
import org.example.vo.config.ConfigUpdateVO;

import java.util.List;

/**
 * @Author: yuanchao
 * @DATE: 2023/4/23 13:12
 */
public class ConfigVO {

    List<ConfigUpdateVO> updateList;

    List<ConfigAddVO> addList;

    List<ConfigDelVO> delList;


    public List<ConfigUpdateVO> getUpdateList() {
        return updateList;
    }

    public void setUpdateList(List<ConfigUpdateVO> updateList) {
        this.updateList = updateList;
    }

    public List<ConfigAddVO> getAddList() {
        return addList;
    }

    public void setAddList(List<ConfigAddVO> addList) {
        this.addList = addList;
    }

    public List<ConfigDelVO> getDelList() {
        return delList;
    }

    public void setDelList(List<ConfigDelVO> delList) {
        this.delList = delList;
    }
}
