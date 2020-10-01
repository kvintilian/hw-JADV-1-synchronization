public class Waiter {
    private final Restaurant restaurant;
    private final String name;

    public Waiter(Restaurant restaurant, String name) {
        this.restaurant = restaurant;
        this.name = name;
    }

    public void doWork() {
        System.out.println(name + " на работе!");
        waitingForOrder();
    }

    private void waitingForOrder() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Order order = restaurant.getNextOrder();
                Visitor visitor = order.getVisitor();
                System.out.println(name + " взял заказ у " + visitor);
                Thread.sleep(1000);
                System.out.println(name + " несет заказ");
                Thread.sleep(3000);
                synchronized (order) {
                    order.notify();
                    System.out.println(name + " отдал заказ " + visitor);
                }
            }
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public String toString() {
        return name ;
    }
}
