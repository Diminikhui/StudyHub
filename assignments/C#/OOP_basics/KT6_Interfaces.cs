using System;
public interface IBoss { void Command(); }
public interface IWorker { void Work(); }
public interface ILazy { void Sleep(); }

public class Person : IBoss, IWorker, ILazy {
    public void Command() => Console.WriteLine("Giving orders");
    public void Work() => Console.WriteLine("Working");
    public void Sleep() => Console.WriteLine("Sleeping");
}
public class Program {
    public static void Main() {
        Person p = new Person();
        p.Command();
        p.Work();
        p.Sleep();
    }
}