package org.waitablepq;

import org.junit.jupiter.api.Test;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

    public class WPQTest {

        @Test
        public void testEnqueueAndDequeue() throws InterruptedException {
            //WaitablePQSem<Integer> pq = new WaitablePQSem<>();
            //WPQCond<Integer> pq = new WPQCond<>();
            WaitablePQFixedSize<Integer> pq = new WaitablePQFixedSize<>(3);

            // Enqueue elements
            pq.enqueue(10);
            pq.enqueue(5);
            pq.enqueue(20);

            // Dequeue elements and check order
            assertEquals(5, pq.dequeue());
            assertEquals(10, pq.dequeue());
            assertEquals(20, pq.dequeue());
        }

        @Test
        public void testDequeueBlocksUntilEnqueue() throws InterruptedException {
            //WaitablePQSem<Integer> pq = new WaitablePQSem<>();
            //WPQCond<Integer> pq = new WPQCond<>();
            WaitablePQFixedSize<Integer> pq = new WaitablePQFixedSize<>(3);

            Thread producer = new Thread(() -> {
                try {
                    Thread.sleep(1000); // Delay enqueue to simulate blocking
                    pq.enqueue(42);
                } catch (InterruptedException ignored) {
                }
            });

            producer.start();

            long startTime = System.currentTimeMillis();
            int result = pq.dequeue(); // This will block until producer enqueues
            long endTime = System.currentTimeMillis();

            assertEquals(42, result);
            assertTrue((endTime - startTime) >= 1000, "Dequeue should block until an element is enqueued");
        }

        @Test
        public void testRemove() throws InterruptedException {
            //WaitablePQSem<Integer> pq = new WaitablePQSem<>();
            //WPQCond<Integer> pq = new WPQCond<>();
            WaitablePQFixedSize<Integer> pq = new WaitablePQFixedSize<>(3);

            // Enqueue elements
            pq.enqueue(1);
            pq.enqueue(2);
            pq.enqueue(3);

            // Remove an element
            assertTrue(pq.remove(2), "Element 2 should be removed");
            assertFalse(pq.remove(4), "Element 4 does not exist in the queue");

            // Dequeue remaining elements
            try {
                assertEquals(1, pq.dequeue());
                assertEquals(3, pq.dequeue());
            } catch (InterruptedException e) {
                fail("Dequeue was interrupted unexpectedly");
            }
        }

        @Test
        public void testCustomComparator() throws InterruptedException {
            //WaitablePQSem<Integer> pq = new WaitablePQSem<>(Comparator.reverseOrder());
            //WPQCond<Integer> pq = new WPQCond<>(Comparator.reverseOrder());
            WaitablePQFixedSize<Integer> pq = new WaitablePQFixedSize<>(3, Comparator.reverseOrder());

            // Enqueue elements
            pq.enqueue(1);
            pq.enqueue(3);
            pq.enqueue(2);

            // Dequeue elements and check reverse order
            assertEquals(3, pq.dequeue());
            assertEquals(2, pq.dequeue());
            assertEquals(1, pq.dequeue());
        }

        @Test
        public void testConcurrentAccess() throws InterruptedException {
            //WaitablePQSem<Integer> pq = new WaitablePQSem<>();
            //WPQCond<Integer> pq = new WPQCond<>();
            WaitablePQFixedSize<Integer> pq = new WaitablePQFixedSize<>(3);
            
            int numberOfProducers = 5;
            int numberOfConsumers = 5;
            int itemsPerProducer = 20;

            // Atomic counters for tracking
            AtomicInteger producedItems = new AtomicInteger(0);
            AtomicInteger consumedItems = new AtomicInteger(0);

            // Producer threads
            Thread[] producers = new Thread[numberOfProducers];
            for (int i = 0; i < numberOfProducers; i++) {
                producers[i] = new Thread(() -> {
                    for (int j = 0; j < itemsPerProducer; j++) {
                        try {
                            pq.enqueue(producedItems.incrementAndGet());
                        } catch (InterruptedException e) {
                            // Exit loop when interrupted
                            break;
                        }
                    }
                });
            }

            // Consumer threads
            Thread[] consumers = new Thread[numberOfConsumers];
            for (int i = 0; i < numberOfConsumers; i++) {
                consumers[i] = new Thread(() -> {
                    while (true) {
                        try {
                            pq.dequeue();
                            consumedItems.incrementAndGet();
                        } catch (InterruptedException e) {
                            // Exit loop when interrupted
                            break;
                        }
                    }
                });
            }

            // Start producers and consumers
            for (Thread producer : producers) {
                producer.start();
            }
            for (Thread consumer : consumers) {
                consumer.start();
            }

            // Wait for all producers to finish
            for (Thread producer : producers) {
                producer.join();
            }

            // Allow consumers to process remaining items
            Thread.sleep(1000);

            // Interrupt all consumers to stop them
            for (Thread consumer : consumers) {
                consumer.interrupt();
            }

            // Wait for all consumers to finish
            for (Thread consumer : consumers) {
                consumer.join();
            }

            // Verify total items produced equals total items consumed
            assertEquals(producedItems.get(), consumedItems.get(), "All produced items should be consumed");

            // Verify the queue is empty after all operations
            //assertTrue(pq.pq.isEmpty(), "Queue should be empty after all operations");
        }
}
