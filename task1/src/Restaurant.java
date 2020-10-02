import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private final int MAX_ORDERS = 5;
    private final int MAX_WAITERS = 3;
    private final int MAX_VISITORS = 7;

    private final ThreadGroup waiters;
    private final List<Thread> visitors;
    private final ArrayDeque<Visitor> visitorsReadyToOrder;
    private int orderCount = 0;

    public Restaurant() {
        this.visitorsReadyToOrder = new ArrayDeque<>();
        waiters = new ThreadGroup("Официанты");
        visitors = new ArrayList<>();
    }

    public void open() throws InterruptedException {
        System.out.println("Ресторан открыт!");
        // Выходят официанты
        for (int i = 0; i < MAX_WAITERS; i++) {
            Waiter waiter = new Waiter(this, "Официант " + (i + 1));
            new Thread(waiters, waiter::doWork, waiter.toString()).start();
        }

        // Впускаем кра.. посетителей
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

    public void readyOrder(Visitor visitor) {
        System.out.println(visitor + " готов сделать заказ");
        if (orderCount < MAX_ORDERS) {
            synchronized (visitorsReadyToOrder) {
                visitorsReadyToOrder.add(visitor);
                orderCount += 1;
                visitorsReadyToOrder.notify();
            }
        } else {
            System.out.println("Ресторан больше не может принять заказов!");
            visitor.kick();
        }
    }

    public Visitor getReadyVisitor() throws InterruptedException {
        Visitor visitor;
        synchronized (visitorsReadyToOrder) {
            while (visitorsReadyToOrder.isEmpty()) {
                visitorsReadyToOrder.wait();
            }
            visitor = visitorsReadyToOrder.removeFirst();
        }
        return visitor;
    }
}

