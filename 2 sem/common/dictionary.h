#pragma once
#include <stdexcept>
#include <iostream>
#include "node.h"

/// <summary>
/// Структура пары "ключ-значение".
/// </summary>
template <typename T, typename V>
struct Pair
{
    T key;
    V value;
    Pair(T k, V v) : key(k), value(v)
    {
    }
};

/// <summary>
/// Простой односвязный список.
/// </summary>
template <typename T>
class List
{
private:
    Node<T>* head;
    int size;

public:
    /// <summary>
    /// Конструктор по умолчанию.
    /// </summary>
    List() : head(nullptr), size(0)
    {
    }

    /// <summary>
   /// Деструктор.
   /// </summary>
    ~List()
    {
        clear();
    }

    /// <summary>
    /// Добавление элемента в список.
    /// </summary>
    void add(T value)
    {
        if (!head)
            head = new Node<T>(value);
        else
        {
            Node<T>* current = head;
            while (current->next)
                current = current->next;
            current->next = new Node<T>(value);
        }
        size++;
    }

    /// <summary>
    /// Оператор доступа к элементу по индексу.
    /// </summary>
    T& operator[](int index)
    {
        if (index < 0 || index >= size)
            throw std::out_of_range("Индекс вне диапазона");
        Node<T>* current = head;
        for (int i = 0; i < index; i++)
            current = current->next;
        return current->data;
    }

    /// <summary>
    /// Получение количества элементов в списке.
    /// </summary>
    int count() const
    {
        return size;
    }

    /// <summary>
    /// Очистка списка.
    /// </summary>
    void clear()
    {
        while (head)
        {
            Node<T>* temp = head;
            head = head->next;
            delete temp;
        }
        size = 0;
    }
};

/// <summary>
/// Словарь на основе хеш-таблицы с разрешением коллизий.
/// </summary>
template <typename T, typename V>
class Dictionary
{
private:
    List<Pair<T, V>>* table;
    int capacity;
    int size;
    int (*hashFunc)(T);

    /// <summary>
    /// Получение индекса в хеш-таблице по ключу.
    /// </summary>
    int getIndex(T key) const
    {
        int index = hashFunc(key) % capacity;
        return index < 0 ? index + capacity : index;
    }

public:
    /// <summary>
    /// Конструктор словаря.
    /// </summary>
    Dictionary(int (*hash)(T)) : capacity(50), size(0), hashFunc(hash)
    {
        table = new List<Pair<T, V>>[capacity];
    }

    /// <summary>
    /// Деструктор словаря.
    /// </summary>
    ~Dictionary()
    {
        clear();
        delete[] table;
    }

    /// <summary>
    /// Добавление пары ключ-значение в словарь.
    /// </summary>
    void add(T key, V value)
    {
        int index = getIndex(key);
        List<Pair<T, V>>& bucket = table[index];
        for (int i = 0; i < bucket.count(); ++i)
        {
            if (bucket[i].key == key)
            {
                bucket[i].value = value;
                return;
            }
        }
        bucket.add(Pair<T, V>(key, value));
        size++;
    }

    /// <summary>
    /// Удаление пары по ключу.
    /// </summary>
    void remove(T key)
    {
        int index = getIndex(key);
        List<Pair<T, V>>& bucket = table[index];
        List<Pair<T, V>> newBucket;
        bool removed = false;
        for (int i = 0; i < bucket.count(); ++i)
        {
            if (bucket[i].key != key)
                newBucket.add(bucket[i]);
            else
                removed = true;
        }
        bucket = newBucket;
        if (removed) size--;
    }

    /// <summary>
    /// Оператор доступа к значению по ключу.
    /// </summary>
    V& operator[](const T key)
    {
        int index = getIndex(key);
        List<Pair<T, V>>& bucket = table[index];
        for (int i = 0; i < bucket.count(); ++i)
        {
            if (bucket[i].key == key)
                return bucket[i].value;
        }
        throw std::out_of_range("Ключ не найден");
    }

    /// <summary>
    /// Попытка получить значение по ключу.
    /// </summary>
    bool TryGetValue(T key, V& value)
    {
        int index = getIndex(key);
        List<Pair<T, V>>& bucket = table[index];
        for (int i = 0; i < bucket.count(); ++i)
        {
            if (bucket[i].key == key)
            {
                value = bucket[i].value;
                return true;
            }
        }
        return false;
    }

    /// <summary>
    /// Очистка словаря.
    /// </summary>
    void clear()
    {
        for (int i = 0; i < capacity; ++i)
            table[i].clear();
        size = 0;
    }

    /// <summary>
    /// Получение количества элементов в словаре.
    /// </summary>
    int count() const
    {
        return size;
    }

    /// <summary>
    /// Получение всех ключей в словаре.
    /// </summary>
    List<T>* GetKeys()
    {
        List<T>* keys = new List<T>();
        for (int i = 0; i < capacity; ++i)
        {
            List<Pair<T, V>>& bucket = table[i];
            for (int j = 0; j < bucket.count(); ++j)
                keys->add(bucket[j].key);
        }
        return keys;
    }

    /// <summary>
    /// Получение всех значений в словаре.
    /// </summary>
    List<V>* GetValues()
    {
        List<V>* values = new List<V>();
        for (int i = 0; i < capacity; ++i)
        {
            List<Pair<T, V>>& bucket = table[i];
            for (int j = 0; j < bucket.count(); ++j)
                values->add(bucket[j].value);
        }
        return values;
    }

    /// <summary>
    /// Преобразование словаря в список пар.
    /// </summary>
    List<Pair<T, V>>* ToList()
    {
        List<Pair<T, V>>* list = new List<Pair<T, V>>();
        for (int i = 0; i < capacity; ++i)
        {
            List<Pair<T, V>>& bucket = table[i];
            for (int j = 0; j < bucket.count(); ++j)
                list->add(bucket[j]);
        }
        return list;
    }
};