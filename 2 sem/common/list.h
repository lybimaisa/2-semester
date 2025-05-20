#pragma once
#include <stdexcept>
#include <string>
#include <algorithm>

using namespace std;

/// <summary>
/// Шаблонная структура узла двусвязного списка
/// </summary>
/// <typeparam name="T">Тип данных, хранящихся в узле</typeparam>
template <typename T>
struct Node
{
    T data;
    Node* prev;
    Node* next;

    Node(T value, Node* prevNode = nullptr, Node* nextNode = nullptr)
        : data(value), prev(prevNode), next(nextNode)
    {
    }
};

/// <summary>
/// Структура для хранения информации о городе
/// </summary>
struct City
{
    string name;
    string region;
    int population;

    /// <summary>
    /// Оператор сравнения двух городов
    /// </summary>
    bool operator==(const City& other) const
    {
        return name == other.name && region == other.region && population == other.population;
    }
};

/// <summary>
/// Шаблонный класс двусвязного списка
/// </summary>
/// <typeparam name="T">Тип элементов списка</typeparam>
template <typename T>
class List
{
private:
    Node<T>* head;
    Node<T>* tail;
    int size;

public:
    /// <summary>
    /// Конструктор по умолчанию
    /// </summary>
    List() : head(nullptr), tail(nullptr), size(0)
    {
    }

    /// <summary>
    /// Деструктор
    /// </summary>
    ~List()
    {
        clear();
    }

    /// <summary>
    /// Добавление элемента в конец списка
    /// </summary>
    void add(T value)
    {
        Node<T>* newNode = new Node<T>(value, tail, nullptr);
        if (tail) tail->next = newNode;
        else head = newNode;
        tail = newNode;
        size++;
    }

    /// <summary>
    /// Вставка элемента по индексу
    /// </summary>
    void insert(int index, const T& value)
    {
        if (index < 0 || index > size)
            throw out_of_range("Индекс вне диапазона");

        if (index == size)
        {
            add(value);
            return;
        }

        if (index == 0)
        {
            Node<T>* newNode = new Node<T>(value, nullptr, head);
            if (head) head->prev = newNode;
            head = newNode;
            if (!tail) tail = newNode;
        }
        else
        {
            Node<T>* current = head;
            for (int i = 0; i < index; i++) current = current->next;
            Node<T>* newNode = new Node<T>(value, current->prev, current);
            current->prev->next = newNode;
            current->prev = newNode;
        }
        size++;
    }

    /// <summary>
    /// Удаление элемента по индексу
    /// </summary>
    void removeAt(int index)
    {
        if (index < 0 || index >= size)
            throw out_of_range("Индекс вне диапазона");

        Node<T>* toDelete;
        if (index == 0)
        {
            toDelete = head;
            head = head->next;
            if (head) head->prev = nullptr;
            else tail = nullptr;
        }
        else if (index == size - 1)
        {
            toDelete = tail;
            tail = tail->prev;
            tail->next = nullptr;
        }
        else
        {
            Node<T>* current = head;
            for (int i = 0; i < index; i++) current = current->next;
            toDelete = current;
            current->prev->next = current->next;
            current->next->prev = current->prev;
        }
        delete toDelete;
        size--;
    }

    /// <summary>
    /// Доступ к элементу по индексу
    /// </summary>
    T& operator[](const int index)
    {
        if (index < 0 || index >= size)
            throw out_of_range("Индекс вне диапазона");

        Node<T>* current = head;
        for (int i = 0; i < index; i++) current = current->next;
        return current->data;
    }

    /// <summary>
    /// Получение количества элементов
    /// </summary>
    int count()
    {
        return size;
    }

    /// <summary>
    /// Подсчет вхождений значения
    /// </summary>
    int count(T value)
    {
        int counter = 0;
        Node<T>* current = head;
        while (current)
        {
            if (current->data == value) counter++;
            current = current->next;
        }
        return counter;
    }

    /// <summary>
    /// Очистка списка
    /// </summary>
    void clear()
    {
        while (head)
        {
            Node<T>* temp = head;
            head = head->next;
            delete temp;
        }
        tail = nullptr;
        size = 0;
    }
};