package ru.tandemservice.test.task1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * staff@tandemservice.ru
 * <h1>Задание №1</h1>
 * Реализуйте интерфейс {@link IStringRowsListSorter}.
 * <p>
 * <p>Мы будем обращать внимание в первую очередь на структуру кода и владение стандартными средствами java.</p>
 */
public class Task1Impl implements IStringRowsListSorter {
    // ваша реализация должна работать, как singleton. даже при использовании из нескольких потоков.
    public static final IStringRowsListSorter INSTANCE = new Task1Impl();

    private Task1Impl() {
    }

    /**
     * Сортирует переданный список записей (каждая запись - набор колонок) таблицы по указанной колонке по следующим правилам:
     * <ul>
     *  <li>в колонке могут быть null и пустые значения - строки с null-значениями должны быть первыми, затем строки с пустым значением, затем все остальные,</li>
     *  <li>строка бьется на подстроки следующим образом: выделяем непрерывные максимальные фрагменты строки, состоящие только из цифр, и считаем набором подстрок эти фрагменты и все оставшиеся от такого разбиения фрагменты строки</li>
     *  <li>при сравнении строк осуществляется последовательное сравнение их подстрок до первого несовпадения,</li>
     *  <li>если обе подстроки состоят из цифр - то при сравнении они интерпретируются как целые числа (вначале должно идти меньшее число), в противном случае - как строки,</li>
     *  <li>сортировка должна быть устойчива к исходной сортировке списка - т.е., если строки (в контексте указанных правил сравнения) неразличимы, то сортировка не должна менять их местами.</li>
     * </ul>
     *
     * @param rows список, элементами которого являются массивы строк. Длины массивов должны быть одинаковыми,
     *             длины содержащихся в них строк могут быть произвольными.
     *
     * @param columnIndex номер колонки, по которой будет производиться сортировка,
     *                    т.е. индекс элемента массива от 0 до (длина массива - 1)
     *
     *
     *
     * @throws  ArrayIndexOutOfBoundsException
     *          При наличии в списке массивов разной длины и сортировке по той колонке (т.е. индексу массива),
     *          которая есть не у всех элементов списка.
     *
     * @throws  NullPointerException
     *          При наличии в списке элемента со значением null.
     *
     * @throws  IllegalArgumentException
     *          При получении null в качестве первого аргумента метода.
     *          При получении в качестве второго аргумента метода значения, не попадающего в диапазон индексов элементов,
     *          который определяется размером первого массива в списке.
     *
     *
     */

    @Override
    public synchronized void sort(final List<String[]> rows, final int columnIndex) {
        if (null == rows)
            throw new IllegalArgumentException("Value passing to the method sort() parameter rows is not valid! It must be not null!");
        if (columnIndex < 0 || columnIndex >= rows.get(0).length)
            throw new IllegalArgumentException("Value passing to the method sort() parameter сolumnIndex is not valid! Check for valid parameter arguments.");


        rows.sort(Comparator.comparing(r -> r[columnIndex], Comparator.nullsFirst(Task1Impl.SubStringComparator)));

    }


    /**
     * Метод получает в качестве аргумента строку, а возвращает массив подстрок, разбивая ее на последовательности,
     * а) состоящие из цифр, которые в дальнейшем можно интерпретировать как целочисленные значения, и
     * б) последовательности других знаков.
     *
     * @param string произвольная строка, которая при наличии в ней цифр будет разбита на подстроки.
     *
     * @return  массив подстрок, полученных в результате разбиения.
     *          Если разбиения не произошло (в переданной строке не было ни одного символа цифры),
     *          в массиве будет только один элемент - изначальная строка переданная в аргументе.
     *
     * @throws  NullPointerException
     *          При получении null в качестве аргумента метода.
     *
     */


    private static String[] getSubStrings(String string) {
        List<String> subStrings = new ArrayList<>();
        StringBuilder subStringWithInt = new StringBuilder();
        StringBuilder subStringWithoutInt = new StringBuilder();
        //Т.к. нет информации об использовании какого-либо разделителя последовательностей цифр и др.последовательностей, строковый метод
        // split() использовать не получится, придется перебирать в цикле каждый символ строки и разбивать ее на подстроки в ручном режиме:
        for (int j = 0; j < string.length(); j++) {
            Character ch = string.charAt(j);
            //если символ является цифрой, он добавляется к подстроке subStringWithInt,
            // которая должна в итоге представлять собой целое число после применения к ней метода Integer.parseInt в компараторе.
            if (ch >= '0' && ch <= '9')
                subStringWithInt.append(ch);
                //если последовательность цифр прерывается, то строка с ними добавляется в ArrayList,
                //после чего переменная очищается:
            else if (subStringWithInt.length() != 0) {
                subStrings.add(subStringWithInt.toString());
                subStringWithInt = new StringBuilder();
            }
            //аналогичная логика реализуется с последовательностями других знаков (не цифр):
            if (ch > '9' || ch == ' ' || ch == '#')
                subStringWithoutInt.append(ch);
            else if (subStringWithoutInt.length() != 0) {
                subStrings.add(subStringWithoutInt.toString());
                subStringWithoutInt = new StringBuilder();
            }
        }//end for
        //После завершения цикла в переменных subStringWithInt и subStringWithoutInt остались строки
        //их необходимо добаввить в список:
        if (subStringWithInt.length() != 0)
            subStrings.add(subStringWithInt.toString());
        if (subStringWithoutInt.length() != 0)
            subStrings.add(subStringWithoutInt.toString());

        return subStrings.toArray(new String[subStrings.size()]);
    }

    //Для реализации правил сравнения подстрок нужен свой вариант компаратора - в лямбда-вырыжении переопределяем метод compare():
    private static Comparator<String> SubStringComparator = (o1, o2) -> {
        //Получаем два массива подстрок:
        String[] one = Task1Impl.getSubStrings(o1);
        String[] another = Task1Impl.getSubStrings(o2);
        //Сравниваем соответствующие подстроки каждой строки между собой. При этом если обе строки состоят из цифр,
        //они интерпретируются как целочисленные значения:
        for (int j = 0; j < one.length && j < another.length; j++) {
            if (one[j].matches("[0-9]+") && another[j].matches("[0-9]+")) {
                if (Integer.parseInt(one[j]) > Integer.parseInt(another[j]))
                    return +1;
                if (Integer.parseInt(one[j]) < Integer.parseInt(another[j]))
                    return -1;
            } else if (!one[j].equals(another[j]))
                return one[j].compareTo(another[j]);
        }
        return o1.compareTo(o2);
    };
}
