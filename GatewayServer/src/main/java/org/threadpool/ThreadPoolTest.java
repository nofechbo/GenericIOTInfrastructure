package org.threadpool;

/*import org.junit.jupiter.api.*;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

public class ThreadPoolTest {

    private ThreadPool threadPool;

    @BeforeEach
    void setUp() {
        threadPool = new ThreadPool(2); // Initialize with 9 threads
    }

    @AfterEach
    void tearDown() {
        threadPool.shutDown();
    }

    @Test
    void testSubmitRunnableWithPriority() throws ExecutionException, InterruptedException {
        Runnable task = () -> System.out.println("Task executed");
        Future<Void> future = threadPool.submit(task, Priority.HIGH, null);

        assertNotNull(future);
        sleep(1000);
        System.out.println(future.get()); // Wait for the task to complete
    }

    @Test
    void testSubmitCallableWithPriority() throws ExecutionException, InterruptedException {
        Callable<String> task = () -> "Task Result";
        Future<String> future = threadPool.submit(task, Priority.DEFAULT);

        assertNotNull(future);
        assertEquals("Task Result", future.get());
    }

    @Test
    void testTaskPrioritization() throws ExecutionException, InterruptedException {
        StringBuilder result = new StringBuilder();

        Runnable highPriorityTask = () -> result.append("HIGH");
        Runnable lowPriorityTask = () -> result.append("LOW");
        threadPool.setNumOfThreads(2);

        threadPool.pauseTP();

        threadPool.submit(lowPriorityTask, Priority.LOW, null);
        threadPool.submit(highPriorityTask, Priority.HIGH, null);
        threadPool.submit(lowPriorityTask, Priority.LOW, null);
        threadPool.submit(highPriorityTask, Priority.HIGH, null);
        threadPool.submit(lowPriorityTask, Priority.LOW, null);
        threadPool.submit(highPriorityTask, Priority.HIGH, null);
        threadPool.submit(lowPriorityTask, Priority.LOW, null);
        threadPool.submit(highPriorityTask, Priority.HIGH, null);
        Future<Void> f = threadPool.submit(lowPriorityTask, Priority.LOW, null);

        threadPool.resumeTP();

        //Thread.sleep(500); // Allow tasks to complete
        f.get();
        System.out.println(result);
        // Verify that the high-priority task ran first
        assertTrue(result.toString().startsWith("HIGH"));
    }

    @Test
    void testMultiThreadedExecution() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            threadPool.submit(() -> {
                System.out.println("Task running by: " + Thread.currentThread().getName());
                latch.countDown();
            }, Priority.DEFAULT, null);
        }

        // Wait for all tasks to finish
        latch.await();
        assertEquals(0, latch.getCount());
    }

    @Test
    void testPauseAndResume() throws InterruptedException {
        threadPool.pauseTP();

        CountDownLatch latch = new CountDownLatch(1);

        threadPool.submit(() -> {
            System.out.println("This should not execute until resumed.");
            latch.countDown();
        }, Priority.DEFAULT, null);

        Thread.sleep(500); // Allow some time to verify task is paused
        assertEquals(1, latch.getCount());

        threadPool.resumeTP();
        latch.await(); // Wait for the task to complete after resuming

        sleep(1000);
    }

    @Test
    void testShutdown() throws InterruptedException {
        threadPool.shutDown();
        threadPool.submit(() -> System.out.println("Task after shutdown"), Priority.DEFAULT, null);

        assertThrows(RejectedExecutionException.class, () ->
                threadPool.submit(() -> System.out.println("Task after shutdown"), Priority.DEFAULT, null)
        );
    }


    @Test
    void testAwaitTermination() throws InterruptedException {
        threadPool.submit(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, Priority.DEFAULT, null);

        threadPool.shutDown();

        assertDoesNotThrow(() -> threadPool.awaitTermination(3, TimeUnit.SECONDS)); // Wait for up to 3 seconds
    }


}
*/
/*
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;

import static org.junit.Assert.*;

public class ThreadPoolTest {
    private ThreadPool threadPool;

    @Before
    public void setUp() {
        threadPool = new ThreadPool();
    }

    @After
    public void tearDown() {
        threadPool.shutDown();
        try {
            threadPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail("Failed to terminate thread pool in time");
        }
    }

    @Test
    public void testSubmitRunnable() throws ExecutionException, InterruptedException {
        Future<Void> future = threadPool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                System.out.println("Task executed");
                return null;
            };});
        assertNull(future.get());
        assertTrue(future.isDone());
    }

    @Test
    public void testSubmitCallable() throws ExecutionException, InterruptedException {
        Future<Integer> future = threadPool.submit(() -> 42);
        assertEquals(Integer.valueOf(42), future.get());
        assertTrue(future.isDone());
    }

    @Test
    public void testSubmitWithPriority() throws ExecutionException, InterruptedException {
        Future<Integer> lowPriorityTask = threadPool.submit(() -> 1, Priority.LOW);
        Future<Integer> highPriorityTask = threadPool.submit(() -> 2, Priority.HIGH);

        assertEquals(Integer.valueOf(2), highPriorityTask.get());
        assertEquals(Integer.valueOf(1), lowPriorityTask.get());
    }

    @Test
    public void testSetThreadsNum() {
        int initialThreads = threadPool.getNumOfThreads();
        threadPool.setNumOfThreads(initialThreads + 2);
        assertEquals(initialThreads + 2, threadPool.getNumOfThreads());

        threadPool.setNumOfThreads(initialThreads);
        assertEquals(initialThreads, threadPool.getNumOfThreads());
    }

    @Test
    public void testPauseAndResumeTP() throws ExecutionException, InterruptedException {
        threadPool.pauseTP();

        Future<Void> future = threadPool.submit(() -> {
            System.out.println("Paused task executed");
            return null;
        });

        Thread.sleep(1000);// Allow time to ensure task doesn't execute during pause

        System.out.println(future.isDone());

        assertFalse(future.isDone());

        threadPool.resumeTP();
        future.get();
        assertTrue(future.isDone());
    }

    /*@Test(expected = RejectedExecutionException.class)
    public void testSubmitAfterShutdown() {
        threadPool.shutDown();
        threadPool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                System.out.println("Should not execute");
                return null;
            };});
    }

    @Test
    public void testAwaitTermination() throws InterruptedException {
        threadPool.shutDown();
        boolean terminated = threadPool.awaitTermination(5, TimeUnit.SECONDS);
        assertTrue(terminated);
    }

}*/

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

