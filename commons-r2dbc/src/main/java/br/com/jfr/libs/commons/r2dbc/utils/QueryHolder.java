package br.com.jfr.libs.commons.r2dbc.utils;

import java.util.Map;

public class QueryHolder {

  private String sqlQuery;
  private Map<String, Object> mapParams;

  public QueryHolder() {
  }

  public QueryHolder(String sqlQuery, Map<String, Object> mapParams) {
    this.sqlQuery = sqlQuery;
    this.mapParams = mapParams;
  }

  public String getSqlQuery() {
    return sqlQuery;
  }

  public void setSqlQuery(String sqlQuery) {
    this.sqlQuery = sqlQuery;
  }

  public Map<String, Object> getMapParams() {
    return mapParams;
  }

  public void setMapParams(Map<String, Object> mapParams) {
    this.mapParams = mapParams;
  }
}
