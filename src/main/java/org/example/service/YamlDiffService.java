package org.example.service;

import org.example.vo.config.ConfigVO;

import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * @Author: yuanchao
 * @DATE: 2023/4/23 10:19
 */
public interface YamlDiffService {

    ConfigVO test() throws IOException;

    void test2()throws FileNotFoundException;
}
