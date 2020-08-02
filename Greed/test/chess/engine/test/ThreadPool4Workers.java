/**********************************************
 * Greed, a chess engine written in java.
 * Copyright (C) 2019 Ahmet Baþar
 * 
 * This file is part of Greed.
 * 
 * Greed is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Greed is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Greed.  If not, see <https://www.gnu.org/licenses/>.
 **********************************************/
package chess.engine.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool4Workers {

	private static volatile ThreadPool4Workers instance;
//	public static final int POOL_SIZE = (Runtime.getRuntime().availableProcessors() / 2);
	public static final int POOL_SIZE = (Runtime.getRuntime().availableProcessors() / 4);
	private ExecutorService executorService = Executors.newFixedThreadPool(POOL_SIZE);

	public static ThreadPool4Workers getInstance() {
		if (instance == null) {
			synchronized (ThreadPool4Workers.class) {
				if (instance == null) {
					instance = new ThreadPool4Workers();
				}
			}
		}
		return instance;
	}

	private ThreadPool4Workers() {
	}

	public void execute(Runnable runnable, boolean sync) {
		if (sync) {
			try {
				executorService.submit(runnable).get();
			} catch (InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
			}
		} else {
			executorService.submit(runnable);
		}
	}

	public void execute(List<Runnable> runnables, boolean sync) {
		if (sync) {
			List<Callable<Object>> callables = convertToCallable(runnables);
			try {
				executorService.invokeAll(callables);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		} else {
			for (Runnable runnable : runnables) {
				executorService.submit(runnable);
			}
		}
	}

	private List<Callable<Object>> convertToCallable(List<Runnable> runnables) {
		List<Callable<Object>> callables = new ArrayList<Callable<Object>>();
		for (Runnable r : runnables) {
			callables.add(Executors.callable(r));
		}
		return callables;
	}
	
	public void shutDown() {
		executorService.shutdown();
	}

}
