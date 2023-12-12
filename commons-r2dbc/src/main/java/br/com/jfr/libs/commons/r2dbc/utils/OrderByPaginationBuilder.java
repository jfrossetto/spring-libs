package br.com.jfr.libs.commons.r2dbc.utils;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

public class OrderByPaginationBuilder {

  public static String buildSortAndPagination(Sort sort, int itemsPerPage, long offset) {
    StringBuilder sql = new StringBuilder()
        .append(buildSort(sort))
        .append(buildPagination(itemsPerPage, offset));
    return sql.toString();
  }

  public static String buildPagination(int itemsPerPage, long offset) {
    StringBuilder sql = new StringBuilder()
        .append(" limit ")
        .append(itemsPerPage)
        .append(" offset ")
        .append(offset);
    return sql.toString();
  }

  public static String buildSort(Sort sort) {
    StringBuilder sql = new StringBuilder();
    if (!sort.isEmpty()) {
      sql.append(" order by ");
      for (Order order : sort.toList()) {
        sql.append(" ")
            .append(order.getProperty())
            .append(" ")
            .append(order.getDirection().name())
            .append(" , ");
      }
      sql.deleteCharAt(sql.lastIndexOf(","));
    }
    return sql.toString();
  }



}
