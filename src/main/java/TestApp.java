import java.util.*;

import ru.tandemservice.test.task1.*;
import ru.tandemservice.test.task2.*;

public class TestApp {
    private final static int MAXSIZE = 10000;

    public static void main(String[] args) {
        //task1:
        List<String[]> rows = makeListForTest(150);
        display(rows);
        Task1Impl.INSTANCE.sort(rows, 0);
        display(rows);
        Task1Impl.INSTANCE.sort(rows, 2);
        display(rows);

        /////////////////////////////////////
        /////////////////////////////////////

        //task2:
        List<IElement> newList = new ArrayList<>();
        ElementExampleImpl.Context context = new ElementExampleImpl.Context();
        Set<Integer> setWithRandomNumbers = new LinkedHashSet<>();

        while (setWithRandomNumbers.size() < MAXSIZE)
            setWithRandomNumbers.add((int) (Math.random() * MAXSIZE));
        Iterator<Integer> itr = setWithRandomNumbers.iterator();
        for (int j = 0; j < MAXSIZE; j++) {
            IElement theElement = new ElementExampleImpl(context, itr.next());
            newList.add(theElement);
        }
        Task2Impl.INSTANCE.assignNumbers(Collections.unmodifiableList(newList));
        System.out.println("NUMBER OF OPERATION setupNumber().............." + context.getOperationCount());

    }

    private static void display(List<String[]> list) {
        for (String[] r : list) {
            for (String aR : r) {
                String blankets = "";
                if (aR != null) {
                    while (aR.length() + blankets.length() <= 20)
                        blankets += " ";
                } else
                    while (blankets.length() <= 16)
                        blankets += " ";

                System.out.print("| " + aR + blankets + "|");
            }
            System.out.println();
        }
        System.out.println("*************************************************");
        System.out.println("*************************************************");
    }

    private static List<String[]> makeListForTest(int size) {
        List<String[]> rows = new ArrayList<>();
        String[] row;
        for (int j = 0; j < size; j++) {
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
        String[] row2 = {"000", "STRING #002", null, "BBCD", "FFGH", "", "0"};
        String[] row3 = {"000", "STRING #004", null, "BBCD", "FFGH", "", "0"};
        String[] row4 = {"010", "STRING #003", null, "BBCD", "FFGH", "", "0"};
        String[] row5 = {"000", "STRING #006", null, "BBCD", "FFGH", "", "0"};
        String[] row6 = {"000", "STRING #005", null, "BBCD", "FFGH", "", "0"};
        String[] row7 = {"000", "STRING #001", null, "BBCD", "FFGH", "", "0"};
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);
        rows.add(row5);
        rows.add(row6);
        rows.add(row7);

        return rows;
    }
}
