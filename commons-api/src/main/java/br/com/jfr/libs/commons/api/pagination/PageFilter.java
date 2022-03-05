package br.com.jfr.libs.commons.api.pagination;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

public class PageFilter {

  private static final int DEFAULT_PAGE_INDEX = 1;
  private static final int DEFAULT_PAGE_SIZE = 10;
  private static final String DEFAULT_SEARCH = "";
  public static final String REGEX_ORDER_BY = "^(\\w+(\\+|\\s)(asc|desc)(,\\w+(\\+|\\s)(asc|desc))*)*$";

  @Min(value = 1, message = "pageSize must be greater than zero.")
  @Max(value = 100, message = "PageSize must be less than 100.")
  private Integer pageSize;

  @Min(value = 1, message = "pageIndex must be greater than zero.")
  private Integer pageIndex;

  @Pattern(
      regexp = REGEX_ORDER_BY,
      message = "Order by do not respect the pattern field1+asc,field2+desc...")
  private String orderBy;

  private String search;

  public PageFilter() {
    this.pageSize = DEFAULT_PAGE_SIZE;
    this.pageIndex = DEFAULT_PAGE_INDEX;
    this.search = DEFAULT_SEARCH;
  }

  public Pageable toPageable() {
    return Optional.ofNullable(orderBy)
        .map(this::buildSort)
        .map(sort -> PageRequest.of(pageIndex - 1, pageSize, sort))
        .orElseGet(() -> PageRequest.of(pageIndex - 1, pageSize));
  }

  private Sort buildSort(String sort) {
    if (sort == null || sort.trim().length() == 0) {
      return Sort.unsorted();
    }
    List<Order> orderList =
        Arrays.stream(sort.split(","))
            .map(
                element -> {
                  String[] split = element.split("\\s|\\+");
                  return new Order(Direction.valueOf(split[1].toUpperCase()), split[0]);
                })
            .collect(Collectors.toList());

    return Sort.by(orderList);
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public Integer getPageIndex() {
    return pageIndex;
  }

  public void setPageIndex(Integer pageIndex) {
    this.pageIndex = pageIndex;
  }

  public String getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }

  public String getSearch() {
    return search;
  }

  public void setSearch(String search) {
    this.search = search;
  }

}
