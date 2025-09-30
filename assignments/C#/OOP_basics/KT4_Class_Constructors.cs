using System;
public class Point_In_Space {
    public double X, Y, Z;
    public Point_In_Space(double x, double y, double z) {
        X = x; Y = y; Z = z;
    }
    public void Print() {
        Console.WriteLine($"Point({X}, {Y}, {Z})");
    }
}
public class Program {
    public static void Main() {
        Point_In_Space point = new Point_In_Space(1,2,3);
        point.Print();
    }
}