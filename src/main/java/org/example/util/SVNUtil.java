package org.example.util;

import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.*;

import java.io.File;

/**
 * @Author: yuanchao
 * @DATE: 2023/4/18 14:20
 */
@Slf4j
@Component
public class SVNUtil {

    private static final Logger logger = LoggerFactory.getLogger(SVNUtil.class);


    /**
     * 通过不同的协议初始化版本库
     */
    public static void setupLibrary() {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
    }

    /**
     * 验证登录svn
     * @Param: svnRoot:svn的根路径
     */
    public static SVNClientManager authSvn(String svnRoot, String username, String password) {
        // 初始化版本库
        setupLibrary();

        // 创建库连接
        SVNRepository repository = null;
        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnRoot));
        } catch (SVNException e) {
            logger.error("",e);
            return null;
        }

        // 身份验证
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);

        // 创建身份验证管理器
        repository.setAuthenticationManager(authManager);

        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager clientManager = SVNClientManager.newInstance(options, authManager);
        return clientManager;
    }

    /**
     * 创建文件夹
     */
    public static SVNCommitInfo makeDirectory(SVNClientManager clientManager, SVNURL url, String commitMessage) {
        try {
            return clientManager.getCommitClient().doMkDir(new SVNURL[]{url}, commitMessage);
        } catch (SVNException e) {
            logger.error("",e);
        }
        return null;
    }

    /**
     * 导入文件夹
     * @Param localPath:本地路径
     * @Param dstURL:目标地址
     */
    public static SVNCommitInfo importDirectory(SVNClientManager clientManager, File localPath, SVNURL dstURL, String commitMessage, boolean isRecursive) {
        try {
            return clientManager.getCommitClient().doImport(localPath, dstURL, commitMessage, null, true, true, SVNDepth.fromRecurse(isRecursive));
        } catch (SVNException e) {
            logger.error("",e);
        }
        return null;
    }

    /**
     * 添加入口
     */
    public static void addEntry(SVNClientManager clientManager, File wcPath) {
        try {
            clientManager.getWCClient().doAdd(new File[]{wcPath}, true, false, false, SVNDepth.INFINITY, false, false, true);
        } catch (SVNException e) {
            logger.error("",e);
        }
    }

    /**
     * 显示状态
     */
    public static SVNStatus showStatus(SVNClientManager clientManager, File wcPath, boolean remote) {
        SVNStatus status = null;
        try {
            status = clientManager.getStatusClient().doStatus(wcPath, remote);
        } catch (SVNException e) {
            logger.error("",e);
        }
        return status;
    }

    /**
     * 提交
     * @Param keepLocks:是否保持锁定
     */
    public static SVNCommitInfo commit(SVNClientManager clientManager, File wcPath, boolean keepLocks, String commitMessage) {
        try {
            return clientManager.getCommitClient().doCommit(new File[]{wcPath}, keepLocks, commitMessage, null, null, false, false, SVNDepth.INFINITY);
        } catch (SVNException e) {
            logger.error("",e);
        }
        return null;
    }

    /**
     * 更新
     */
    public static long update(SVNClientManager clientManager, File wcPath, SVNRevision updateToRevision, SVNDepth depth) {
        SVNUpdateClient updateClient = clientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);
        try {
            return updateClient.doUpdate(wcPath, updateToRevision, depth, false, false);
        } catch (SVNException e) {
            logger.error("",e);
        }
        return 0;
    }

    /**
     * 从SVN导出项目到本地
     * @Param url:SVN的url
     * @Param revision:版本
     * @Param destPath:目标路径
     */
    public static long checkout(SVNClientManager clientManager, SVNURL url, SVNRevision revision, File destPath, SVNDepth depth) {
        SVNUpdateClient updateClient = clientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);
        try {
            return updateClient.doCheckout(url, destPath, revision, revision, depth, false);
        } catch(SVNException e) {
            logger.error("",e);
        }
        return 0;
    }

    /**
     * 确定path是否是一个工作空间
     */
    public static boolean isWorkingCopy(File path) {
        if(!path.exists()) {
            logger.warn("'" + path + "' not exist!");
            return false;
        }
        try {
            if(null == SVNWCUtil.getWorkingCopyRoot(path, false)) {
                return false;
            }
        } catch (SVNException e) {
            logger.error("",e);
        }
        return true;
    }

    /**
     * 确定一个URL在SVN上是否存在
     */
    public static boolean isURLExist(SVNURL url, String username, String password) {
        try {
            SVNRepository svnRepository = SVNRepositoryFactory.create(url);
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
            svnRepository.setAuthenticationManager(authManager);
            SVNNodeKind nodeKind = svnRepository.checkPath("", -1); //遍历SVN,获取结点。
            return nodeKind == SVNNodeKind.NONE ? false : true;
        } catch (SVNException e) {
            logger.error("",e);
        }
        return false;
    }

}
