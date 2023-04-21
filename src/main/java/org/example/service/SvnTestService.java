package org.example.service;

import org.springframework.web.multipart.MultipartFile;
import org.tmatesoft.svn.core.SVNException;

import java.io.IOException;

/**
 * @Author: yuanchao
 * @DATE: 2023/4/20 15:09
 */
public interface SvnTestService {

    void testSvnUpload(MultipartFile file) throws SVNException;
}
