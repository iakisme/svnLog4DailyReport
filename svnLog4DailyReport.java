/*
 * @(#)svnLog4DailyReport.java    Created on 2016年11月18日
 * Copyright (c) 2016 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package tools;

import supportPackage.ExportSvnLog;

/**
 * @author wangk
 * @version $Revision: 1.0 $, $Date: 2016年11月18日 下午4:47:13 $
 */
public class svnLog4DailyReport {
    // http://192.168.0.2/svn/repos/demo-keel/branches/keeldemo_wangk
    // http://192.168.0.2/svn/repos/etoh2/branches/etoh_nx_base2.9.0.0
    private static String url = "http://192.168.0.2/svn/repos/etoh2/branches/etoh_nx_base2.9.0.0";
    private static String userName = "wangkai1";
    private static String password = "654321";
    private static String localUrl = "F:/工作日报/";
    private static final String author = "wangkai1";

    public static void main(String[] args) throws Exception {
        System.out.println("工作日报正在生成，请稍后...");
        ExportSvnLog exportSvnLog = new ExportSvnLog();
        // 连接
        exportSvnLog.setupLibrary(url, userName, password);
        System.out.println("地址和身份验证成功...");
        // 查找Log
        exportSvnLog.filterCommitHistoryTest(url, author, localUrl);
        System.out.println("完成");
    }
}
