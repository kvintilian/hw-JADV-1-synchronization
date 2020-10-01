import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private final int MAX_ORDERS = 5;
    private final int MAX_WAITERS = 3;
    private final int MAX_VISITORS = 7;

    private final ThreadGroup waiters;
    private final List<Thread> visitors;
    private final ArrayDeque<Order> orders;
    private int orderCount = 0;

    public Restaurant() throws InterruptedException {
        System.out.println("Ресторан открыт!");
        this.orders = new ArrayDeque<>();
        // Выходят официанты
        waiters = new ThreadGroup("Официанты");
        for (int i = 0; i < MAX_WAITERS; i++) {
            Waiter waiter = new Waiter(this, "Официант " + (i + 1));
            new Thread(waiters, waiter::doWork, waiter.toString()).start();
        }

        // Впускаем кра.. посетителей
        visitors = new ArrayList<>();
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

    public void addOrder(Order order) {
        if (orderCount < MAX_ORDERS) {
            synchronized (orders) {
                orders.add(order);
                orderCount += 1;
                orders.notify();
            }
        } else {
            System.out.println("Ресторан больше не может принять заказов!");
            order.getVisitor().kick();
        }
    }

    public Order getNextOrder() throws InterruptedException {
        Order order;
        synchronized (orders) {
            while (orders.isEmpty()) {
                orders.wait();
            }
            order = orders.removeFirst();
        }
        return order;
    }
}