import static java.lang.Thread.sleep;

public class ThreadPoolTest {
    private ThreadPool tp;

    @Before
    public void initTP(){
        tp = new ThreadPool(10);
    }

    @Test
    public void submitTest() {
        System.out.println("SubmitTest test!");
        for(int i = 0; i < 10; ++i){
            RunTask t = new RunTask();
            t.addN(i);
            tp.submit(t, Priority.DEFAULT);
        }
    }

    @Test
    public void testTaskCancellation() throws Exception {
        Callable<Integer> longRunningTask = () -> {
            Thread.sleep(5_000);
            System.out.println("executed");
            return 17;
        };

        for (int i = 0; i < 15 ; ++i) {
            tp.submit(longRunningTask);
        }
        Future<Integer> future = tp.submit(longRunningTask, Priority.LOW);

        assertNotNull(future);
        assertFalse(future.isCancelled());
        Thread.sleep(1_000);
        boolean canceled = future.cancel(false);

        assertTrue(canceled);
        assertTrue(future.isCancelled());
    }

    @Test
    public void futureTest() throws InterruptedException, ExecutionException {
        ArrayList<Future<Integer>> futureContainer = new ArrayList<>();

        for(int i = 0; i < 20; ++i){
            RunTaskCall t = new RunTaskCall();
            t.addN(i);
            if(i == 13 || i == 16){
                tp.pauseTP();
            }

            futureContainer.add(tp.submit(t, Priority.DEFAULT));
            if(i == 13 || i == 16){
                futureContainer.get(i).cancel(true);
                //sleep(2000);
                assertTrue(futureContainer.get(i).isCancelled());
                tp.resumeTP();
            }

            if(i == 15){
                assertFalse(futureContainer.get(15).isDone());
                sleep(2000);

                assertTrue(futureContainer.get(0).isDone());
                assertTrue(futureContainer.get(5).isDone());
            }
        }
        sleep(1000);
        for(int i = 0; i < 20; ++i){
            if(i == 13 || i == 16){
                ++i;
            }
            assertEquals(i, futureContainer.get(i).get());
        }
    }

