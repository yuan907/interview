package org.example.controller;

import org.example.service.YamlDiffService;
import org.example.vo.config.ConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * @Author: yuanchao
 * @DATE: 2023/4/23 10:18
 */
@RestController
public class YamlDiffController {


   @Autowired
   private YamlDiffService yamlDiffService;

   @RequestMapping("/test")
   public ConfigVO test() throws IOException {
      return yamlDiffService.test();
   }

   @RequestMapping("/test2")
   public void test2() throws FileNotFoundException {
      yamlDiffService.test2();
   }
}
