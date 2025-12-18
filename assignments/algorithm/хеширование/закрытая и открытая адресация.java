//Разрешение коллизий с помощью кучи.
// В хеш-таблицах при закрытой адресации для разрешения коллизий обычно используется список.
// Вместо этого можно применить кучу (min-heap или max-heap).
// Куча идеально подходит для хранения элементов в упорядоченном виде,
// особенно если часто требуется получить минимальный или максимальный элемент.
// Основные преимущества:
//
//Вставка элемента - O(log n)
//
//Быстрый доступ к минимальному/максимальному элементу
//
//Структура не вырождается, как списки.
//
//Каждая корзина хеш-таблицы - это куча, которая отслеживает порядок элементов в корзине.
import java.util.ArrayList;
import java.util.PriorityQueue;

class HeapHashTable {
    private ArrayList<PriorityQueue<Integer>> table;
    private int size;

    public HeapHashTable(int size) {
        this.size = size;
        table = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            table.add(new PriorityQueue<>());  // min-heap
        }
    }

    public void put(int key) {
        int hash = key % size;
        table.get(hash).add(key);
    }

    public void printTable() {
        for (int i = 0; i < size; i++) {
            System.out.println(i + ": " + table.get(i));
        }
    }
}
