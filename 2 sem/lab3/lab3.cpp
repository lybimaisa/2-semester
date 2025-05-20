#include <iostream>
#include "cyclelist.h"

using namespace std;

void insertBeforeNegatives(CycleList<int>& list)
{
    for (int i = 0; i < list.count(); ++i)
    {
        if (list[i] < 0)
        {
            list.insert(i, 1);
            ++i;
        }
    }
}
void removeNegatives(CycleList<int>& list)
{
    if (list.count() == 0) return;

    int index = 0;
    while (index < list.count())
    {
        if (list[index] < 0)
        {
            list.removeAt(index);
        }
        else {
            index++;
        }
    }
}

void Menu()
{
    cout << "Меню:" << endl;
    cout << "1. Добавить число" << endl;
    cout << "2. Вставить число по индексу" << endl;
    cout << "3. Удалить по индексу" << endl;
    cout << "4. Показать число по индексу" << endl;
    cout << "5. Количество чисел\n";
    cout << "6. Сколько раз встречается число" << endl;
    cout << "7. Очистить список" << endl;
    cout << "8. Вставить 1 перед минусами" << endl;
    cout << "9. Удалить все минусы" << endl;
    cout << "0. Выход" << endl;
    cout << "Выбери: ";
}

int main()
{
    setlocale(LC_ALL, "Russian");
    CycleList<int> list;
    int choice, value, index;

    while (true)
    {
        Menu();
        cin >> choice;

        switch (choice)
        {
        case 1:
            cout << "Введи число: ";
            cin >> value;
            list.add(value);
            break;

        case 2:
            cout << "Введи индекс и число: ";
            cin >> index >> value;
            list.insert(index, value);
            break;

        case 3:
            cout << "Введи индекс: ";
            cin >> index;
            list.removeAt(index);
            break;

        case 4:
            cout << "Введи индекс: ";
            cin >> index;
            cout << "Число: " << list[index] << endl;
            break;

        case 5:
            cout << "Всего чисел: " << list.count() << endl;
            break;

        case 6:
            cout << "Какое число искать? ";
            cin >> value;
            cout << "Найдено: " << list.count(value) << endl;
            break;

        case 7:
            list.clear();
            cout << "Список очищен";
            break;

        case 8:
            insertBeforeNegatives(list);
            break;

        case 9:
            removeNegatives(list);
            break;

        case 0:
            return 0;

        default:
            cout << "Нет такого пункта";
        }
    }
}