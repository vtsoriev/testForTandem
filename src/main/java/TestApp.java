import java.util.ArrayList;
import java.util.List;

import ru.tandemservice.test.task1.*;

public class TestApp {
    public static void main(String[] args) {
        List<String[]> rows = new ArrayList<>();
        String[] row;
        for (int j = 0; j < 150; j++) {
            row = new String[7];
            row[0] = Integer.toString(j);
            if (Integer.parseInt(row[0]) % 7 == 0) {
                row[1] = null;
                row[2] = (int) (Math.random() * 1000) + "ABCDE №" + j + j + j;
                row[6] = "";

            } else if (Integer.parseInt(row[0]) % 3 == 0) {
                row[1] = "";
            } else {
                row[1] = "a string #" + j;
                row[2] = (int) (Math.random() * 1000) + "ABCDE №" + j + j + j;
                row[6] = null;
            }
            rows.add(row);
        }
        String[] row100 = {"000", "STRING #000", null, "ABCD", "EFGH", "", "1"};
        String[] row101 = {"000", "STRING #002", null, "BBCD", "FFGH", "", "0"};
        String[] row102 = {"000", "STRING #004", null, "BBCD", "FFGH", "", "0"};
        String[] row103 = {"000", "STRING #003", null, "BBCD", "FFGH", "", "0"};
        String[] row104 = {"000", "STRING #006", null, "BBCD", "FFGH", "", "0"};
        String[] row105 = {"000", "STRING #005", null, "BBCD", "FFGH", "", "0"};
        String[] row106 = {"000", "STRING #001", null, "BBCD", "FFGH", "", "0"};

        rows.set(100, row100);
        rows.set(101, row101);
        rows.set(102, row102);
        rows.set(103, row103);
        rows.set(104, row104);
        rows.set(105, row105);
        rows.set(106, row106);


        for (String[] r : rows) {
            for (int k = 0; k < r.length; k++)
                System.out.print("| " + r[k] + "|");
            System.out.println();

        }


        System.out.println("*************************************************");
        Task1Impl.INSTANCE.display();
        System.out.println("*************************************************");
        Task1Impl.INSTANCE.sort(rows, 1);
        Task1Impl.INSTANCE.sort(Task1Impl.sortedRows, 0);
        Task1Impl.INSTANCE.display();
        System.out.println("*************************************************");
        Task1Impl.INSTANCE.sort(rows.subList(55,130), 0);
        Task1Impl.INSTANCE.display();


    }
}
