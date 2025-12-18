import java.util.*;

public class CityDistanceApp {

    private static Map<String, Map<String, Integer>> distances = new HashMap<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("\nМеню:");
            System.out.println("1. Добавить город");
            System.out.println("2. Добавить расстояние между городами");
            System.out.println("3. Показать расстояния");
            System.out.println("4. Выход");
            System.out.print("Выберите пункт: ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                System.out.print("Введите название города: ");
                String city = sc.nextLine();
                distances.putIfAbsent(city, new HashMap<>());
                System.out.println("Город добавлен.");

            } else if (choice == 2) {
                System.out.print("Введите первый город: ");
                String a = sc.nextLine();

                System.out.print("Введите второй город: ");
                String b = sc.nextLine();

                System.out.print("Введите расстояние между ними: ");
                int d = sc.nextInt();
                sc.nextLine();

                distances.putIfAbsent(a, new HashMap<>());
                distances.putIfAbsent(b, new HashMap<>());

                distances.get(a).put(b, d);
                distances.get(b).put(a, d);

                System.out.println("Расстояние сохранено.");

            } else if (choice == 3) {
                System.out.println("\nСписок расстояний между городами:");
                for (String city : distances.keySet()) {
                    for (Map.Entry<String, Integer> entry : distances.get(city).entrySet()) {
                        System.out.println(city + " — " + entry.getKey() + " : " + entry.getValue() + " км");
                    }
                }

            } else if (choice == 4) {
                System.out.println("Выход...");
                break;

            } else {
                System.out.println("Неверный пункт меню.");
            }
        }
    }
}
