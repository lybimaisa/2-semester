#pragma once
#include "node.h"

/// <summary>
/// Шаблонное хеш-множество на основе массива списков (разрешение коллизий методом цепочек).
/// </summary>
/// <typeparam name="T">Тип элементов множества.</typeparam>
template <typename T>
class Set
{
private:
    static const int SIZE = 10;
    Node<T>* buckets[SIZE];
    int elementCount;

    /// <summary>
    /// Хеш-функция, вычисляет индекс для значения.
    /// </summary>
    /// <param name="value">Значение.</param>
    /// <returns>Хеш-индекс.</returns>
    int hash(T value) const
    {
        // Простая хеш-функция для демонстрации
        size_t h = std::hash<T>{}(value);
        return h % SIZE;
    }

public:
    /// <summary>
    /// Конструктор. Инициализирует пустое множество.
    /// </summary>
    Set() : elementCount(0)
    {
        for (int i = 0; i < SIZE; ++i)
        {
            buckets[i] = nullptr;
        }
    }

    /// <summary>
    /// Деструктор. Освобождает память.
    /// </summary>
    ~Set()
    {
        clear();
    }

    /// <summary>
    /// Добавляет элемент в множество.
    /// </summary>
    /// <param name="value">Добавляемое значение.</param>
    void add(T value)
    {
        int index = hash(value);
        Node<T>* current = buckets[index];

        while (current != nullptr)
        {
            if (current->data == value)
            {
                return;
            }
            current = current->next;
        }
        Node<T>* newNode = new Node<T>(value);
        newNode->next = buckets[index];
        buckets[index] = newNode;
        elementCount++;
    }

    /// <summary>
    /// Удаляет элемент из множества.
    /// </summary>
    /// <param name="value">Удаляемое значение.</param>
    void remove(T value)
    {
        int index = hash(value);
        Node<T>* current = buckets[index];
        Node<T>* prev = nullptr;

        while (current != nullptr)
        {
            if (current->data == value)
            {
                if (prev == nullptr)
                {
                    buckets[index] = current->next;
                }
                else
                {
                    prev->next = current->next;
                }
                delete current;
                elementCount--;
                return;
            }
            prev = current;
            current = current->next;
        }
    }

    /// <summary>
    /// Проверяет, содержится ли значение в множестве.
    /// </summary>
    /// <param name="value">Значение для проверки.</param>
    /// <returns>true, если значение есть в множестве, иначе false.</returns>
    bool contains(T value) const
    {
        int index = hash(value);
        Node<T>* current = buckets[index];

        while (current != nullptr)
        {
            if (current->data == value)
            {
                return true;
            }
            current = current->next;
        }
        return false;
    }

    /// <summary>
    /// Удаляет все элементы множества.
    /// </summary>
    void clear()
    {
        for (int i = 0; i < SIZE; ++i)
        {
            Node<T>* current = buckets[i];
            while (current != nullptr)
            {
                Node<T>* temp = current;
                current = current->next;
                delete temp;
            }
            buckets[i] = nullptr;
        }
        elementCount = 0;
    }

    /// <summary>
    /// Возвращает количество элементов в множестве.
    /// </summary>
    /// <returns>Количество элементов.</returns>
    int count() const
    {
        return elementCount;
    }

    /// <summary>
    /// Преобразует множество в список.
    /// </summary>
    /// <returns>Указатель на список, содержащий все значения множества.</returns>
    Node<T>* ToList() const
    {
        Node<T>* result = nullptr;
        Node<T>* last = nullptr;

        for (int i = 0; i < SIZE; ++i)
        {
            Node<T>* current = buckets[i];
            while (current != nullptr)
            {
                Node<T>* newNode = new Node<T>(current->data);

                if (result == nullptr)
                {
                    result = newNode;
                    last = newNode;
                }
                else
                {
                    last->next = newNode;
                    last = newNode;
                }

                current = current->next;
            }
        }

        return result;
    }
};