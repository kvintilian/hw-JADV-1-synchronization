import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Waiter {
    private final int TIMEOUT_CARRY = 200;

    private final Restaurant restaurant;
    private final String name;

    private final Lock lock;


    public Waiter(Restaurant restaurant, String name) {
        this.restaurant = restaurant;
        this.name = name;
        lock = new ReentrantLock();
    }

    public void doWork() {
        System.out.println(name + " на работе!");
        waitingForOrder();
    }

    private void waitingForOrder() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Visitor visitor = restaurant.getReadyVisitor();
                System.out.println(name + " взял заказ у " + visitor);
                restaurant.cook.addToCookingQueue(visitor, this);
            }
        } catch (InterruptedException ignored) {
        }
    }

    public void giveOrderToVisitor(Visitor visitor) {
        System.out.println(name + " несет заказ");
        try {
            Thread.sleep(TIMEOUT_CARRY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(name + " отдал заказ " + visitor);
        visitor.receiveOrder();
    }

    @Override
    public String toString() {
        return name;
    }
}
