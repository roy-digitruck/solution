package pack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pack.IO.readLine;
import static pack.IO.readLineAsInteger;

public class Solution implements Runnable {
    @Override
    public void run() {
        int testCases = readLineAsInteger();
        for (int cs = 1; cs <= testCases; ++cs) {
            List<Table> tableList = new ArrayList<>();
            this.readTables(tableList);

            int nQueries = readLineAsInteger();
            for (int q = 0; q < nQueries; q++) this.executeQuery(tableList);
        }
    }

    private void executeQuery(List<Table> tableList) {
        QueryExecutor qe = new QueryExecutor();
        qe.allTables = new HashMap<>();
        for (Table t : tableList) {
          try {
            qe.allTables.put(t.name, (Table) t.clone());
          }
          catch (CloneNotSupportedException e) {
            e.printStackTrace();
          }
        }
        qe.queryString = new StringBuilder(readLine());//-> read the first line of the query

        String input = "(__ DUMMY STRING __)";
        while (!input.equals("")) {//-> read other lines of a query until a newline is found
            input = readLine();
            qe.queryString.append("\n");//-> append all the lines separating using newline
            qe.queryString.append(input);//-> appending other lines into query string
        }
        this.generateOutput(qe);
    }

    private void generateOutput(QueryExecutor qe) {
        qe.parseQuery();
        System.out.println();
    }

    private void readTables(List<Table> tableList) {
        int nTables = readLineAsInteger();
        for (int i = 0; i < nTables; i++) {
            Table table = new Table();
            table.name = readLine();

            String[] rowColumnInput = readLine().split(" ");
            table.nColumns = Integer.parseInt(rowColumnInput[0]);
            table.nRows = Integer.parseInt(rowColumnInput[1]);
            table.columnList.addAll(Arrays.asList(readLine().split(" ")));

            for (int r = 0; r < table.nRows; r++) {
                List<String> row = Arrays.asList(readLine().split(" "));
                table.rowList.add(row);
            }

            tableList.add(table);
        }
    }
}


class Table {
    public String name;
    public Integer nRows;
    public Integer nColumns;

    public List<String> columnList = new ArrayList<>();
    public List<List<String>> rowList = new ArrayList<>();

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}

class QueryExecutor {
    public StringBuilder queryString = new StringBuilder();
    public Table outputTable = new Table();
    public Map<String, Table> allTables = new HashMap<>();
    public List<String> outputColumnList = new ArrayList<>();

    //    SELECT *
    //    FROM <first_table_name>
    //    JOIN <second_table_name>
    //    ON <first_table_name>.<a name of the column from the first table> = <second_table_name>.<a name of the column of the second table>
    public void parseQuery() {
        String[] queryLines = this.queryString.toString().split("\n");//-> splitting all input fields using newline
        if (queryLines.length > 0) {
            String selectLine = queryLines[0];//-> first line contains all the fields need to show on output
            //-> splitting all fields need to show on output:
            this.outputColumnList = Arrays.asList(selectLine.substring("SELECT ".length()).split(","));
        }

        String fromLine = queryLines[1];
        String[] partsAfterFromClause = fromLine.substring("FROM ".length()).split(" ");

        String tableName = partsAfterFromClause[0];
        Table mainTable = this.allTables.get(tableName);
        allTables.put(tableName, mainTable);
        if (partsAfterFromClause.length == 2) {//-> there is an alias:
            String alias = partsAfterFromClause[1];
            allTables.put(alias, mainTable);
        }
        //    SELECT *
        //    FROM <first_table_name>
        //    JOIN <second_table_name>
        //    ON <first_table_name>.<a name of the column from the first table> = <second_table_name>.<a name of the column of the second table>
        for (int i = 2; i < queryLines.length; i += 2) {//-> all the lines after index 1 will be join table:
            String[] partsAfterJoinClause = queryLines[i].substring("JOIN ".length()).split(" ");

            String joinTableName = partsAfterJoinClause[0];
            if (partsAfterJoinClause.length > 1) {
                String alias = partsAfterJoinClause[1];
                allTables.put(alias, allTables.get(joinTableName));
            }

            String[] partsAfterOnClause = queryLines[i + 1].substring("ON ".length()).split(" = ");

            String[] leftSide = partsAfterOnClause[0].split("\\.");
            String[] rightSide = partsAfterOnClause[1].split("\\.");

            String leftTable = leftSide[0];
            String leftColumn = leftSide[1];

            String rightTable = rightSide[0];
            String rightColumn = rightSide[1];
        }
    }
}
