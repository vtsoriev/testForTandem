package ru.tandemservice.test.task2;

import java.util.*;

/**
 * <h1>Задание №2</h1>
 * Реализуйте интерфейс {@link IElementNumberAssigner}.
 * <p>
 * <p>Помимо качества кода, мы будем обращать внимание на оптимальность предложенного алгоритма по времени работы
 * с учетом скорости выполнения операции присвоения номера:
 * большим плюсом (хотя это и не обязательно) будет оценка числа операций, доказательство оптимальности
 * или указание области, в которой алгоритм будет оптимальным.</p>
 */
public class Task2ImplSimple implements IElementNumberAssigner {

    // ваша реализация должна работать, как singleton. даже при использовании из нескольких потоков.

    public static final IElementNumberAssigner INSTANCE = new Task2ImplSimple();

    private Task2ImplSimple() {
    }

    @Override
    public synchronized void assignNumbers(final List<IElement> elements) {
        /*
          Т.к. изначально коллекция не содержит элементов, номера которых повторяются, номера элементов,
          хранящиеся в поле ElementExampleImpl#number, можно сохранить в качестве ключей в отображении,
          значениям же присваивать индекс элемента в переданном в качестве аргумента метода списке.
          Для сортировки номеров можно использовать ArrayList.
        */
        Map<Integer, Integer> theMap = new HashMap<>();
        List<Integer> numbers = new ArrayList<>(elements.size());
        /*
          В цикле пробегаемся по переданному нам списку, заполняя отображение парами "номер элемента" (ключ)
          - "индекс элемента в переданном списке"(значение). Номера кроме этого добавляем еще и в список numbers,
          для последующей сортировки:
        */
        for (IElement element : elements) {
            int number = element.getNumber();
            theMap.put(number, elements.indexOf(element));
            numbers.add(number);
        }
        // Сортируем список с номерами:
        numbers.sort(Integer::compareTo);
        // Сохраняем в переменную максимальное значение, которое принимает поле номер в переданном списке
        // (оно нам понадобится для назначения временного номера элементу, чтобы разбить "зацикленную" последовательность номеров):
        int maxNumberValue = numbers.get(numbers.size() - 1);

       /* Эти четыре переменные нужны только для подсчета кол-ва итераций внешнего и внутреннего цикла
        и количества циклических последовательностей.
        Их можно удалить:
        */
        int numberOfIterationOuterWhile = 0, numberOfIterationInnerFor = 0, numberOfCycles = 0, luckyNumbers = 0;

       /* System.out.println();
        for (IElement element : elements) {
            System.out.println("Index: " + elements.indexOf(element) + ". Number: " + element.getNumber() + " Proper number: " + numbers.get(elements.indexOf(element)));
        }*/

        //Создаем множество записей из отображения, чтобы у нас была возможность итерации по ним:
        Set<Map.Entry<Integer, Integer>> entrySet = theMap.entrySet();
        // Внешний цикл while выполняется до тех пор, пока отображение не станет пустым
        // (т.е. у каждого элемента списка elements, переданного в качестве аргумента, с индексом n
        // номера будут совпадать со значением, хранящемся в отсортированном списке numbers по этому же индексу n):
        while (!theMap.isEmpty()) {
            numberOfIterationOuterWhile++;
            // Создаем итератор:
            Iterator<Map.Entry<Integer, Integer>> iterator = entrySet.iterator();
            Map.Entry<Integer, Integer> entry = iterator.next();
            int elementIndex = entry.getValue();

            Set<Integer> setForChains = new LinkedHashSet<>();

            System.out.println();
            while (setForChains.add(numbers.get(elementIndex))) {
                //System.out.println("Только что добавили " + numbers.get(elementIndex) + ". Index = " + elementIndex);
                elementIndex = theMap.get(numbers.get(elementIndex));
                //System.out.println("Следующее значение, которое будет добавлено " + numbers.get(elementIndex));
            }
            //Циклическая последовательность номеров состоит минимум из двух элементов, отсюда условие if,
            //которое позволяет отсечь множества с одним числом, которые появляются, если первоначальный номер элемента
            //оказывается верным.
            if (setForChains.size() > 1) {
                System.out.println("BREAK THE CYCLE #" + (numberOfCycles++) + ". Количество номеров в циклической последовательности: " + setForChains.size());
                System.out.println("По индексу elementIndex (=" + elementIndex + ") в elements хранится значение number (это текущий номер элемента), равное " + elements.get(elementIndex).getNumber());
                System.out.println("Сейчас этому элементу будет присвоено временное значение (maxNumberValue + 1), равное " + (maxNumberValue + 1) + ", для того, чтобы разбить цикл. ");
                System.out.println("Позже (на новой итерации внешнего цикла) ему будет присвоено уже правильное значение, равное " + numbers.get(elementIndex));
                System.out.println("Оно будет взято из отсортированного списка numbers, где оно уже храниться по индексу elementIndex");
                System.out.println();

                theMap.put(maxNumberValue + 1, elementIndex);
                theMap.remove(elements.get(elementIndex).getNumber());
                elements.get(elementIndex).setupNumber(maxNumberValue + 1);

                Integer[] arrayForChains = setForChains.toArray(new Integer[setForChains.size()]);
                for (int j = arrayForChains.length - 1; j > 0; j--) {
                    numberOfIterationInnerFor++;
                    int num = arrayForChains[j];
                    // System.out.println("Номер, по которому мы можем получить индекс, который этот номер имеет в списке numbers " + num);
                    // System.out.println("Индекс в numbers, который имеет элемент num " + numbers.indexOf(num));

                    /*Фокус заключается в том, как получить номер предыдущего элемента в последовательности
                    (т.е.ключ для отображения theMap, необходимый нам для того, чтобы из этого отображения извлечь индекс.
                    Этот индекс, в свою очередь, нам нужен для того,
                    чтобы обратиться к элементу из списка elements для изменения номера этого элемента
                    методом IElement#setupNumber().*/

                    int numInElements = elements.get(numbers.indexOf(num)).getNumber();
                    elementIndex = theMap.get(numInElements);
                    theMap.remove(elements.get(elementIndex).getNumber());
                    elements.get(elementIndex).setupNumber(numbers.get(elementIndex));

                } //end for loop
                elements.get(theMap.get(maxNumberValue + 1)).setupNumber(numbers.get(theMap.get(maxNumberValue + 1)));
                theMap.remove(maxNumberValue + 1);
            }//end if
            else if (entry.getKey().equals(numbers.get(elementIndex))) {
                System.out.println("NOT A CYCLE!!! IT'S LUCKY NUMBER!");
                luckyNumbers++;
                iterator.remove();
            }

        }//end outer while
        System.out.println();
        System.out.println("NUMBER OF ITERATIONS OUTER LOOP while(!theMap.isEmpty()).." + numberOfIterationOuterWhile);
        System.out.println("NUMBER OF ITERATIONS INNER LOOP for()....................." + numberOfIterationInnerFor);
        System.out.println("NUMBER OF CYCLES.........................................." + numberOfCycles);
        System.out.println("NUMBER OF LUCKY ELEMENTS.................................." + luckyNumbers);
    }

}
