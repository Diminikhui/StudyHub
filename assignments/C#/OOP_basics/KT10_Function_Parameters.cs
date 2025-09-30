using System;
public struct Center {
    public double X, Y;
}
public struct Okrug {
    public string Name;
    public Center Center;
}
public class Program {
    public static void PrintOkrug(Okrug o) {
        Console.WriteLine($"{o.Name}: ({o.Center.X},{o.Center.Y})");
    }
    public static void Main() {
        Okrug o = new Okrug { Name = "South", Center = new Center { X = 10, Y = 20 } };
        PrintOkrug(o);
    }
}