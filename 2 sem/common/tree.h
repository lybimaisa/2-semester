#pragma once
#include "list.h"
#include <iostream>

using namespace std;

/// <summary>
/// Шаблонный класс бинарного дерева поиска
/// </summary>
/// <typeparam name="T">Тип данных (должен поддерживать сравнение)</typeparam>
template <typename T>
class Tree
{
private:
    /// <summary>
    /// Шаблонный класс бинарного дерева поиска
    /// </summary>
    struct TreeNode
    {
        T data;
        TreeNode* left;
        TreeNode* right;

        /// <summary>
        /// Конструктор узла
        /// </summary>
        TreeNode(T value) : data(value), left(nullptr), right(nullptr)
        {
        }
    };

    TreeNode* root;
    int size;
    bool autoBalance;

    /// <summary>
    /// Рекурсивная очистка дерева
    /// </summary>
    void clear(TreeNode* node)
    {
        if (!node) return;
        clear(node->left);
        clear(node->right);
        delete node;
    }

    /// <summary>
    /// Рекурсивная проверка наличия элемента
    /// </summary>
    bool contains(TreeNode* node, T value) const
    {
        if (!node) return false;
        if (value == node->data) return true;
        return value < node->data ? contains(node->left, value) : contains(node->right, value);
    }

    /// <summary>
    /// Рекурсивное добавление элемента
    /// </summary>
    void add(TreeNode*& node, T value)
    {
        if (!node) {
            node = new TreeNode(value);
            size++;
        }
        else if (value < node->data)
        {
            add(node->left, value);
        }
        else if (value > node->data)
        {
            add(node->right, value);
        }
    }

    /// <summary>
    /// Рекурсивное удаление элемента
    /// </summary>
    TreeNode* remove(TreeNode* node, T value)
    {
        if (!node) return nullptr;

        if (value < node->data)
        {
            node->left = remove(node->left, value);
        }
        else if (value > node->data)
        {
            node->right = remove(node->right, value);
        }
        else
        {
            if (!node->left)
            {
                TreeNode* temp = node->right;
                delete node;
                size--;
                return temp;
            }
            else if (!node->right)
            {
                TreeNode* temp = node->left;
                delete node;
                size--;
                return temp;
            }
            else
            {
                TreeNode* minNode = findMin(node->right);
                node->data = minNode->data;
                node->right = remove(node->right, minNode->data);
            }
        }
        return node;
    }

    /// <summary>
    /// Поиск минимального элемента в поддереве
    /// </summary>
    TreeNode* findMin(TreeNode* node)
    {
        while (node->left) node = node->left;
        return node;
    }

    /// <summary>
    /// Рекурсивное преобразование дерева в список
    /// </summary>
    void toList(TreeNode* node, List<T>& list, int order) const
    {
        if (!node) return;
        if (order == Prefix)
        {
            list.add(node->data);
            toList(node->left, list, order);
            toList(node->right, list, order);
        }
        else if (order == Infix)
        {
            toList(node->left, list, order);
            list.add(node->data);
            toList(node->right, list, order);
        }
        else if (order == Postfix)
        {
            toList(node->left, list, order);
            toList(node->right, list, order);
            list.add(node->data);
        }
    }

    /// <summary>
    /// Левый поворот относительно узла
    /// </summary>
    void rotateLeft(TreeNode*& node, T value)
    {
        if (!node) return;
        if (value < node->data) rotateLeft(node->left, value);
        else if (value > node->data) rotateLeft(node->right, value);
        else if (node->right)
        {
            TreeNode* newRoot = node->right;
            node->right = newRoot->left;
            newRoot->left = node;
            node = newRoot;
        }
    }

    /// <summary>
    /// Правый поворот относительно узла
    /// </summary>
    void rotateRight(TreeNode*& node, T value)
    {
        if (!node) return;
        if (value < node->data) rotateRight(node->left, value);
        else if (value > node->data) rotateRight(node->right, value);
        else if (node->left)
        {
            TreeNode* newRoot = node->left;
            node->left = newRoot->right;
            newRoot->right = node;
            node = newRoot;
        }
    }

public:
    /// <summary>
    /// Конструктор дерева
    /// </summary>
    Tree(bool autoBalance = false) : root(nullptr), size(0), autoBalance(autoBalance)
    {
    }

    /// <summary>
    /// Добавление элемента
    /// </summary>
    void add(T value) {
        add(root, value);
    }

    /// <summary>
    /// Удаление элемента
    /// </summary>
    void remove(T value)
    {
        root = remove(root, value);
    }

    /// <summary>
    /// Очистка дерева
    /// </summary>
    void clear()
    {
        clear(root);
        root = nullptr;
        size = 0;
    }
    /// <summary>
        /// Проверка наличия элемента
        /// </summary>
    bool contains(T value) const
    {
        return contains(root, value);
    }

    /// <summary>
    /// Количество элементов
    /// </summary>
    int count() const
    {
        return size;
    }

    /// <summary>
    /// Порядок обхода дерева
    /// </summary>
    enum Order
    {
        Prefix, Infix, Postfix
    };

    /// <summary>
    /// Преобразование дерева в список по порядку
    /// </summary>
    List<T> ToList(Order order) const
    {
        List<T> list;
        toList(root, list, order);
        return list;
    }

    /// <summary>
    /// Преобразование в список (по умолчанию Infix)
    /// </summary>
    List<T> ToList() const
    {
        return ToList(Infix);
    }

    /// <summary>
    /// Левый поворот относительно узла
    /// </summary>
    void ToLeft(T value)
    {
        rotateLeft(root, value);
    }

    /// <summary>
    /// Правый поворот относительно узла
    /// </summary>
    void ToRight(T value)
    {
        rotateRight(root, value);
    }
};