package com.zhan_dui.download;

import java.text.DecimalFormat;

public class DownloadUtils {

	public static String getReadableSize(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
				+ (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public static String getReadableSize(long bytes) {
		if (bytes <= 0)
			return "0";
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(bytes
				/ Math.pow(1024, digitGroups))
				+ " " + units[digitGroups];
	}

	public static String getReadableSpeed(long speed) {
		return getReadableSize(speed) + "/S";
	}
}
