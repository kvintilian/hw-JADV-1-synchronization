import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Restaurant {
    private final int MAX_ORDERS = 5;
    private final int MAX_WAITERS = 3;
    private final int MAX_VISITORS = 7;

    private final ThreadGroup waiters;
    private final List<Thread> visitors;
    final Cook cook;
    private Thread cookThread;
    private final ArrayDeque<Visitor> visitorsReadyToOrder;
    private int orderCount = 0;
    private final Lock lock;
    private final Condition conditionVisitors;
    private final Condition conditionOrders;

    public Restaurant() {
        this.visitorsReadyToOrder = new ArrayDeque<>();
        waiters = new ThreadGroup("Официанты");
        visitors = new ArrayList<>();
        cook = new Cook("Повар");
        lock = new ReentrantLock();
        conditionVisitors = lock.newCondition();
        conditionOrders = lock.newCondition();
    }

    public void open() throws InterruptedException {
        System.out.println("Ресторан открыт!");
        // Готовим повара
        cookThread = new Thread(null, cook::doWork);
        cookThread.start();

        // Выходят официанты
        for (int i = 0; i < MAX_WAITERS; i++) {
            Waiter waiter = new Waiter(this, "Официант " + (i + 1));
            new Thread(waiters, waiter::doWork, waiter.toString()).start();
        }

        // Впускаем посетителей
        for (int i = 0; i < MAX_VISITORS; i++) {
            Visitor visitor = new Visitor(this, "Посетитель " + (i + 1));
            Thread thread = new Thread(null, visitor::begin, visitor.toString());
            visitors.add(thread);
            thread.start();
        }

        while (true) {
            if (!clientsIs()) {
                closeRestaurant();
                break;
            }
        }
    }

    private void closeRestaurant() {
        waiters.interrupt();
        cookThread.interrupt();
        System.out.println("Ресторан закрыт!");
    }

    private boolean clientsIs() {
        boolean result = false;
        for (Thread visitor : visitors) {
            if (result = visitor.isAlive()) {
                break;
            }
        }
        return result;
    }

    public void readyVisitor(Visitor visitor) {
        System.out.println(visitor + " готов сделать заказ");
        if (orderCount < MAX_ORDERS) {
            lock.lock();
            try {
                visitorsReadyToOrder.add(visitor);
                orderCount += 1;
                conditionVisitors.signal();
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println("Ресторан больше не может принять заказов!");
            visitor.kick();
        }
    }

    public Visitor getReadyVisitor() throws InterruptedException {
        Visitor visitor;
        lock.lock();
        try {
            while (visitorsReadyToOrder.isEmpty())
                conditionVisitors.await();
            visitor = visitorsReadyToOrder.removeFirst();
        } finally {
            lock.unlock();
        }
        return visitor;
    }
}

