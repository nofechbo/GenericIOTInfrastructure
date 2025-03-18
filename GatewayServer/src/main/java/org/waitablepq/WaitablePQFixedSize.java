package org.waitablepq;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;


public class WaitablePQFixedSize<E> {
    private final PriorityQueue<E> pq;;
    private final Semaphore semRead = new Semaphore(0);
    private final Semaphore semWrite;

    public WaitablePQFixedSize(int size) {
        pq = new PriorityQueue<>();
        semWrite = new Semaphore(size);
    }
    public WaitablePQFixedSize(int size, Comparator<? super E> cmp) {
        pq = new PriorityQueue<>(cmp);
        semWrite = new Semaphore(size);
    }

    public void enqueue(E element) throws InterruptedException {
        semWrite.acquire();
        synchronized (pq) {
            pq.add(element);
        }
        semRead.release();
    }

    public E dequeue() throws InterruptedException {
        semRead.acquire();
        synchronized (pq) {
            semWrite.release();
            return pq.poll();
        }
    }

    public boolean remove(Object o)  {
        boolean removed = false;

        boolean semIsAcquired = semRead.tryAcquire();
        if (semIsAcquired) {
            synchronized (pq) {
                removed = pq.remove(o);
            }
        }

        if (removed) {
            semWrite.release();
        }
        else if (semIsAcquired) {
            semRead.release();
        }

        return removed;
    }
}
