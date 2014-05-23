package org.dragons.itsmeim.model;

import java.util.concurrent.ScheduledFuture;

/**
 * Zdarzenie zaplanowane w timerze.
 */
public class ScheduleTask
{
	private Runnable task;
	private ScheduledFuture<?> scheduled;

	public ScheduleTask(final Runnable task, final ScheduledFuture<?> scheduled)
	{
		super();
		this.task = task;
		this.scheduled = scheduled;
	}

	public ScheduleTask(final Runnable task)
	{
		super();
		this.task = task;
	}

	public Runnable getTask()
	{
		return task;
	}

	public void setTask(final Runnable task)
	{
		this.task = task;
	}

	public ScheduledFuture<?> getScheduled()
	{
		return scheduled;
	}

	public void setScheduled(final ScheduledFuture<?> scheduled)
	{
		this.scheduled = scheduled;
	}

	/** Anuluje zaplanowane zadarzenie. */
	public void cancel()
	{
		if (scheduled != null)
		{
			scheduled.cancel(false);
			scheduled = null;
		}
	}
}
