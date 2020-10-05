public class Main {
    public static void main(String[] args) {

        final Restaurant restaurant = new Restaurant();
        try {
            restaurant.open();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
