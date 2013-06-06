package com.zhan_dui.download;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloadThreadPool extends ThreadPoolExecutor {

	private ConcurrentHashMap<Future<?>, Runnable> mRunnable_Monitor_HashMap = new ConcurrentHashMap<Future<?>, Runnable>();
	private ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Future<?>>> mMissions_Monitor = new ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Future<?>>>();

	public DownloadThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				handler);
	}

	public DownloadThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	public DownloadThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory);
	}

	public DownloadThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory, handler);
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		if (t == null) {
			System.out.println(Thread.currentThread().getId()
					+ " has been succeesfully finished!");
		} else {
			System.out.println(Thread.currentThread().getId()
					+ " errroed! Retry");
		}
		for (Future<?> future : mRunnable_Monitor_HashMap.keySet()) {
			if (future.isDone() == false) {
				DownloadRunnable runnable = (DownloadRunnable) mRunnable_Monitor_HashMap
						.get(future);
				DownloadRunnable newRunnable = runnable.split();
				if (newRunnable != null) {
					submit(newRunnable);
					break;
				}
			}
		}
	}

	@Override
	public Future<?> submit(Runnable task) {
		Future<?> future = super.submit(task);
		if (task instanceof DownloadRunnable) {
			DownloadRunnable runnable = (DownloadRunnable) task;

			if (mMissions_Monitor.containsKey(runnable.MISSION_ID)) {
				mMissions_Monitor.get(runnable.MISSION_ID).add(future);
			} else {
				ConcurrentLinkedQueue<Future<?>> queue = new ConcurrentLinkedQueue<Future<?>>();
				queue.add(future);
				mMissions_Monitor.put(runnable.MISSION_ID, queue);
			}

			mRunnable_Monitor_HashMap.put(future, task);

		} else {
			throw new RuntimeException(
					"runnable is not an instance of DownloadRunnable!");
		}
		return future;
	}

	public boolean isFinished(int mission_id) {
		ConcurrentLinkedQueue<Future<?>> futures = mMissions_Monitor
				.get(mission_id);
		if (futures == null)
			return true;

		for (Future<?> future : futures) {
			if (future.isDone() == false) {
				return false;
			}
		}
		return true;
	}

	public void pause(int mission_id) {
		ConcurrentLinkedQueue<Future<?>> futures = mMissions_Monitor
				.get(mission_id);
		for (Future<?> future : futures) {
			future.cancel(true);
		}
	}

	public void cancel(int mission_id) {
		ConcurrentLinkedQueue<Future<?>> futures = mMissions_Monitor
				.remove(mission_id);
		for (Future<?> future : futures) {
			mRunnable_Monitor_HashMap.remove(future);
			future.cancel(true);
		}
	}
}
