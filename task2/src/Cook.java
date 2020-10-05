import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Cook {
    private final int TIMEOUT_COOKING = 500;

    private final String name;
    private final Lock lock;
    private final Condition condition;
    private final Queue<Order> queueToCooking;

    public Cook(String name) {
        this.name = name;
        lock = new ReentrantLock();
        condition = lock.newCondition();
        queueToCooking = new LinkedList<>();
    }

    public void doWork() {
        System.out.println(name + " на работе!");
        lock.lock();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                while (queueToCooking.isEmpty())
                    condition.await();
                Order order = queueToCooking.poll();
                if (order != null) {
                    System.out.println(name + " готовит для " + order.visitor);
                    Thread.sleep(TIMEOUT_COOKING);
                    System.out.println(name + " закончил готовить для " + order.visitor);
                    order.waiter.giveOrderToVisitor(order.visitor);
                }
            }
        } catch (InterruptedException ignored) {
        } finally {
            lock.unlock();
        }
    }

    public void addToCookingQueue(Visitor visitor, Waiter waiter) {
        lock.lock();
        try {
            queueToCooking.add(new Order(waiter, visitor));
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    private class Order {
        final Waiter waiter;
        final Visitor visitor;

        public Order(Waiter waiter, Visitor visitor) {
            this.waiter = waiter;
            this.visitor = visitor;
        }
    }
}
