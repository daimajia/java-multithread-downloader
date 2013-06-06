package com.zhan_dui.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.zhan_dui.download.DownloadMission.MissionMonitor;

@XmlRootElement(name = "Downloading")
@XmlAccessorType(XmlAccessType.NONE)
public class DownloadRunnable implements Runnable {

	private static final int BUFFER_SIZE = 1024;

	private static int counter = 0;
	private String mFileUrl;
	private String mSaveDirectory;
	private String mSaveFileName;
	@XmlElement(name = "StartPosition")
	private int mStartPosition;
	@XmlElement(name = "EndPosition")
	private int mEndPosition;
	public final int MISSION_ID;
	public final int ID = counter++;

	@XmlElement(name = "CurrentPosition")
	private int mCurrentPosition;

	private MissionMonitor mDownloadMonitor;

	private DownloadRunnable() {
		// just use for annotation
		// -1 is meanningless
		MISSION_ID = -1;
	}

	public DownloadRunnable(MissionMonitor monitor, String mFileUrl,
			String mSaveDirectory, String mSaveFileName, int mStartPosition,
			int mEndPosition) {
		super();
		this.mFileUrl = mFileUrl;
		this.mSaveDirectory = mSaveDirectory;
		this.mSaveFileName = mSaveFileName;
		this.mStartPosition = mStartPosition;
		this.mEndPosition = mEndPosition;
		this.mDownloadMonitor = monitor;
		this.mCurrentPosition = this.mStartPosition;
		MISSION_ID = monitor.mHostMission.mMissionID;
	}

	public DownloadRunnable(MissionMonitor monitor, String mFileUrl,
			String mSaveDirectory, String mSaveFileName, int mStartPosition,
			int mCurrentPosition, int mEndPosition) {
		this(monitor, mFileUrl, mSaveDirectory, mSaveFileName, mStartPosition,
				mEndPosition);
		this.mCurrentPosition = mCurrentPosition;
	}

	@Override
	public void run() {
		File targetFile;
		synchronized (this) {
			File dir = new File(mSaveDirectory + File.pathSeparator);
			if (dir.exists() == false) {
				dir.mkdirs();
			}
			targetFile = new File(mSaveDirectory + File.separator
					+ mSaveFileName);
			if (targetFile.exists() == false) {
				try {
					targetFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Download Task ID:" + Thread.currentThread().getId()
				+ " has been started! Range From " + mCurrentPosition + " To "
				+ mEndPosition);
		BufferedInputStream bufferedInputStream = null;
		RandomAccessFile randomAccessFile = null;
		byte[] buf = new byte[BUFFER_SIZE];
		URLConnection urlConnection = null;
		try {
			URL url = new URL(mFileUrl);
			urlConnection = url.openConnection();
			urlConnection.setRequestProperty("Range", "bytes="
					+ mCurrentPosition + "-" + mEndPosition);
			randomAccessFile = new RandomAccessFile(targetFile, "rw");
			randomAccessFile.seek(mCurrentPosition);
			bufferedInputStream = new BufferedInputStream(
					urlConnection.getInputStream());
			while (mCurrentPosition < mEndPosition) {
				if (Thread.currentThread().isInterrupted()) {
					System.out.println("Download TaskID:"
							+ Thread.currentThread().getId()
							+ " was interrupted, Start:" + mStartPosition
							+ " Current:" + mCurrentPosition + " End:"
							+ mEndPosition);
					break;
				}
				int len = bufferedInputStream.read(buf, 0, BUFFER_SIZE);
				if (len == -1)
					break;
				else {
					randomAccessFile.write(buf, 0, len);
					mCurrentPosition += len;
					mDownloadMonitor.down(len);
				}
			}
			bufferedInputStream.close();
			randomAccessFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public DownloadRunnable split() {
		int end = mEndPosition;
		int remaining = mEndPosition - mCurrentPosition;
		int remainingCenter = remaining / 2;
		System.out.print("CurrentPosition:" + mCurrentPosition
				+ " EndPosition:" + mEndPosition + "Rmaining:" + remaining
				+ " ");
		if (remainingCenter > 1048576) {
			int centerPosition = remainingCenter + mCurrentPosition;
			System.out.print(" Center position:" + centerPosition);
			mEndPosition = centerPosition;

			DownloadRunnable newSplitedRunnable = new DownloadRunnable(
					mDownloadMonitor, mFileUrl, mSaveDirectory, mSaveFileName,
					centerPosition + 1, end);
			mDownloadMonitor.mHostMission.addPartedMission(newSplitedRunnable);
			return newSplitedRunnable;
		} else {
			System.out
					.println(toString() + " can not be splited ,less than 1M");
			return null;
		}
	}

	public boolean isFinished() {
		return mCurrentPosition >= mEndPosition;
	}

	public int getCurrentPosition() {
		return mCurrentPosition;
	}

	public int getEndPosition() {
		return mEndPosition;
	}

	public int getStartPosition() {
		return mStartPosition;
	}

}
