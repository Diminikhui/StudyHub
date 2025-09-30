using System;
public class Order<T> {
    public T Item;
    public void Print() {
        Console.WriteLine(Item);
    }
}
public class Program {
    public static void Main() {
        Order<string> order = new Order<string>();
        order.Item = "Tour service";
        order.Print();
    }
}