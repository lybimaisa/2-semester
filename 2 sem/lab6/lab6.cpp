#include "tree.h"

int main()
{
    setlocale(LC_ALL, "Russian");

    Tree<int> tree;

    int choice;
    int value;

    do
    {
        cout << "\nМеню:\n";
        cout << "1. Добавить элемент\n";
        cout << "2. Удалить элемент\n";
        cout << "3. Проверить наличие элемента\n";
        cout << "4. Показать все элементы (Infix)\n";
        cout << "5. Очистить дерево\n";
        cout << "6. Количество элементов\n";
        cout << "0. Выход\n";
        cout << "Выбор: ";
        cin >> choice;

        switch (choice)
        {
        case 1:
            cout << "Введите значение: ";
            cin >> value;
            tree.add(value);
            break;
        case 2:
            cout << "Введите значение: ";
            cin >> value;
            tree.remove(value);
            break;
        case 3:
            cout << "Введите значение: ";
            cin >> value;
            cout << (tree.contains(value) ? "Есть в дереве." : "Нет в дереве.") << endl;
            break;
        case 4:
        {
            List<int> list = tree.ToList();
            cout << "Элементы дерева: ";
            for (int i = 0; i < list.count(); i++) {
                cout << list[i] << " ";
            }
            cout << endl;
            break;
        }
        case 5:
            tree.clear();
            cout << "Дерево очищено.\n";
            break;
        case 6:
            cout << "Количество элементов: " << tree.count() << endl;
            break;
        }

    } while (choice != 0);

    return 0;
}