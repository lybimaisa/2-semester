#include <iostream>
#include "list.h"

void printMenu();

void printCities(List<City>& cities)
{
    if (cities.count() == 0)
    {
        cout << "Список пуст.\n";
        return;
    }

    for (int i = 0; i < cities.count(); i++)
    {
        City city = cities[i];
        cout << i << ". " << city.name << ", " << city.region
            << ", население: " << city.population << "\n";
    }
}

void printRegionsByPopulation(List<City>& cities)
{
    struct RegionInfo
    {
        string name;
        int totalPopulation;

        RegionInfo() : name(""), totalPopulation(0)
        {
        }

        RegionInfo(const string& n, int p) : name(n), totalPopulation(p)
        {
        }

        bool operator<(const RegionInfo& other) const
        {
            return totalPopulation > other.totalPopulation;
        }
    };

    List<RegionInfo> regions;

    for (int i = 0; i < cities.count(); i++)
    {
        City city = cities[i];
        bool found = false;

        for (int j = 0; j < regions.count(); j++)

        {
            if (regions[j].name == city.region)
            {
                regions[j].totalPopulation += city.population;
                found = true;
                break;
            }
        }

        if (!found)
        {
            RegionInfo newRegion;
            newRegion.name = city.region;
            newRegion.totalPopulation = city.population;
            regions.add(newRegion);
        }
    }

    for (int i = 0; i < regions.count() - 1; i++)
    {
        for (int j = 0; j < regions.count() - i - 1; j++)
        {
            if (regions[j].totalPopulation < regions[j + 1].totalPopulation)
            {
                swap(regions[j], regions[j + 1]);
            }
        }
    }

    cout << "Регионы по населению (по убыванию):\n";
    for (int i = 0; i < regions.count(); i++)
    {
        cout << regions[i].name << ": " << regions[i].totalPopulation << " жителей\n";
    }
}

void removeCitiesByRegion(List<City>& cities)
{
    string region;
    cout << "Введите название региона: ";
    cin.ignore();
    getline(cin, region);

    for (int i = cities.count() - 1; i >= 0; i--)
    {
        if (cities[i].region == region)
        {
            cities.removeAt(i);
        }
    }
    cout << "Города региона '" << region << "' удалены.\n";
}

int main()
{
    setlocale(LC_ALL, "RUS");
    List<City> cities;
    int choice;

    do
    {
        printMenu();
        cin >> choice;

        try
        {
            switch (choice)
            {
            case 1:
            {
                City city;
                cout << "Название города: ";
                cin.ignore();
                getline(cin, city.name);
                cout << "Название региона: ";
                getline(cin, city.region);
                cout << "Численность населения: ";
                cin >> city.population;
                cities.add(city);
                break;
            }
            case 2:
            {
                int index;
                City city;
                cout << "Индекс: ";
                cin >> index;
                cout << "Название города: ";
                cin.ignore();
                getline(cin, city.name);
                cout << "Название региона: ";
                getline(cin, city.region);
                cout << "Численность населения: ";
                cin >> city.population;
                cities.insert(index, city);
                break;
            }
            case 3:
            {
                int index;
                cout << "Индекс: ";
                cin >> index;
                cities.removeAt(index);
                break;
            }
            case 4:
            {
                int index;
                cout << "Индекс: ";
                cin >> index;
                City& city = cities[index];
                cout << "Город: " << city.name << ", " << city.region
                    << ", население: " << city.population << "\n";
                break;
            }
            case 5:
            {
                cout << "Всего городов: " << cities.count() << "\n";
                break;
            }
            case 6:
            {
                City city;
                cout << "Название города: ";
                cin.ignore();
                getline(cin, city.name);
                cout << "Название региона: ";
                getline(cin, city.region);
                cout << "Численность населения: ";
                cin >> city.population;
                cout << "Найдено: " << cities.count(city) << "\n";
                break;
            }
            case 7:
            {
                cities.clear();
                cout << "Список очищен.\n";
                break;
            }
            case 8:
            {
                printCities(cities);
                break;
            }
            case 9:
            {
                printRegionsByPopulation(cities);
                break;
            }
            case 10:
            {
                removeCitiesByRegion(cities);
                break;
            }
            case 0:
            {
                cout << "Выход...\n";
                break;
            }
            default:
            {
                cout << "Неверный выбор.\n";
                break;
            }
            }
        }
        catch (const out_of_range& e)
        {
            cout << "Ошибка: " << e.what() << "\n";
        }

    } while (choice != 0);

    return 0;
}

void printMenu()
{
    cout << "\nМеню:\n";
    cout << "1. Добавить город\n";
    cout << "2. Вставить город по индексу\n";
    cout << "3. Удалить город по индексу\n";
    cout << "4. Получить город по индексу\n";
    cout << "5. Количество городов\n";
    cout << "6. Количество одинаковых городов\n";
    cout << "7. Очистить список\n";
    cout << "8. Вывести все города\n";
    cout << "9. Вывести регионы по населению\n";
    cout << "10. Удалить города региона\n";
    cout << "0. Выход\n";
    cout << "Выберите действие: ";
}