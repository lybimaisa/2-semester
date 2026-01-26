#include <iostream>
#include <queue.h>

using namespace std;

int displayMenu();

template <typename T, typename Predicate>
void insertBeforeIf(Queue<T>& queue, T valueToInsert, Predicate predicate)
{
    int originalSize = queue.count();

    for (int i = 0; i < originalSize; ++i)
    {
        T value = queue.unqueue();

        if (predicate(value))
        {
            queue.queue(valueToInsert);
        }
        queue.queue(value);
    }
}

template <typename T, typename Predicate>
void removeIf(Queue<T>& queue, Predicate predicate)
{
    int originalSize = queue.count();

    for (int i = 0; i < originalSize; ++i)
    {
        T value = queue.unqueue();

        if (predicate(value))
        {
            queue.queue(value);
        }
    }
}

template <typename T>
int countOccurrences(Queue<T>& queue, T value)
{
    int count = 0;
    int originalSize = queue.count();

    for (int i = 0; i < originalSize; ++i)
    {
        T currentValue = queue.unqueue();
        if (currentValue == value)
        {
            count++;
        }
        queue.queue(currentValue);
    }
    return count;
}

template <typename T>
void printQueue(Queue<T>& queue)
{
    int originalSize = queue.count();
    for (int i = 0; i < originalSize; ++i)
    {
        T value = queue.unqueue();
        cout << value << " ";
        queue.queue(value);
    }
    cout << endl;
}

int main()
{
    setlocale(LC_ALL, "RUS");

    Queue<int> intQueue;

    int choice;
    int value;

    do
    {
        choice = displayMenu();

        switch (choice)
        {
        case 1:
        {
            int value;
            cout << "Введите число: ";
            cin >> value;
            intQueue.queue(value);
        }
        break;
        case 2:
            try
            {
                cout << "Извлечено: " << intQueue.unqueue() << endl;
            }
            catch (const out_of_range& e)
            {
                cout << e.what() << endl;
            }
            break;

        case 3:
            cout << "Количество элементов: " << intQueue.count() << endl;
            break;

        case 4:
            intQueue.clear();
            cout << "Очередь очищена.\n";
            break;

        case 5:
        {
            int valueToInsert;
            cout << "Введите значение, которое нужно вставить: ";
            cin >> valueToInsert;

            insertBeforeIf(intQueue, valueToInsert, [](const int& val) { return val < 0; });

            cout << "Вставлены " << valueToInsert << " перед элементами, удовлетворяющими условию.\n";
        }
        break;
        case 6:
        {
            removeIf(intQueue, [](const int& val) { return val < 0; });
            cout << "Удалены все элементы, удовлетворяющие условию.\n";
        }
        break;
        case 7:
            cout << "Введите число для подсчета вхождений: ";
            cin >> value;
            cout << "Число " << value << " встречается " << countOccurrences(intQueue, value) << " раз.\n";
            break;
        case 8:
            cout << "Очередь: ";
            printQueue(intQueue);
            break;
        case 9:
            cout << "Выход...\n";
            break;

        default:
            cout << "Неверный выбор, попробуйте снова.\n";
        }
    } while (choice != 9);

    return 0;
}

int displayMenu()
{
    int choice;
    cout << "\nМеню:\n";
    cout << "1. Добавить элемент в очередь\n";
    cout << "2. Извлечь элемент из очереди\n";
    cout << "3. Показать количество элементов\n";
    cout << "4. Очистить очередь\n";
    cout << "5. Вставить 1 перед каждым отрицательным числом\n";
    cout << "6. Удалить все отрицательные числа\n";
    cout << "7. Подсчитать вхождения числа\n";
    cout << "8. Вывести очередь\n";
    cout << "9. Выйти\n";
    cout << "Выберите действие: ";
    cin >> choice;
    return choice;
}