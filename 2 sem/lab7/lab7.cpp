#include <iostream>
#include "set.h"

using namespace std;

void printList(Node<int>* head)
{
    Node<int>* current = head;
    while (current)
    {
        cout << current->data << " ";
        current = current->next;
    }
    cout << endl;
}

int main()
{
    setlocale(LC_ALL, "RUS");
    Set<int> set;
    int choice, value;

    do
    {
        cout << "\n--- МЕНЮ ---\n";
        cout << "1. Добавить элемент\n";
        cout << "2. Удалить элемент\n";
        cout << "3. Проверить наличие элемента\n";
        cout << "4. Очистить множество\n";
        cout << "5. Количество элементов\n";
        cout << "6. Показать элементы\n";
        cout << "0. Выход\n";
        cout << "Ваш выбор: ";
        cin >> choice;

        switch (choice)
        {
        case 1:
            cout << "Введите значение: ";
            cin >> value;
            set.add(value);
            break;
        case 2:
            cout << "Введите значение: ";
            cin >> value;
            set.remove(value);
            break;
        case 3:
            cout << "Введите значение: ";
            cin >> value;
            cout << (set.contains(value) ? "Присутствует\n" : "Отсутствует\n");
            break;
        case 4:
            set.clear();
            cout << "Множество очищено.\n";
            break;
        case 5:
            cout << "Количество элементов: " << set.count() << "\n";
            break;
        case 6:
            cout << "Элементы множества: ";
            printList(set.ToList());
            break;
        case 0:
            cout << "Выход...\n";
            break;
        default:
            cout << "Неверный выбор!\n";
        }
    } while (choice != 0);

    return 0;
}