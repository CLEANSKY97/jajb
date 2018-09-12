package com.d_project.jajb.rpc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.d_project.jajb.DefaultJSONParserHandler;
import com.d_project.jajb.JSONParser;
import com.d_project.jajb.JSONParserHandler;

public class RPCServlet extends HttpServlet {
  @Override
  protected void doPost(
    HttpServletRequest request,
    HttpServletResponse response
  ) throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    JSONParserHandler handler = new DefaultJSONParserHandler(ServiceRequest.class);
    JSONParser parser = new JSONParser(request.getReader(), handler);
    parser.parseAny();
    
  }
}