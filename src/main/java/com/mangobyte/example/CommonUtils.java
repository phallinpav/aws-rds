package com.mangobyte.example;

import java.util.List;

public class CommonUtils {

    public static String convertList(List<List<String>> lists, String type) {
        switch (type) {
            case "text":
                return lists.toString();
            case "html":
                return CommonUtils.convertListToTableHtml(lists);
            case "markdown":
                return CommonUtils.convertListToTableMarkdown(lists);
            default:
                throw new IllegalArgumentException("Invalid type.\nPlease input 'text' or 'html' or 'markdown'.");
        }
    }

    public static String convertListToTableHtml(List<List<String>> lists) {
        if (lists.isEmpty()) {
            return "";
        }

        String fmTable = "<table style='border: 1px solid black; border-collapse: collapse'>%s</table>";
        String fmRow = "<tr>%s</tr>";
        String fmHead = "<th style='border: 1px solid black'>%s</th>";
        String fmData = "<td style='border: 1px solid black'>%s</td>";

        StringBuilder htmlTable = new StringBuilder();
        boolean isFirst = true;
        for (List<String> list : lists) {
            StringBuilder htmlRow = new StringBuilder();
            if (isFirst) {
                for (String st : list) {
                    htmlRow.append(String.format(fmHead, st));
                }
                isFirst = false;
            } else {
                for (String st: list) {
                    htmlRow.append(String.format(fmData, st));
                }
            }
            htmlTable.append(String.format(fmRow, htmlRow));
        }
        return String.format(fmTable, htmlTable);
    }

    public static String convertListToTableMarkdown(List<List<String>> lists) {
        if (lists.isEmpty()) {
            return "";
        }

        StringBuilder head = new StringBuilder();
        StringBuilder border = new StringBuilder();
        StringBuilder body = new StringBuilder();

        boolean isFirst = true;
        for (List<String> list : lists) {
            if (isFirst) {
                isFirst = false;
                head.append("| ");
                border.append("| ");
                list.forEach(item -> {
                    head.append(item);
                    head.append(" | ");
                    border.append("-".repeat(item.length()));
                    border.append(" | ");
                });
                head.append("\n");
                border.append("\n");
            } else {
                body.append("| ");
                list.forEach(item -> {
                    body.append(item);
                    body.append(" | ");
                });
                body.append("\n");
            }
        }
        return head.append(border).append(body).toString();
    }
}
