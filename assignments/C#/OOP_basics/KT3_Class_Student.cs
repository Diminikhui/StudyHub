using System;
public class Student {
    public string Name;
    public int Age;
    public void Print() {
        Console.WriteLine($"Name: {Name}, Age: {Age}");
    }
}
public class Program {
    public static void Main() {
        Student st = new Student();
        st.Name = "Alice";
        st.Age = 20;
        st.Print();
    }
}