using System;
public enum ListOfClients { Alice, Bob, Charlie }
public class Program {
    public static void Main() {
        ListOfClients client = ListOfClients.Bob;
        Console.WriteLine($"Chosen client: {client}");
    }
}