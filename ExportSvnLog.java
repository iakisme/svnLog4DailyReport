/*
 * @(#)ExportSvnLog.java    Created on 2016年11月18日
 * Copyright (c) 2016 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package supportPackage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * @author wangk
 * @version $Revision: 1.0 $, $Date: 2016年11月18日 下午4:52:10 $
 */
public class ExportSvnLog {

    private static SVNRepository repository = null;

    public void setupLibrary(String url, String userName, String password) {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
        }
        catch (SVNException e) {
        }
        // 身份验证
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, password);
        repository.setAuthenticationManager(authManager);
    }

    public void filterCommitHistoryTest(String url, final String author, String localUrl) throws Exception {
        // 过滤条件
        // 时间
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // final Date begin = format.parse("2016-10-21");
        // final Date end = format.parse("2016-11-18");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        final Date begin = calendar.getTime();
        final Date end = new Date();
        // 版本
        long startRevision = 0;
        long endRevision = -1;// 表示最后一个版本

        final List<String> history = new ArrayList<String>();
        // String[] 为过滤的文件路径前缀，为空表示不进行过滤

        repository.log(new String[] { "" }, startRevision, endRevision, true, true, new ISVNLogEntryHandler() {
            @Override
            public void handleLogEntry(SVNLogEntry svnlogentry) throws SVNException {
                // 依据提交时间进行过滤
                if (svnlogentry.getDate().after(begin) && svnlogentry.getDate().before(end)) {
                    // 依据提交人过滤
                    if (!"".equals(author)) {
                        if (author.equals(svnlogentry.getAuthor())) {
                            fillResult(svnlogentry);
                        }
                    }
                    else {
                        fillResult(svnlogentry);
                    }
                }
            }

            public void fillResult(SVNLogEntry svnlogentry) {
                // getChangedPaths为提交的历史记录MAP key为文件名，value为文件详情
                // history.addAll(svnlogentry.getChangedPaths().keySet());
                history.add(svnlogentry.getMessage());
            }
        });
        File file = new File(localUrl + format.format(end));
        createFile4Log(file, url, history);
    }

    private void createFile4Log(File file, String url, List<String> history) throws IOException {
        if (!file.getParentFile().exists() || !file.getParentFile().isDirectory()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        BufferedOutputStream out = null;
        out = new BufferedOutputStream(new FileOutputStream(file));
        String dataString = "";
        String projectName = url.contains("etoh_nx") ? "宁夏" : url.contains("etoh_sc") ? "四川"
                : url.contains("etoh_xj") ? "新疆" : url.contains("etoh_zjcu") ? "浙江联通" : "test";
        dataString = "1、" + projectName + "校讯通项目开发\n";
        int i = 1;
        for (String log : history) {
            dataString += String.valueOf(i++) + "）" + log.replace("\n", ",") + "（% | h）" + "\n";
        }
        out.write(dataString.getBytes("utf-8"));
        out.flush();
        IOUtils.closeQuietly(out);

    }
}
