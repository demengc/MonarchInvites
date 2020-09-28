package dev.demeng.monarchinvites.serializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListSerializer {

  public static String serialize(List<String> list) {

    final StringBuilder sb = new StringBuilder("[");

    for (int i = 0; i < list.size(); i++) {

      if (i == list.size() - 1) {
        sb.append(list.get(i)).append("]");
        return sb.toString();
      }

      sb.append(list.get(i)).append(", ");
    }

    return "[]";
  }

  public static List<String> deserialize(String string) {

    if (string.equals("[]")) {
      return Collections.emptyList();
    }

    return new ArrayList<>(Arrays.asList(string.substring(1, string.length() - 1).split(", ")));
  }
}