    @Test
    public void futureGetTimeoutTest2(){
        ArrayList<Future<Integer>> futureContainer = new ArrayList<>();

        for(int i = 0; i < 21; ++i){
            RunTaskCall t = new RunTaskCall();
            t.addN(i);

            if(i % 10 == 0 && i > 0){
                RunTaskCall t2 = new RunTaskCall(){
                    @Override
                    public Integer call() throws Exception {
                        sleep(15000);
                        return super.i;
                    }
                };
                t2.addN(i);
                futureContainer.add(tp.submit(t2, Priority.DEFAULT));
            }
            else {
                futureContainer.add(tp.submit(t, Priority.DEFAULT));
            }
        }

        for(int i = 0; i < 21; ++i){
            try {
                assertEquals(i, futureContainer.get(i).get(5, TimeUnit.SECONDS));
            } catch (TimeoutException | InterruptedException | ExecutionException e){
                System.out.println("Timeout in get worked");
            }
        }
    }

    @Test
    public void SetMoreThreads() throws InterruptedException {
        System.out.println("SetMoreThreads test!");
        assertEquals(10, tp.getNumOfThreads());
        for(int i = 0; i < 11; ++i){
            RunTask t = new RunTask();
            t.addN(i);
            tp.submit(t, Priority.DEFAULT);
            if(i == 7){
                tp.setNumOfThreads(12);
            }
        }
        assertEquals(12, tp.getNumOfThreads());
    }

    @Test
    public void SetLessThreads() throws InterruptedException {
        System.out.println("SetLessThreads test!");
        assertEquals(10, tp.getNumOfThreads());
        for(int i = 0; i < 11; ++i){
            RunTask t = new RunTask();
            t.addN(i);
            tp.submit(t, Priority.DEFAULT);
            if(i == 7){
                tp.setNumOfThreads(6);
            }
        }
        assertEquals(6, tp.getNumOfThreads());
    }

    @Test
    public void PauseResumeTest(){
        //Need to look for 3 elements after pause
        System.out.println("Pause Resume test!");
        for(int i = 0; i < 11; ++i){
            RunTask t = new RunTask();
            t.addN(i);
            tp.submit(t, Priority.DEFAULT);
            if(i == 7){
                tp.pauseTP();
            }
        }
        tp.resumeTP();
    }

    @Test
    public void ShutdownTest(){
        System.out.println("Shutdown test!");
        for(int i = 0; i < 11; ++i){
            RunTask t = new RunTask();
            t.addN(i);
            tp.submit(t, Priority.DEFAULT);
        }

        tp.shutDown();
        RunTask t = new RunTask();
        t.addN(11);
        try {
            tp.submit(t, Priority.DEFAULT);
        } catch (RejectedExecutionException E) {
            System.out.println("you cannot submit after shutdown!");
        }
    }

    @Test
    public void AwaitTerminationTimeoutTest() throws InterruptedException {
        System.out.println("AwaitTermination test!");
        for(int i = 0; i < 11; ++i){
            RunTask t = new RunTask();
            t.addN(i);
            tp.submit(t, Priority.DEFAULT);
        }

        RunTaskCall t2 = new RunTaskCall(){
            @Override
            public Integer call() throws Exception {
                sleep(1500000);
                return super.i;
            }
        };
        t2.addN(11);
        tp.submit(t2, Priority.LOW);
        tp.shutDown();
        try {
            assertFalse(tp.awaitTermination(1, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e){
            System.out.println("interrupted exception");
        }

        assertEquals(10, tp.getNumOfThreads());
    }

    private static class RunTask implements Runnable{
        private int i = 0;

        public void addN(int n){
            i += n;
        }

        @Override
        public void run() {
            System.out.println("Task number: " + i);
        }
    }

    private static class RunTaskCall implements Callable<Integer> {
        private int i = 0;

        public void addN(int n){
            i += n;
        }

        @Override
        public Integer call() throws Exception {
            sleep(1000);
            return i;
        }
    }
}
