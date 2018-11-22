package com.cds.iot.data;

public class BaseResp<T> {

  /**
   * data : {"user_id":21}
   * info : {"code":"5007","title":"确认解除该设备？","info":"解除后，您将无法使用该设备，管理员权限将转移到顺位第一个用户"}
   */

  private T data;
  private BaseInfo info;

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public BaseInfo getInfo() {
    return info;
  }

  public void setInfo(BaseInfo info) {
    this.info = info;
  }
}
