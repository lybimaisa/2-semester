#include <iostream>
#include <string>
#include "dictionary.h"

using namespace std;

int HashFunction(string key)
{
    int hash = 0;
    for (char ch : key)
        hash += static_cast<int>(ch);
    return hash;
}

int main()
{
    setlocale(LC_ALL, "Russian");
    Dictionary<string, string> dict(HashFunction);

    int choice;
    string key, value;

    do
    {
        cout << "\nМеню:\n";
        cout << "1. Добавить элемент\n";
        cout << "2. Удалить элемент\n";
        cout << "3. Получить значение по ключу\n";
        cout << "4. Попробовать получить значение (TryGetValue)\n";
        cout << "5. Показать все ключи\n";
        cout << "6. Показать все значения\n";
        cout << "7. Показать все пары ключ-значение\n";
        cout << "8. Очистить словарь\n";
        cout << "9. Выход\n";
        cout << "Выбор: ";
        cin >> choice;

        switch (choice)
        {
        case 1:
            cout << "Введите ключ: ";
            cin >> key;
            cout << "Введите значение: ";
            cin >> value;
            dict.add(key, value);
            break;

        case 2:
            cout << "Введите ключ для удаления: ";
            cin >> key;
            dict.remove(key);
            break;

        case 3:
            cout << "Введите ключ: ";
            cin >> key;
            try
            {
                cout << "Значение: " << dict[key] << "\n";
            }
            catch (exception& e)
            {
                cout << e.what() << "\n";
            }
            break;

        case 4:
            cout << "Введите ключ: ";
            cin >> key;
            if (dict.TryGetValue(key, value))
                cout << "Найдено: " << value << "\n";
            else
                cout << "Ключ не найден.\n";
            break;

        case 5:
        {
            List<string>* keys = dict.GetKeys();
            cout << "Ключи:\n";
            for (int i = 0; i < keys->count(); ++i)
                cout << keys->operator[](i) << "\n";
            delete keys;
            break;
        }

        case 6:
        {
            List<string>* values = dict.GetValues();
            cout << "Значения:\n";
            for (int i = 0; i < values->count(); ++i)
                cout << values->operator[](i) << "\n";
            delete values;
            break;
        }

        case 7:
        {
            List<Pair<string, string>>* items = dict.ToList();
            cout << "Пары:\n";
            for (int i = 0; i < items->count(); ++i)
            {
                Pair<string, string> p = items->operator[](i);
                cout << p.key << " => " << p.value << "\n";
            }
            delete items;
            break;
        }

        case 8:
            dict.clear();
            cout << "Словарь очищен.\n";
            break;

        case 9:
            cout << "Выход...\n";
            break;

        default:
            cout << "Неверный выбор.\n";
        }

    } while (choice != 9);

    return 0;
}