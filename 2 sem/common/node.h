#pragma once

/// <summary>
/// Шаблонная структура узла для работы с однонаправленным списком.
/// </summary>
/// <typeparam name="T">Тип данных, который будет храниться в узле.</typeparam>
template <typename T>
struct Node
{
    T data;
    Node* next;

    /// <summary>
    /// Конструктор узла, принимает данные и указатель на следующий узел.
    /// </summary>
    Node(T value, Node* nextNode = nullptr)
        : data(value), next(nextNode)
    {
    }
};