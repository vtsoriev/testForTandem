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
public class Task2ImplOldVersion implements IElementNumberAssigner {

    // ваша реализация должна работать, как singleton. даже при использовании из нескольких потоков.

    public static final IElementNumberAssigner INSTANCE = new Task2ImplOldVersion();

    private Task2ImplOldVersion() {
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
     * <p>
     * Во внешнем цикле мы перебираем записи отображения, проверяя, содержится ли новый номер элемента
     * (он сохранен в отсортированном списке numbers в ячейке с индексом, совпадающем с индексом элемента в списке elements)
     * во множестве сохраненных в отображении ключей. Этой проверкой обеспечивается выполнение условия:
     * "вызов {@code element.setNumber(i)} разрешен ⇔   ∀ e ∊ {@code elements} (e.number ≠ i).
     * Если его там нет мы переназначаем элементу номер, вызывая дорогостоящую операцию {@link IElement#setupNumber(int)}
     * - в итоге у данного элемента номер будет соответствовать его расположению в списке.
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
     * чьи номера не совпадают с номерами, соответствующими их расположению,
     * плюс по одной дополнительной операции на каждый зацикленный граф переназначений номеров.
     * <p>
     * В худшем случае, когда всё подмножество элементов состоит из двухвершинных циклических графов,
     * потребуется (1,5*N) операций, где N - количество элементов, требующих перенумерации,
     * а не общее количество элементов в списке, что может быть важно, если данные частично упорядочены.
     * <p>
     * Т.о., если пренебречь другими операциями, то алгоритм работает за О(N), т.е. линейное время.
     * <p>
     * Так, обработка списка с 1 млн. элементов, номера которых  присвоены случаным образом с помощью Math.random(),
     * потребовала 1 млн. + 18 операций setupNumber(int), т.к. было 19 циклических последовательностей перенумерации.
     * Итераций внешнего цикла while(!theMap.isEmpty()) было 39,
     * а внутреннего while(iterator.hasNext()) - 5 295 188. Вообще, этот внутренний while дает количество итераций от
     * 3N до 10N
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
        int numberOfIterationOuterWhile = 0, numberOfIterationInnerWhile = 0, numberOfIterationInnerFor = 0, numberOfCycles = 0;

        //Создаем множество записей из отображения, чтобы у нас была возможность итерации по ним:
        Set<Map.Entry<Integer, Integer>> entrySet = theMap.entrySet();

        // Внешний цикл while выполняется до тех пор, пока отображение не станет пустым
        // (т.е. у каждого элемента списка elements, переданного в качестве аргумента, с индексом n
        // номера будут совпадать со значением, хранящемся в отсортированном списке numbers по этому же индексу n):

        while (!theMap.isEmpty()) {
            numberOfIterationOuterWhile++;
            // Создаем итератор:
            Iterator<Map.Entry<Integer, Integer>> iterator = entrySet.iterator();
            // Эта переменная-счетчик нужна нам для того, чтобы узнать, была ли удалена хотя бы одна пара ключ-значение из
            // theMap и во внутреннем цикле while (далее count будет сравниваться с актуальным размером theMap)
            int count = theMap.size();


            //Внутренний цикл while
            while (iterator.hasNext()) {
                numberOfIterationInnerWhile++;
                Map.Entry<Integer, Integer> entry = iterator.next();
                int listIndex = entry.getValue();

                if (entry.getKey().equals(numbers.get(listIndex))) {
                    System.out.print("Номер элемента совпал с тем, который он и должен иметь по порядку: ");
                    System.out.println("Index: " + entry.getValue() + ". Number: " + entry.getKey());
                    System.out.println();
                    //Если текущий номер элемента совпал с тем, который он и должен иметь по порядку,
                    // то просто удаляем запись из о нем из отображения и начинаем новую итерацию цикла:
                    iterator.remove();
                    continue;
                }

                /*
                с помощью следующего выражения if обеспечивается условие
                "вызов {@code element.setNumber(i)} разрешен ⇔   ∀ e ∊ {@code elements} (e.number ≠ i)"
                т.е. что вызов element.setNumber(i) выполняется тогда и только тогда, когда для всех элементов e, принадлежащих elements, e.number не равняется i.
                */
                if (!theMap.containsKey(numbers.get(listIndex))) {
                    //System.out.println("Переназначаем номер! Было: " + elements.get(listIndex).getNumber() + " Стало: " + numbers.get(listIndex));
                    //Меняем номер элемента:
                    elements.get(listIndex).setupNumber(numbers.get(listIndex));
                    //Удаляем запись об элементе из отображения:
                    iterator.remove();
                }
            }//end inner while


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

                /*
                Чтобы узнать, какие и сколько элементов образуют циклическую последовательность номеров,
                будем добавлять номера, хранящиеся по индексу currentElementIndex, во множество до тех пор,
                пока они не начнут повторяться, т.е. Set#add() не вернет false. При этом следующее значение currentElementIndex
                определяется с помощью извлечения из theMap значения (напомним, в качестве значений там хранятся индексы
                элементов в первоначальном списке, переданном нам в качестве аргумента метода) по ключу, совпадающему
                с номером, который хранится в списке numbers в ячейке с индексом currentElementIndex.
                */
                Set<Integer> setForCycles = new LinkedHashSet<>();
                for (; setForCycles.add(numbers.get(currentElementIndex)); currentElementIndex = theMap.get(numbers.get(currentElementIndex))) {
                    // System.out.println("Adding in set. number = " + numbers.get(currentElementIndex) + "/ Index = " + currentElementIndex);
                }
                System.out.println("Adding in set has finished. Last number = " + numbers.get(currentElementIndex) + "/ Index = " + currentElementIndex);

                /*
                На этом этапе множество setForCycles содержит в себе все номера, образующие цикл. Преобразуем множество в массив:
                */
                Integer[] arrayForCycles = setForCycles.toArray(new Integer[setForCycles.size()]);
                System.out.println("Последнее значение " + arrayForCycles[arrayForCycles.length - 1]);

                /*
                Теперь нам необходимо разомкнуть циклическую последовательность номеров. Это можно сделать,
                присвоив элементу из List<IElement> elements с номером, сохраненным в arrayForCycles[0],
                значение (maxValue+1) - этого значения гарантированно нет ни у одного из элементов List<IElement> elements,
                т.к. оно на единицу больше максимального значения, которое есть у поля ElementExampleImpl#number в переданном списке.
                */
                System.out.println();
                System.out.println("BREAK THE CYCLE #" + numberOfCycles++);
                System.out.println("По индексу currentElementIndex (=" + currentElementIndex + ") в elements хранится значение number (это текущий номер элемента), равное " + elements.get(currentElementIndex).getNumber());
                System.out.println("Сейчас этому элементу будет присвоено временное значение (maxNumberValue + 1), равное " + (maxNumberValue + 1) + ", для того, чтобы разбить цикл. ");
                System.out.println("Позже (на новой итерации внешнего цикла) ему будет присвоено уже правильное значение, равное " + numbers.get(currentElementIndex));
                System.out.println("Оно будет взято из отсортированного списка numbers, где оно уже храниться по индексу currentElementIndex");
                System.out.println();

                /*
                Помещаем в отображение новый ключ (maxNumberValue + 1) со значением currentElementIndex,
                затем удаляем из отображения устаревшую пару ключ(старый номер данного элемнта)-значение(его индекс в elements).
                После чего нужно назначить временный номер для данного элемента в elements, т.е. присвоить ему номер (maxNumberValue + 1).
                Эти действия разомкнут циклическую последовательность номеров.
                */

                theMap.put(maxNumberValue + 1, currentElementIndex);
                theMap.remove(elements.get(currentElementIndex).getNumber());
                elements.get(currentElementIndex).setupNumber(maxNumberValue + 1);

                /*
                В принципе, мы могли бы больше ничего тут не делать, позволив начаться новой итерации внешнего цикла.
                Но мы можем попытаться сократить количество итераций внутреннего цикла while()
                (без оптимизации оно равно приблизительно количеству итераций внешнего цикла, возведенному в степень 2),
                чтобы он не пробегал все отображение только ради того, чтобы найти в среднем пару элементов,
                номера которых на данной итерации можно поменять не нарушив правилаЖ
                "вызов {@code element.setNumber(i)} разрешен ⇔   ∀ e ∊ {@code elements} (e.number ≠ i)".
                Этого можно добиться, если перенумеровать последовательность, которая ранее (до нашего разбиения)
                образовывала цикл, "в ручную". Мы сохранили эту последовательность в массиве arrayForCycles,
                при этом этом тот элемент, номер которого был сохранен в ячейке 0, уже получил новый номер со значением (maxNumberValue + 1),
                поэтому перенумеровывать мы должны от конца массива к его началу и уже не трогая нулевую ячеку.
                Для этого мы используем внутренний цикл for,
                количество итераций которого никогда не превышает размер переданного в метод списка:
                */
                for (int j = arrayForCycles.length - 1; j > 0; j--) {
                    numberOfIterationInnerFor++;
                    int num = arrayForCycles[j];
                    //System.out.println("Номер, по которому мы можем получить индекс, который этот номер имеет в списке numbers " + num);
                    //System.out.println("Индекс в numbers, который имеет элемент num " + numbers.indexOf(num));
                    /*
                    Фокус заключается в том, как получить номер предыдущего элемента в последовательности
                    (т.е.ключ для отображения theMap, необходимый нам для того, чтобы из этого отображения извлечь индекс.
                    Этот индекс, в свою очередь, нам нужен для того,
                    чтобы обратиться к элементу из списка elements для изменения номера этого элемента
                    методом IElement#setupNumber().
                    */
                    int numInElements = elements.get(numbers.indexOf(num)).getNumber();
                    currentElementIndex = theMap.get(numInElements);
                    theMap.remove(elements.get(currentElementIndex).getNumber());
                    elements.get(currentElementIndex).setupNumber(numbers.get(currentElementIndex));
                } //end for loop

                elements.get(theMap.get(maxNumberValue + 1)).setupNumber(numbers.get(theMap.get(maxNumberValue + 1)));
            }//end if (count == theMap.size())

        }//end outer while

        System.out.println("NUMBER OF ITERATIONS OUTER LOOP while(!theMap.isEmpty()).." + numberOfIterationOuterWhile);
        System.out.println("NUMBER OF ITERATIONS INNER LOOP while(iterator.hasNext())." + numberOfIterationInnerWhile);
        System.out.println("NUMBER OF ITERATIONS INNER LOOP for()....................." + numberOfIterationInnerFor);
        System.out.println("NUMBER OF CYCLES.........................................." + numberOfCycles);
    }

}