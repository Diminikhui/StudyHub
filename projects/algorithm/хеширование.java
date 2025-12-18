public class HashTableFIO {

    private static class Entry {
        String key;
        String value;

        Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private Entry[] table;

    public HashTableFIO(int size) {
        table = new Entry[size];
    }

    private int hash(String key) {
        int hashValue = 0;
        for (char c : key.toCharArray()) {
            hashValue += c;
        }
        return hashValue % table.length;
    }

    public void insert(String key, String value) {
        int index = hash(key);

        if (table[index] != null) {
            System.out.println("Коллизия при вставке: " + key + " в индекс " + index);
        }

        while (table[index] != null) {
            index = (index + 1) % table.length;
        }

        table[index] = new Entry(key, value);
    }

    public void display() {
        for (int i = 0; i < table.length; i++) {
            if (table[i] == null) {
                System.out.println(i + ": [пусто]");
            } else {
                System.out.println(i + ": " + table[i].key + " → " + table[i].value);
            }
        }
    }

    public static void main(String[] args) {
        HashTableFIO hashTable = new HashTableFIO(20);

        hashTable.insert("Чебурек Чебоксаров", "Чебоксары");
        hashTable.insert("Петров Андрей", "Москва");
        hashTable.insert("Сидорова Анна", "Казань");
        hashTable.insert("Кузнецов Павел", "Самара");
        hashTable.insert("Смирнова Мария", "Уфа");
        hashTable.insert("Сергеев Иван", "Тюмень");
        hashTable.insert("Белов Дмитрий", "Омск");
        hashTable.insert("Королева Ирина", "Пермь");
        hashTable.insert("Александров Олег", "Тула");
        hashTable.insert("Яковлева Дарья", "Воронеж");

        System.out.println("\nСодержимое хеш-таблицы:");
        hashTable.display();
    }
}
