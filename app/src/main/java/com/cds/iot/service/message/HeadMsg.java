package com.cds.iot.service.message;

public class HeadMsg {
  /**
   * msg_id :
   * msg_type : 1
   * user_id : 26
   * token : 5284047F4FFB4E04824A2FD1D1F0CD62
   */

  private String msg_id;
  private String msg_type;
  private String user_id;
  private String token;

  public HeadMsg(String msg_id, String msg_type, String user_id, String token) {
    this.msg_id = msg_id;
    this.msg_type = msg_type;
    this.user_id = user_id;
    this.token = token;
  }

  public String getUser_id() {
    return user_id;
  }

  public void setUser_id(String user_id) {
    this.user_id = user_id;
  }

  public String getMsg_type() {
    return msg_type;
  }

  public void setMsg_type(String msg_type) {
    this.msg_type = msg_type;
  }

  public String getMsg_id() {
    return msg_id;
  }

  public void setMsg_id(String msg_id) {
    this.msg_id = msg_id;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
