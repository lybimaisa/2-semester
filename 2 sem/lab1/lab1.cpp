#include <iostream>
#include <stack.h>

using namespace std;

int main()
{
    setlocale(LC_ALL, "RUS");

    Stack<int> stack;

    cout << "Введите последовательность чисел (для завершения последовательности введите 0):\n";

    while (true)
    {
        cout << "Введите число: ";
        int number;
        cin >> number;

        if (number == 0)
        {
            break;
        }

        stack.push(number);
    }

    cout << "Положительные числа в обратном порядке: ";

    while (stack.count() > 0)
    {
        int value = stack.pop();
        if (value > 0)
        {
            cout << value << " ";
        }
    }

    return 0;
}
