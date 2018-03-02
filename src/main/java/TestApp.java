import java.util.*;

import ru.tandemservice.test.task1.*;
import ru.tandemservice.test.task2.*;

public class TestApp {
    private final static int MAXSIZE = 10000;
    private final static ElementExampleImpl.Context CONTEXT = new ElementExampleImpl.Context();

    public static void main(String[] args) {
        //task1:
        List<String[]> ListForTask1 = makeListForTask1(MAXSIZE);
        display(ListForTask1);
        Task1Impl.INSTANCE.sort(ListForTask1, 0);
        display(ListForTask1);
        Task1Impl.INSTANCE.sort(ListForTask1, 1);
        display(ListForTask1);

        /////////////////////////////////////
        /////////////////////////////////////

        //task2:
        List<IElement> ListForTask2 = makeListForTask2(MAXSIZE);
        Task2Impl.INSTANCE.assignNumbers(Collections.unmodifiableList(ListForTask2));
        System.out.println("NUMBER OF OPERATIONS setupNumber()............." + CONTEXT.getOperationCount());

    }

    private static void display(List<String[]> list) {
        for (String[] r : list) {
            if (r!=null)
            for (String aR : r) {
                StringBuilder blankets = new StringBuilder();
                if (aR != null) {
                    while (aR.length() + blankets.length() <= 20)
                        blankets.append(" ");
                } else
                    while (blankets.length() <= 16)
                        blankets.append(" ");

                System.out.print("| " + aR + blankets + "|");
            }
            System.out.println();
        }
        System.out.println("*************************************************");
        System.out.println("*************************************************");
    }

    private static List<String[]> makeListForTask1(int size) {
        List<String[]> rows = new ArrayList<>();
        String[] row;
        for (int j = 0; j < (size - 7); j++) {
            row = new String[7];
            row[0] = Integer.toString(j);
            if (Integer.parseInt(row[0]) % 7 == 0) {
                row[1] = null;
                row[2] = (int) (Math.random() * 1000) + "ABCDE #" + j + j + j;
                row[6] = "";

            } else if (Integer.parseInt(row[0]) % 3 == 0) {
                row[1] = "";
            } else {
                row[1] = "a string #" + j;
                row[2] = (int) (Math.random() * 1000) + "ABCDE #" + j + j + j;
                row[6] = null;
            }
            rows.add(row);
        }
        String[] row1 = {"000", "STRING #00000", null, "ABCD", "EFGH", "", "1"};
        String[] row2 = {"001", "STR#002***ЕЕК!?**09", null, "BBCD", "FFGH", "", "0"};
        String[] row3 = {"001", "STR#002***ЕЕК!?**03", null, "BBCD", "FFGH", "", "0"};
        String[] row4 = {"010", "STRING #003", null, "BBCD", "FFGH", "", "0"};
        String[] row5 = {"000", "0006 STRING # ! @", "", "BBCD", "FFGH", "", "0"};
        String[] row6 = {"000", "STRING #005", null, "BBCD", "FFGH", "", "0"};
        String[] row7 = {"000", "STRING #001", null, "BBCD", "FFGH", "", "ЕЕЕЕЕЕЕЕЕЕЕЕ"};
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);
        rows.add(row5);
        rows.add(row6);
        rows.add(row7);


        return rows;
    }

    private static List<IElement> makeListForTask2(int size){
        List<IElement> newList = new ArrayList<>();

        Set<Integer> setWithRandomNumbers = new LinkedHashSet<>();

        while (setWithRandomNumbers.size() < size)
            setWithRandomNumbers.add((int) (Math.random() * size));
        Iterator<Integer> itr = setWithRandomNumbers.iterator();
        for (int j = 0; j < size; j++) {
            IElement theElement = new ElementExampleImpl(CONTEXT, itr.next());
            newList.add(theElement);
        }
        return newList;
    }
}
