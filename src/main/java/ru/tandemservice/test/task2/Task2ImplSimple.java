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

    /**
     * Для переназначения номеров используется отображение theMap,
     * в которое сохраняются пары "текущий номер элемента"(ключ) - "индекс элемента в переданном списке"(значение).
     * Номера также добавляются в список для дальнейшей сортировки.
     * <p>
     * <p>
     * Во внешнем цикле мы каждый раз создаем итератор, а затем инициализируем переменную elementIndex.
     * Далее создается множество setForChains для определения цепочки номеров, образующих циклическую последовательность.
     * Это необходимо, чтобы мы смогли разбить цикличность и по цепочке от конца к началу перенумеровать элементы в списке elements.
     *
     * Этим обеспечивается выполнение условия:
     * "вызов {@code element.setNumber(i)} разрешен ⇔   ∀ e ∊ {@code elements} (e.number ≠ i).
     * В итоге у элементов, попавших в цепочку, номера будут соответствовать их расположению в списке.
     * <p>
     * Вообще, все номера делятся на две категории:
     *
     * 1) те, чьи значения текущий номер/правильный номер в списке образуют "цикл",
     * т.е. циклической последовательности чисел, полученных по правилу:
     * если мы в данный момент не можем переназначить номер для текущего элемента, т.к. новый номер, который мы хотим
     * присвоить, уже присвоен другому элементу, мы переходим к этому другому элементу, совершаем ту же самую проверку,
     * переходим к следующему элементу и т.д., пока на каком-то шаге круг не замкнется и мы не вернемся к тому элементу,
     * с которого начали. Простейшая циклическая последовательность состит из двух элементов,
     * у каждого из которых значение его номера равно индексу другого элемента, так,
     * что мы не можем переназначить номер одного элемента, не переназначив до этого номер второго,
     * а номер второго нельзя переназначить, пока не переназначен номер первого;
     * возможны циклы и большего размера, состоящие из произвольного количества элементов.
     * Чтобы разорвать подобные порочные круги, приходится на каждый из таких "циклов"
     * тратить одну дополнительную операцию setupNumber(),
     * которая присваивает временный номер (maxNumberValue+1).
     *
     * 2) те, которым посчастливилось случайно получить правильный номер, т.е. их первоначальный номер совпал с правильным.
     * Эта категория номеров также попадает во множество setForChains, но это множество всегда содержит только один такой номер,
     * т.к. цикл  while (setForChains.add(numbers.get(elementIndex))) прерывается после первой итерации.
     * Это позволяет нам обработать такие случаи особым образом, используя if...else.
     *
     *
     * <p>
     * Т.о. количество операций {@link IElement#setupNumber(int)} будет равно количеству элементов в списке,
     * чьи номера не совпадают с правильными номерами из отсортированного списка numbers, соответствующими их расположению,
     * плюс по одной дополнительной операции на каждый зацикленный граф переназначений номеров.
     * <p>
     * В худшем случае, когда всё подмножество элементов состоит из двухвершинных циклических графов,
     * потребуется (1,5*N) операций, где N - количество элементов в списке.
     * <p>
     * Алгоритм работает за О(N), т.е. линейное время.
     * <p>
     *
     * @param elements элементы, которым нужно выставить номера
     */
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

       /* Эти пять переменных нужны только для подсчета кол-ва итераций внешнего и внутренних циклов
        и количества циклических последовательностей и "счастливых номеров", т.е. номеров, которые не нужно менять,
        т.к. они правильные.
        Эти переменные можно удалить вместе с их инкрементациями и выводом результатов в консоль:
        */
        int numberOfIterationOuterWhile = 0, numberOfIterationInnerWhile = 0, numberOfIterationInnerFor = 0, numberOfCycles = 0, luckyNumbers = 0;

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
            //Во множество добавляется последовательность номеров - цикл while работает до тех пор, пока не появится повторяющийся номер:
            while (setForChains.add(numbers.get(elementIndex))) {
                //System.out.println("Только что добавили " + numbers.get(elementIndex) + ". Index = " + elementIndex);
                elementIndex = theMap.get(numbers.get(elementIndex));
                numberOfIterationInnerWhile++;
                //System.out.println("Следующее значение, которое будет добавлено " + numbers.get(elementIndex));
            }
            //Циклическая последовательность номеров состоит минимум из двух элементов, отсюда следующее условие if,
            //которое позволяет отсечь множества с одним числом (они появляются, если первоначальный номер элемента
            //оказывается верным - luckyNumber).
            if (setForChains.size() > 1) {
                System.out.println("BREAK THE CYCLE #" + (numberOfCycles++) + ". Количество номеров в циклической последовательности: " + setForChains.size());
                System.out.println("По индексу elementIndex (=" + elementIndex + ") в elements хранится значение number (это текущий номер элемента), равное " + elements.get(elementIndex).getNumber());
                System.out.println("Сейчас этому элементу будет присвоено временное значение (maxNumberValue + 1), равное " + (maxNumberValue + 1) + ", для того, чтобы разбить цикл. ");
                System.out.println("Позже (на новой итерации внешнего цикла) ему будет присвоено уже правильное значение, равное " + numbers.get(elementIndex));
                System.out.println("Оно будет взято из отсортированного списка numbers, где оно уже храниться по индексу elementIndex");
                System.out.println();
                //Собственно, разбиваем циклическую последовательность, назначая элементу временный номер, равный (maxNumberValue + 1):
                theMap.put(maxNumberValue + 1, elementIndex); //помещаем в отображение новую запись
                theMap.remove(elements.get(elementIndex).getNumber()); //удаляем старую
                elements.get(elementIndex).setupNumber(maxNumberValue + 1); //меняем номер элемента
                //Для удобства конвертируем множество в массив:
                Integer[] arrayForChains = setForChains.toArray(new Integer[setForChains.size()]);
                //Пробегаемся по массиву от конца к началу, но пока не трогаем самый первый элемент.
                //Идя по цепочке производим последовательную перенумерацию элементов (изменение номера каждого предыдущего элемента
                //позволяет нам поменять номер следующего):
                for (int j = arrayForChains.length - 1; j > 0; j--) {
                    numberOfIterationInnerFor++;
                    int num = arrayForChains[j];

                    /*Фокус заключается в том, как получить номер предыдущего элемента в последовательности
                    т.е.ключ для отображения theMap, необходимый нам для того, чтобы из этого отображения извлечь индекс.
                    Этот индекс, в свою очередь, нам нужен для того,
                    чтобы обратиться к элементу из списка elements для изменения номера этого элемента
                    методом IElement#setupNumber().*/

                    int numInElements = elements.get(numbers.indexOf(num)).getNumber();
                    elementIndex = theMap.get(numInElements);
                    theMap.remove(elements.get(elementIndex).getNumber());
                    elements.get(elementIndex).setupNumber(numbers.get(elementIndex));

                } //end for loop
                //Назначаем элементу (тому самому, которому ранее назначили временный номер (maxNumberValue + 1), чтобы разбить
                //циклическую последовательность), его правильный номер:
                elements.get(theMap.get(maxNumberValue + 1)).setupNumber(numbers.get(theMap.get(maxNumberValue + 1)));
                //Удаляем устаревшую запись из отображения:
                theMap.remove(maxNumberValue + 1);
            }//end if
            //Если нам повезло и менять номер не нужно, т.к.он правильный, мы убираем из отображения запись о нем:
            else if (entry.getKey().equals(numbers.get(elementIndex))) {
                System.out.println("NOT A CYCLE!!! IT'S LUCKY NUMBER!");
                luckyNumbers++;
                iterator.remove();
            }

        }//end outer while

        /*
        По счетчикам видно, что количество итераций внешнего цикла while и количество итераций внутреннего цикла for(),
        если их сложить, будет равно количеству элементов в переданном списке elements (т.е.N).
        Количество итераций while(setForChains.add(numbers.get(elementIndex))) всегда равно N.
        Количество циклических последовательностей и количество изначально правильных номеров - величины относительно
        случайные. В худшем и фантастически маловероятном случае весь список может состоять из двухномерных циклических последовательностей,
        разбиение каждой из которых будет стоить дополнительной операции #setupNumber (1,5*N операций в итоге, где N - размер переданного списка).
        В обычном же случае количество операции IElement#setupNumber() равно N (если быть совсем точным, то N + numberOfCycles - luckyNumbers).
        В любом случае, алгоритм работает за линейное время, Big-O Notation: O(N)
        * */
        System.out.println();
        System.out.println("NUMBER OF ITERATIONS OUTER LOOP while(!theMap.isEmpty()).." + numberOfIterationOuterWhile);
        System.out.println("NUMBER OF ITERATIONS INNER LOOP while()..................." + numberOfIterationInnerWhile);
        System.out.println("NUMBER OF ITERATIONS INNER LOOP for()....................." + numberOfIterationInnerFor);
        System.out.println("NUMBER OF CYCLES.........................................." + numberOfCycles);
        System.out.println("NUMBER OF LUCKY ELEMENTS.................................." + luckyNumbers);
    }

}
