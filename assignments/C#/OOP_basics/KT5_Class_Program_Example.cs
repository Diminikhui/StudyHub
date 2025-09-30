using System;
public class Enrollee {
    public string FullName;
    public int Score;
    public void Print() {
        Console.WriteLine($"{FullName} - {Score}");
    }
}
public class Program {
    public static void Main() {
        Enrollee e = new Enrollee();
        e.FullName = "Bob Smith";
        e.Score = 95;
        e.Print();
    }
}