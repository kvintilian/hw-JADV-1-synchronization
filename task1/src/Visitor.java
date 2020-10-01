import java.util.Random;

public class Visitor {
    private final int TIMEOUT_MIN = 800;
    private final int TIMEOUT_MAX = 1500;

    private final Restaurant restaurant;
    private final String name;
    private final Order order;
    private final int timeOutMakeOrder;
    private final int timeOutEat;

    public Visitor(Restaurant restaurant, String name) {
        this.restaurant = restaurant;
        this.name = name;
        this.order = new Order(this);
        Random random = new Random();
        this.timeOutMakeOrder = random.nextInt(TIMEOUT_MAX - TIMEOUT_MIN) + TIMEOUT_MIN;
        this.timeOutEat = random.nextInt(TIMEOUT_MAX - TIMEOUT_MIN) + TIMEOUT_MIN;
    }

    void begin() {
        try {
            makeOrder();
        } catch (InterruptedException ignored) {
        }
    }

    private void makeOrder() throws InterruptedException {
        System.out.println(name + " в ресторане");
        Thread.sleep(timeOutMakeOrder);
        restaurant.addOrder(order);
        synchronized (order) {
            order.wait();
            System.out.println(Thread.currentThread().getName() + " приступил к еде");
        }
        Thread.sleep(timeOutEat);
        System.out.println(Thread.currentThread().getName() + " вышел из ресторана");
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
