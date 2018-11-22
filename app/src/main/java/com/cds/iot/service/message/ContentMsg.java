package com.cds.iot.service.message;

public class ContentMsg {
  private String title;

  private DetailMsg detail;

  private String tail;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public DetailMsg getDetail() {
    return detail;
  }

  public void setDetail(DetailMsg detail) {
    this.detail = detail;
  }

  public String getTail() {
    return tail;
  }

  public void setTail(String tail) {
    this.tail = tail;
  }
}
