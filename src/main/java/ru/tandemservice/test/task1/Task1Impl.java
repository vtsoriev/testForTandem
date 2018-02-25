package ru.tandemservice.test.task1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>Задание №1</h1>
 * Реализуйте интерфейс {@link IStringRowsListSorter}.
 * <p>
 * <p>Мы будем обращать внимание в первую очередь на структуру кода и владение стандартными средствами java.</p>
 */
public class Task1Impl implements IStringRowsListSorter {


    public static List<String[]> sortedRows;
    // ваша реализация должна работать, как singleton. даже при использовании из нескольких потоков.
    public static final IStringRowsListSorter INSTANCE = new Task1Impl();

    public static IStringRowsListSorter getInstance() {
        return INSTANCE;
    }

    private Task1Impl() {

    }


    @Override
    public void sort(final List<String[]> rows, final int columnIndex) {
        // "напишите здесь свою реализацию. Мы ждем от вас хорошо структурированного, документированного и понятного кода".
        // Т.к. sorted() это stateful операция, выигрыш от использования parrallelStream() не очевиден.

        sortedRows = rows.stream()
                .sorted(Comparator.comparing(r -> r[columnIndex], Comparator.nullsFirst(Task1Impl.SubStringComparator)))
                .collect(Collectors.toList());

    }

    // Метод получает в качестве аргумента строку, а возвращает массив подстрок, разбивая ее на последовательности цифр,
// которые в дальнейшем можно интерпретировать как целочисленные значения, и последовательности других знаков.
    private static String[] getSubSrings(String string) {
        List<String> subStrings = new ArrayList<>();
        String subStringWithInt = "";
        String subStringWithoutInt = "";

//Т.к. нет информации об использовании какого-либо разделителя последовательностей цифр и др.последовательностей, строковый метод
// split() использовать не получится, придется перебирать в цикле каждый символ строки и разбивать ее на подстроки в ручном режиме:
        for (int j = 0; j < string.length(); j++) {
            char ch = string.charAt(j);
            //если символ является цифрой, он добавляется к подстроке subStringWithInt,
            // которая должна в итоге представлять собой целое число после применения к ней метода Integer.parseInt в компараторе.
            if (ch >= '0' && ch <= '9')
                subStringWithInt += ch;
            //если последовательность цифр прерывается, то строка с ними добавляется в ArrayList,
            //после чего переменная очищается:
            else if (subStringWithInt.length() != 0) {
                subStrings.add(subStringWithInt);
                subStringWithInt = "";
            }
            //аналогичная логика реализуется с последовательностями других знаков (не цифр):
            if (ch > '9' || ch == ' ' || ch == '#')
                subStringWithoutInt += ch;
            else if (subStringWithoutInt.length() != 0) {
                subStrings.add(subStringWithoutInt);
                subStringWithoutInt = "";
            }
        }//end for
        //После завершения цикла в переменных subStringWithInt и subStringWithoutInt остались строки
        //их необходимо добаввить в список:
        if (subStringWithInt.length() != 0)
            subStrings.add(subStringWithInt);
        if (subStringWithoutInt.length() != 0)
            subStrings.add(subStringWithoutInt);

        return subStrings.toArray(new String[subStrings.size()]);
    }

//Для реализации правил сравнения подстрок нужен свой вариант компаратора - в анонимном классе переопределяем метод compare():
    public static Comparator<String> SubStringComparator = new Comparator<String>() {

        @Override
        public int compare(String o1, String o2) {
            //Получаем два массива подстрок:
            String[] one = Task1Impl.getSubSrings(o1);
            String[] another = Task1Impl.getSubSrings(o2);
            //Сравниваем соответствующие подстроки каждой строки между собой. При этом если обе строки состоят из цифр,
            //они интерпретируются как целочисленные значения:
            for (int j = 0; j < one.length && j < another.length; j++) {
                if (one[j].matches("[0-9]+") && another[j].matches("[0-9]+")) {
                    if (Integer.parseInt(one[j]) == Integer.parseInt(another[j]))
                        continue;
                    if (Integer.parseInt(one[j]) >= Integer.parseInt(another[j]))
                        return +1;
                    if (Integer.parseInt(one[j]) <= Integer.parseInt(another[j]))
                        return -1;
                } else if (one[j].equals(another[j]))
                    continue;
                return one[j].compareTo(another[j]);
            }
            return o1.compareTo(o2);
        }

    };


    @Override
    public void display() {
        for (String[] r : sortedRows) {
            for (int k = 0; k < r.length; k++)
                System.out.print("| " + r[k] + "|");
            System.out.println();
        }
        /*String[] splitedString;
        for (String[] r : sortedRows) {
            for (int k = 0; k < r.length; k++) {
                if (r[k] != null) {
                    splitedString = getSubSrings(r[k]);
                    System.out.print("Разбитые подстроки: ");
                    for (String splStr : splitedString) {
                        System.out.print("/" + splStr + "/");
                    }
                }
            }
            System.out.println();
        }*/

    }
}
