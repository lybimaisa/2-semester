#pragma once
#include "node.h"

using namespace std;

/// <summary>
/// Очередь с использованием шаблонного класса и связного списка.
/// </summary>
template <typename T>
class Queue
{
private:
    Node<T>* front = nullptr;
    Node<T>* rear = nullptr;
    int size = 0;

public:
    /// <summary>
    // Конструктор по умолчанию
    /// </summary>
    Queue() = default;

    /// <summary>
    // Конструктор копирования удаляем
    /// </summary>
    Queue(const Queue& other) = delete;

    /// <summary>
    // Оператор присваивания удаляем
    /// </summary>
    Queue& operator=(const Queue& other) = delete;

    /// <summary>
    /// Деструктор для автоматического освобождения памяти.
    /// </summary>
    ~Queue()
    {
        clear();
    }

    /// <summary>
    /// Добавляет новый элемент в конец очереди.
    /// </summary>
    /// <param name="value">Элемент, который нужно добавить.</param>
    void queue(T value)
    {
        Node<T>* newNode = new Node<T>(value);
        if (rear)
        {
            rear->next = newNode;
        }
        else
        {
            front = newNode;
        }
        rear = newNode;
        size++;
    }

    /// <summary>
    /// Удаляет и возвращает элемент из начала очереди.
    /// </summary>
    /// <returns>Удалённое значение.</returns>
    T unqueue()
    {
        if (!front)
        {
            throw out_of_range("Queue is empty");
        }
        Node<T>* temp = front;
        T value = temp->data;
        front = front->next;
        if (!front)
        {
            rear = nullptr;
        }
        delete temp;
        size--;
        return value;
    }

    /// <summary>
    /// Возвращает текущее количество элементов в очереди.
    /// </summary>
    /// <returns>Количество элементов в очереди.</returns>
    int count() const
    {
        return size;
    }

    /// <summary>
    /// Очищает очередь, удаляя все элементы.
    /// </summary>
    void clear()
    {
        while (!isEmpty())
        {
            unqueue();
        }
    }

    /// <summary>
    /// Проверяет, является ли очередь пустой.
    /// </summary>
    bool isEmpty() const
    {
        return size == 0;
    }
};