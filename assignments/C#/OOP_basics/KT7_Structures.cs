using System;
public struct Center {
    public double X, Y;
}
public struct Okrug {
    public string Name;
    public Center Center;
}
public class Program {
    public static void Main() {
        Okrug o = new Okrug();
        o.Name = "Central";
        o.Center = new Center { X = 0, Y = 0 };
        Console.WriteLine($"{o.Name}: ({o.Center.X},{o.Center.Y})");
    }
}