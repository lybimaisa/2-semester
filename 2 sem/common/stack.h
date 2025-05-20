#pragma once

#include <stdexcept>

using namespace std;

/// <summary>
///  Класс для работы со стеком (LIFO - последний вошел, первый вышел).
/// </summary>
template <typename T>
class Stack
{
private:
    struct Node
    {
        T data;
        Node* next;
    };

    Node* top;
    int size;

public:
    /// <summary>
    ///  Конструктор по умолчанию
    /// </summary>
    Stack() = default;

    /// <summary>
    /// Деструктор. Удаляет все элементы из стека.
    /// </summary>
    ~Stack()
    {
        clear();
    }

    /// <summary>
    /// Добавляет элемент на вершину стека.
    /// </summary>
    void push(T value)
    {
        Node* temp = new Node();
        temp->data = value;
        temp->next = top;
        top = temp;
        size++;
    }

    /// <summary>
    /// Удаляет верхний элемент из стека и возвращает его значение.
    /// </summary>
    T pop()
    {
        if (top == nullptr)
        {
            throw runtime_error("Stack is empty!");
        }
        Node* temp = top->next;
        T data = top->data;
        delete top;
        top = temp;
        size--;
        return data;
    }

    /// <summary>
    /// Возвращает значение верхнего элемента без удаления из стека.
    /// </summary>
    T peek() const
    {
        if (top == nullptr)
        {
            throw runtime_error("Stack is empty!");
        }
        return top->data;
    }

    /// <summary>
    ///  Возвращает количество элементов в стеке.
    /// </summary>
    int count() const
    {
        return size;
    }

    /// <summary>
    /// Удаляет все элементы из стека.
    /// </summary>
    void clear()
    {
        while (top != nullptr)
        {
            Node* temp = top->next;
            delete top;
            top = temp;
        }
        size = 0;
    }
};
