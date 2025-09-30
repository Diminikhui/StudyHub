using System;
public class Hosts {
    public string Name;
    public string Domain;
    public void Print() {
        Console.WriteLine($"{Name} - {Domain}");
    }
}
public class Program {
    public static void Main() {
        Hosts host = new Hosts();
        host.Name = "Google";
        host.Domain = "google.com";
        host.Print();
    }
}