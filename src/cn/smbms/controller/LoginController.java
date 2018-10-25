package cn.smbms.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.smbms.pojo.User;
import cn.smbms.service.user.UserService;
import cn.smbms.service.user.UserServiceImpl;
import cn.smbms.tools.Constants;

@Controller
public class LoginController {
	@Autowired
	private UserService userService;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	@RequestMapping("/login")
	public String login() {
		return "login";
	}
	@RequestMapping(value="/login.do",method=RequestMethod.POST)
	public String doLogin(String userCode,String userPassword ,
			HttpServletRequest request) throws Exception {
		System.out.println("login ============ " );
		//调用service方法，进行用户匹配	
		User user = userService.login(userCode,userPassword);
		if(null != user){//登录成功
			//放入session
			request.getSession().setAttribute(Constants.USER_SESSION, user);
			//页面跳转（frame.jsp）
//			response.sendRedirect("jsp/frame.jsp");
//SpringMVC重定向只能重定向到Controller方法，redirect:controller的Mapping			
			return "redirect:jsp/frame";
		}else{
			//页面跳转（login.jsp）带出提示信息--转发
		//	request.setAttribute("error", "用户名或密码不正确");
			//request.getRequestDispatcher("login.jsp").forward(request, response);
		//	return "login";
			throw new Exception("用户名密码不正确");
		}
	}
	
//	@ExceptionHandler //当该类中Controller方法出现异常时，调用此方法处理
//	public String exception(Exception ex,HttpServletRequest request) {
//		request.setAttribute("error", ex.getMessage());
//		return "error";
//	}
	
	
	
	
}




