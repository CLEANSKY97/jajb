package cart;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.d_project.jajb.rpc.SecurityHandler;

@Component
public class MySecurityHandler implements SecurityHandler {

  @Override
  public void init(ServletConfig config) throws ServletException {
  }

  @Override
  public boolean isAuthorized(HttpServletRequest request,
      Map<String, Object> opts, Method targetMethod) throws ServletException {
    // allow all.
    return true;
  }
}
