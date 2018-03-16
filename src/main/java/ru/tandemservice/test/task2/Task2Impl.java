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
public class Task2Impl implements IElementNumberAssigner {

    // ваша реализация должна работать, как singleton. даже при использовании из нескольких потоков.

    public static final IElementNumberAssigner INSTANCE = new Task2Impl();

    private Task2Impl() {
    }


    /**
     * Метод выставляет номера {@link IElement#setupNumber(int)}
     * для элементов коллекции {@code elements}
     * в порядке следования элементов в коллекции.
     * <p>
     * <p>
     * <p>
     * При этом обеспечиваются слеюущие условия:<ul>
     * <li>
     * <p>метод работает только с существующими элементами (не создает новых),</p>
     * </li>
     * <li>
     * <p>на протяжении всей работы метода обеспечивается уникальность номеров элементов:</p>
     * <p>вызов {@code element.setNumber(i)} разрешен ⇔   ∀ e ∊ {@code elements} (e.number ≠ i),</p>
     * </li>
     * <li>
     * <p>метод устойчив к передаче в него в качестве параметра {@link java.util.Collections#unmodifiableList(List)} и любой другой реализации immutable-list,</p>
     * </li>
     * <li>
     * <p>метод должен работать за «приемлемое» время ({@link IElement#setupNumber(int)} - трудоемкая операция и пользоваться ей надо рационально)</p>
     * </li>
     * </ul>
     * Во внешнем цикле мы перебираем записи отображения, проверяя, содержится ли число индекса элемента
     * (он сохранен в Map в качестве значения) во множестве сохраненных в отображении ключей и если его там нет
     * (этой проверкой обеспечивается обеспечивается условие:
     * "вызов {@code element.setNumber(i)} разрешен ⇔   ∀ e ∊ {@code elements} (e.number ≠ i)),
     * мы переназначаем элементу номер, вызывая дорогостоящую операцию {@link IElement#setupNumber(int)}
     * - в итоге у данного элемента номер будет совпадать с его индексом в списке
     * и на новой итерации он уже не попадает в отображение.
     * <p>
     * Если же мы не можем в данной итерации переназначить номер, элемент ждет, когда для номера его индекса освободится место во множестве.
     * После переназначения номеров для всех элементов, чьи значения текущий номер/индекс в списке не образуют "цикла",
     * т.е. циклической последовательности чисел, полученных по правилу:
     * если мы в данный момент не можем переназначить номер для текущего элемента, т.к. новый номер, который мы хотим
     * присвоить, уже присвоен другому элементу, мы переходим к этому другому элементу, совершаем ту же самую проверку,
     * переходим к следующему элементу и т.д., пока на каком-то шаге круг не замкнется и мы не вернемся к тому элементу,
     * с которого начали. Простейшая циклическая последовательность состит из двух элементов,
     * у каждого из которых значение его номера равно индексу другого элемента, так,
     * что мы не можем переназначить номер одного элемента, не переназначив до этого номер второго,
     * а номер второго нельзя переназначить, пока не переназначен номер первого;
     * возможны циклы и большего размера, состоящие из произвольного количества элементов.
     * <p>
     * Чтобы разорвать подобные порочные круги (а после завершения первоначальных обходов,
     * когда последний из них уже не смог переназначить номер ни для одного из элементов,
     * в отображении theMap остаются только данные о элементах, чьи номера и индексы образуют циклы),
     * приходится на каждый из таких "циклов" тратить одну дополнительную операцию setupNumber(),
     * которая присваивает временный номер (если мы знаем, что для нумерации используются только положительные числа,
     * то в качестве временного номера можно использовать значение -1).
     * <p>
     * Т.о. количество операций {@link IElement#setupNumber(int)} будет равно количеству элементов в списке,
     * чьи номера не совпадают с порядком их расположения (т.е. с индексом списка),
     * плюс по одной дополнительной операции на каждый зацикленный граф переназначений номеров.
     * <p>
     * В худшем случае, когда всё подмножество элементов состоит из двухвершинных циклических графов,
     * потребуется (1,5*N) операций, где N - количество элементов, требующих перенумерации,
     * а не общее количество элементов в списке.
     * <p>
     * Т.о., если пренебречь другими операциями, то алгоритм работает за О(N),
     * т.е. линейное время.
     * <p>
     * Так, обработка списка с 1 млн. элементов, номера которых лежат в диапазоне 0 до 999 000 включительно,
     * и присвоены случаным образом с помощью Math.random(), потребовала 1 млн. + 9 операций setupNumber(int).
     * (а также ок. 500 000 итераций внешнего цикла)
     * <p>
     * <p>
     * Если же посчитать другие операции, то во внутреннем цикле мы бегаем по множеству entrySet
     * (размер которого в худшем случае совпадает с общим количеством элементов в переданном методу списке),
     * проверяя с помощью метода Map#containsKey() (константное время) каждую запись.
     * <p>
     * Т.к. для случайных данных мы не можем предсказать,
     * сколько в среднем за один проход будет перенумеровано элементов, то константы мы учесть не можем,
     * но в Big-O Notation  мы получаем O(N*N) количества сравнений.
     * <p>
     * То есть общее время T = K*N + Q*N*N,
     * где K - константа, определяющая время выполнения операции IElement#setupNumber,
     * а Q - константа, обозначающая время выполнения Map#containsKey().
     * <p>
     * Если К = 100*Q, то T = 100*Q*N + Q*N*N,
     * и для N < 100 рост затрат при увеличении N будет почти линейным
     * <p>
     * Например:
     * для N = 10, T = 1100*Q,
     * для N = 20, T = 2400*Q,
     * для N = 100, T = 20 000*Q. На этой точке графики функций пересекутся, после чего рост затрат будет определять f(N*N).
     * <p>
     * Соответственно, этот алгоритм целесообразно применять, пока N <= K/Q.
     * <p>
     * В противном случае дешевле перенумеровать каждый элемент дважды,
     * что даст (при К = 100*Q) T = 200*Q*N,
     * и если N> 100, например, для N=1000, Т=200 000*Q,
     * тогда как исходный алгоритм даст 1 100 000*Q.
     *
     * @param elements элементы, которым нужно выставить номера
     */

