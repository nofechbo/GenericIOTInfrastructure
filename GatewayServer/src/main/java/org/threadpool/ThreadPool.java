package org.threadpool;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.waitablepq.WaitablePQFixedSize;

public class ThreadPool implements Executor {
    private int numThreads;
    private final WaitablePQFixedSize<Task<?>> pq;
    private final Semaphore semResume = new Semaphore(0);
    private boolean isPaused = false;
    private boolean isShutdown = false;
    private final int highestPriority = Priority.HIGH.ordinal() + 1;
    private final int lowestPriority = Priority.LOW.ordinal() - 1;
    private final Semaphore aliveThreads = new Semaphore(0);

    private final int GROWTH_FACTOR = 2;
    private final int DEFAULT_SIZE = 32;

    public ThreadPool(){
        this((Runtime.getRuntime().availableProcessors())+1);
    }
    public ThreadPool(int numThreads){
        if (numThreads == -1) {
            throw new IllegalArgumentException();
        }
        
        this.numThreads = numThreads;
        int size = Math.max(numThreads * GROWTH_FACTOR, DEFAULT_SIZE);
        pq = new WaitablePQFixedSize<>(size);

        for (int i = 0; i < this.numThreads; ++i) {
            Thread t = new WorkingThread();
            t.start();
        }
    }

    public <V> Future<V> submit(Callable<V> command, Priority priority) {
        if (isShutdown) {
            throw new RejectedExecutionException();
        }
        Task<V> task = new Task<>(priority.ordinal(), command, pq);
        try {
            pq.enqueue(task);
        } catch (InterruptedException ignore) {
        }
        return task.getFuture();
    }
    public <V> Future<V> submit(Callable<V> command) {
        return this.submit(command, Priority.DEFAULT);
    }
    public <V> Future<V> submit(Runnable command, Priority priority, V outParam){
        Callable<V> callableCommand = Executors.callable(command, outParam);
        return this.submit(callableCommand, priority);
    }
    public Future<Void> submit(Runnable command, Priority priority){
        return this.submit(command, priority, null);
    }
    @Override
    public void execute(Runnable command) {
        this.submit(command, Priority.DEFAULT);
    }

    public void setNumOfThreads(int numThreads) {
        int diff = this.numThreads - numThreads;
        if (0 == diff) {
            return;
        }
        if (0 < diff) {
            for (int i = 0; i < diff; ++i) {
                Thread t = new WorkingThread();
                if (isPaused) {
                    insertSleepingPill();
                }
                t.start();
            }
        }
        else {
            diff = Math.abs(diff);
            for (int i = 0; i < diff; ++i) {
                insertPoisonApple(highestPriority);
                if (isPaused){
                    semResume.release();
                }
            }
        }
        this.numThreads = numThreads;
    }

    public int getNumOfThreads(){
        return numThreads;
    }

    public void resumeTP(){
        semResume.release(numThreads);
        isPaused = false;
    }
    public void pauseTP(){
        for (int i = 0; i < numThreads; ++i) {
            insertSleepingPill();
        }
        isPaused = true;
    }
    private void insertSleepingPill() {
        try {
            pq.enqueue(new Task<>(highestPriority, () -> {
                semResume.acquireUninterruptibly();
                return null;}, pq));
        } catch(InterruptedException ignore){}
    }

    //Initiates an orderly shutdown in which previously
    // submitted tasks are executed, but no new tasks will be accepted.
    public void shutDown(){
        if (isShutdown) {
            return;
        }

        isShutdown = true;
        for (int i = 0; i < numThreads; ++i) {
            insertPoisonApple(lowestPriority);
        }
    }

    //Blocks until all tasks have completed execution after a shutdown request,
    // or the timeout occurs, or the current thread is interrupted, whichever
    // happens first.
    public boolean awaitTermination() throws InterruptedException {
        aliveThreads.acquire(numThreads);
        return true;
    }
    public boolean awaitTermination(long timeOut, TimeUnit timeUnit) throws InterruptedException{
        return aliveThreads.tryAcquire(numThreads, timeOut, timeUnit);
    }

    private void insertPoisonApple(int priority) {
        Task<Void> task = new Task<>(priority, () -> {
            ((WorkingThread) (Thread.currentThread())).setRunning(false);
            if (isShutdown) {
                aliveThreads.release();
            }
            return null;}, pq);
        try {
            pq.enqueue(task);
        } catch (InterruptedException ignore) {}
    }

    private static class Task<V> implements Comparable<Task<V>>{
        public V returnVal;
        private Throwable exception;
        private final Future<V> future;
        private final int priority;
        private final Callable<V> method;
        private final WaitablePQFixedSize<Task<?>> pq;
        private final Lock lock = new ReentrantLock();
        private final Condition cond = lock.newCondition();
        private boolean isCancelled = false;
        private volatile boolean isDone = false;

        private void setIsCancelled(boolean val) {
            isCancelled = val;
        }
        private void setTaskAsDone() {
            isDone = true;
            lock.lock();
            cond.signal();
            lock.unlock();
        }

        private Task(int priority, Callable<V> method, WaitablePQFixedSize<Task<?>> pq) {
            future = new TFuture();
            this.priority = priority;
            this.method = method;
            this.pq = pq;
        }

        private void setReturnVal(V val) {
            returnVal = val;
        }
        public void setException(Throwable e) {
            exception = e;
        }
        private Future<V> getFuture() {
            return future;
        }
        private int getPriority() {
            return priority;
        }
        private Future<V> run() {
            try {
               setReturnVal(method.call());
            } catch (Exception e) {
                setException(e);
            } finally {
                setTaskAsDone();
            }
            return future;
        }

        @Override
        public int compareTo(Task<V> task) {
            return task.getPriority() - getPriority();
        }


        private class TFuture implements Future<V>{
            @Override
            public boolean cancel(boolean b) {
                if (isDone() || !pq.remove(Task.this)) {
                    return false;
                }
                Task.this.setIsCancelled(true);
                return true;
            }
            @Override
            public boolean isCancelled() {
                return Task.this.isCancelled;
            }
            @Override
            public boolean isDone() {
                return Task.this.isDone;
            }

            @Override
            public V get() throws InterruptedException, ExecutionException {
                try {
                    return get(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                } catch (TimeoutException e) {
                    throw new ExecutionException(e);
                }
            }
            @Override
            public V get(long time, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                long timeout = timeUnit.toNanos(time);

                lock.lock();
                try {
                    while (!this.isDone() && !isCancelled && timeout > 0) {
                        timeout = cond.awaitNanos(timeout);
                    }
                } finally {
                    lock.unlock();
                }

                if (isCancelled) {
                    throw new CancellationException();
                }

                if (timeout <= 0) {
                    throw new TimeoutException();
                }
                if (exception != null) {
                    throw new ExecutionException(exception);
                }

                return Task.this.returnVal;
            }
        }
    }

    private class WorkingThread extends Thread {
        private boolean isRunning = true;

        public void setRunning(boolean val) {
            isRunning = val;
        }
        @Override
        public void run(){
            while(isRunning) {
                try {
                    Task<?> task = pq.dequeue();
                    task.run();
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

}



