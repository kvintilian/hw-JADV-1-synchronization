import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Visitor {
    private final int TIMEOUT_MIN = 800;
    private final int TIMEOUT_MAX = 1500;

    private final Restaurant restaurant;
    private final String name;
    private final int timeOutMakeOrder;
    private final int timeOutEat;

    private final Lock lock;
    final Condition condition;

    public Visitor(Restaurant restaurant, String name) {
        this.restaurant = restaurant;
        this.name = name;
        Random random = new Random();
        this.timeOutMakeOrder = random.nextInt(TIMEOUT_MAX - TIMEOUT_MIN) + TIMEOUT_MIN;
        this.timeOutEat = random.nextInt(TIMEOUT_MAX - TIMEOUT_MIN) + TIMEOUT_MIN;
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    void begin() {
        try {
            makeOrder();
        } catch (InterruptedException ignored) {
        }
    }

    public void makeOrder() throws InterruptedException {
        lock.lock();
        try {
            System.out.println(name + " в ресторане");
            Thread.sleep(timeOutMakeOrder);
            restaurant.readyOrder(this);
            condition.await();
            System.out.println(Thread.currentThread().getName() + " приступил к еде");
            Thread.sleep(timeOutEat);
            System.out.println(Thread.currentThread().getName() + " вышел из ресторана");
        } finally {
            lock.unlock();
        }
    }

    public void receiveOrder() {
        lock.lock();
        try {
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public void kick() {
        System.out.println(Thread.currentThread().getName() + " вышел из ресторана голодным");
        Thread.currentThread().interrupt();
    }
}