    @Override
    public synchronized void assignNumbers(final List<IElement> elements) {
        for (IElement element : elements) {
            System.out.println("Index: " + elements.indexOf(element) + ". Number: " + element.getNumber());
        }

        /*
          Т.к. изначально коллекция не содержит элементов, номера которых повторяются, номера элементов,
          хранящиеся в поле ElementExampleImpl#number, можно сохранить в качестве ключей в отображении,
          значениям же присваивать индекс элемента в переданном в качестве аргумента метода списке.
          Для сортировки номеров можно использовать ArrayList.

         */

        Map<Integer, Integer> theMap = new HashMap<>();
        List<Integer> numbers = new ArrayList<>(elements.size());

        // В цикле пробегаемся по переданному нам списку, заполняя отображение и список с номерами:
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



       /* Эти три переменные нужны только для подсчета кол-ва итераций внешнего и внутреннего цикла
        и количества циклических последовательностей.
        Их можно удалить:
        */
        int numberOfIterationOuterWhile = 0, numberOfIterationInnerWhile = 0, numberOfCycles = 0;

        // Внешний цикл выполняется до тех пор, пока отображение не станет пустым
        // (т.е. у каждого элемента списка elements, переданного в качестве аргумента, с индексом n
        // номера будут совпадать со значением, хранящемся в отсортированном списке numbers по этому же индексу n):

        Set<Map.Entry<Integer, Integer>> entrySet = theMap.entrySet();



        while (!theMap.isEmpty()) {
            numberOfIterationOuterWhile++;

            Iterator<Map.Entry<Integer, Integer>> iterator = entrySet.iterator();
            int count = theMap.size();

            while (iterator.hasNext()) {
                numberOfIterationInnerWhile++;
                Map.Entry<Integer, Integer> entry = iterator.next();
                int listIndex = entry.getValue();

                if (entry.getKey().equals(numbers.get(listIndex))) {
                    System.out.println("Текущий номер элемента совпал с тем, который он и должен иметь по порядку");
                    System.out.println("Index: " + entry.getValue() + ". Number: " + entry.getKey());
                    iterator.remove();
                    continue;
                }

                /*
                с помощью следующего выражения if обеспечивается условие
                "вызов {@code element.setNumber(i)} разрешен ⇔   ∀ e ∊ {@code elements} (e.number ≠ i)"
                т.е. что вызов element.setNumber(i) выполняется тогда и только тогда, когда для всех элементов e, принадлежащих elements, e.number не равняется i.

                */
                if (!theMap.containsKey(numbers.get(listIndex))) {
                    System.out.println("Переназначаем номер! Было: " + elements.get(listIndex).getNumber() + " Стало: " + numbers.get(listIndex));
                    //Меняем номер элемента:
                    elements.get(listIndex).setupNumber(numbers.get(listIndex));
                    //Удаляем запись об элементе:
                    iterator.remove();
                }
            }
            //Если во внутреннем цикле while при последней итерации ни одна запись не была удалена из отображения,
            // то переменная count будет равна текущему размеру отображения theMap. Это сигнализирует о том,
            // что в отображении остались только зацикленные последовательности чисел
            // (а они там остались, т.к. отображение не пустое), для разбиения которых требуется дополнительная
            // операция перенумерации (для временного номера используется значение -1,
            // т.к. мы исходим из того, что изначально номера могут быть только положительными числами):
            if (count == theMap.size()) {
                //System.out.println("\nWE HAVE CYCLES IN THE GRAPH!!!!");
                iterator = entrySet.iterator();
                Map.Entry<Integer, Integer> entry = iterator.next();
                int currentElementIndex = entry.getValue();

                System.out.println("BREAK THE CYCLE #" + numberOfCycles++);
                theMap.put(maxNumberValue + 1, currentElementIndex);
                theMap.remove(elements.get(currentElementIndex).getNumber());
                elements.get(currentElementIndex).setupNumber(maxNumberValue + 1);
            }
        }

        for (IElement element : elements) {
            System.out.println("Index: " + elements.indexOf(element) + ". Number: " + element.getNumber());
        }
        System.out.println("NUMBER OF ITERATIONS while(!theMap.isEmpty()).." + numberOfIterationOuterWhile);
        System.out.println("NUMBER OF ITERATIONS while(iterator.hasNext()).." + numberOfIterationInnerWhile);

        System.out.println("NUMBER OF CYCLES..............................." + numberOfCycles);
    }

}
