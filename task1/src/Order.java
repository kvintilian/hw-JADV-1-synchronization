public class Order {

    private final Visitor visitor;

    public Order(Visitor visitor) {
        this.visitor = visitor;
    }

    public Visitor getVisitor() {
        return visitor;
    }
}
