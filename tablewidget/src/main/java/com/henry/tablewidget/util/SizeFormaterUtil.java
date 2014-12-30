package com.henry.tablewidget.util;

import java.text.DecimalFormat;

/**
 * 计算流量时有用到
 */
public class SizeFormaterUtil {
	public static String getDataSize(long size) {
		if (size < 0) {
			size = 0;
		}
		DecimalFormat formater = new DecimalFormat("####.00");
		if (size < 1024) {
			return size + "字节";
		} else if (size < 1024 * 1024) {
			float kbsize = size / 1024f;
			return formater.format(kbsize) + "KB";
		} else if (size < 1024 * 1024 * 1024) {
			float mbsize = size / 1024f / 1024f;
			return formater.format(mbsize) + "MB";
		} else if (size < 1024 * 1024 * 1024 * 1024) {
			float gbsize = size / 1024f / 1024f / 1024f;
			return formater.format(gbsize) + "GB";
		} else {
			return "size: error";
		}
	}
}
