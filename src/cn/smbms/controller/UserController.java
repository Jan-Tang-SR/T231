package cn.smbms.controller;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.mysql.jdbc.StringUtils;

import cn.smbms.pojo.Role;
import cn.smbms.pojo.User;
import cn.smbms.service.role.RoleService;
import cn.smbms.service.user.UserService;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;

@Controller
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@RequestMapping("/jsp/frame")
	public String showFrame() {
		return "jsp/frame";
	}

	@RequestMapping("/jsp/logout.do")
	public String logout(HttpServletRequest request) {
		request.getSession().removeAttribute(Constants.USER_SESSION);
//		response.sendRedirect(request.getContextPath()+"/login.jsp");
		return "redirect:/login";
	}

	@RequestMapping("/jsp/userquery")
	public String userQuery(@RequestParam(value = "queryname", required = false) String queryUserName,
			@RequestParam(value = "queryUserRole", required = false) String temp,
			@RequestParam(value = "pageIndex", required = false) String pageIndex, HttpServletRequest request) {
		// 查询用户列表
		int queryUserRole = 0;
		List<User> userList = null;
		// 设置页面容量
		int pageSize = Constants.pageSize;
		// 当前页码
		int currentPageNo = 1;
		/**
		 * http://localhost:8090/SMBMS/userlist.do ----queryUserName --NULL
		 * http://localhost:8090/SMBMS/userlist.do?queryname= --queryUserName ---""
		 */
		System.out.println("queryUserName servlet--------" + queryUserName);
		System.out.println("queryUserRole servlet--------" + queryUserRole);
		System.out.println("query pageIndex--------- > " + pageIndex);
		if (queryUserName == null) {
			queryUserName = "";
		}
		if (temp != null && !temp.equals("")) {
			queryUserRole = Integer.parseInt(temp);
		}

		if (pageIndex != null) {
			currentPageNo = Integer.valueOf(pageIndex);
		}
		// 总数量（表）
		int totalCount = userService.getUserCount(queryUserName, queryUserRole);
		// 总页数
		PageSupport pages = new PageSupport();
		pages.setCurrentPageNo(currentPageNo);
		pages.setPageSize(pageSize);
		pages.setTotalCount(totalCount);

		int totalPageCount = pages.getTotalPageCount();

		// 控制首页和尾页
		if (currentPageNo < 1) {
			currentPageNo = 1;
		} else if (currentPageNo > totalPageCount) {
			currentPageNo = totalPageCount;
		}
		userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
		request.setAttribute("userList", userList);
		List<Role> roleList = null;
		roleList = roleService.getRoleList();
		request.setAttribute("roleList", roleList);
		request.setAttribute("queryUserName", queryUserName);
		request.setAttribute("queryUserRole", queryUserRole);
		request.setAttribute("totalPageCount", totalPageCount);
		request.setAttribute("totalCount", totalCount);
		request.setAttribute("currentPageNo", currentPageNo);
		return "jsp/userlist";
	}

	@RequestMapping(value = "/jsp/useradd", method = RequestMethod.POST)
	public String userAdd(User user,  MultipartFile useridpic, MultipartFile userworkpic,
			HttpServletRequest request) {
		System.out.println("add()================");

		user.setCreationDate(new Date());
		user.setCreatedBy(((User) request.getSession().getAttribute(Constants.USER_SESSION)).getId());
	    String savePath = request.getServletContext().getRealPath("idpics");
	    String idpic = uploadFile(useridpic, savePath);
	    if(idpic != null) {
			user.setIdpic(idpic);
		}
        String workpic = uploadFile(userworkpic, savePath);
		if(workpic != null) {
			user.setWorkpic(workpic);
		}

		if (userService.add(user)) {
			return "redirect:userquery";
		} else {
			return "jsp/useradd";
		}
	}
//	@RequestMapping(value="/jsp/useradd",method=RequestMethod.POST)
//	public String userAdd(User user,
//			@RequestParam("userpic")
//			MultipartFile[] userpic,HttpServletRequest request) {
//		System.out.println("add()================");
//		
//		user.setCreationDate(new Date());
//		user.setCreatedBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
//		int i = 0;
//	   String savePath = request.getServletContext().getRealPath("idpics");
//		for(MultipartFile mulFile : userpic) {
//			String newName = uploadFile(mulFile,savePath);
//			if(newName != null){
//	                  if(i == 0) {
//	                	  user.setIdpic(newName);
//	                  }
//	                  else {
//	                	  user.setWorkpic(newName);
//	                  }
//				}
//		}
//		
//		
//		
//		
//		if(userService.add(user)){
//			return "redirect:userquery";
//		}else{
//			return "jsp/useradd";
//		}
//	}

	@RequestMapping(value = "jsp/usermodify/{id}.html", method = RequestMethod.GET)
	public String usermodify(@PathVariable("id") String id, HttpServletRequest request) {
		if (!StringUtils.isNullOrEmpty(id)) {
			// 调用后台方法得到user对象
			User user = userService.getUserById(id);
			request.setAttribute("user", user);
			return "jsp/usermodify";
		}
		return "redirect:userquery";
	}

	@RequestMapping(value = "jsp/usermodify", method = RequestMethod.POST)
	public String usermodify(User user, HttpServletRequest request) {

		user.setModifyBy(((User) request.getSession().getAttribute(Constants.USER_SESSION)).getId());
		user.setModifyDate(new Date());

		if (userService.modify(user)) {
			return "redirect:userquery";
		} else {
			return "jsp/usermodify";
		}
	}
/**
 * 上传文件并在成功后返回新文件名
 * @param multiFile
 * @param savePath
 * @return
 */
	private String uploadFile(MultipartFile multiFile, String savePath) {
		if (!multiFile.isEmpty()) {
			// 获取用户上传的文件的文件名
			String fileName = multiFile.getOriginalFilename();
			// 获取文件的后缀
			String suffix = fileName.substring(fileName.lastIndexOf("."));
			long size = multiFile.getSize(); // 获取文件的大小（字节）
			// 在此处判断后缀是否符合要求
			Random ran = new Random();
			// 使用随机数+当前时间毫秒数+后缀 生成新的文件名
			String newName = ran.nextInt(1000000) + "" + System.currentTimeMillis() + suffix;
			// 根据保存路径和新文件名创建一个用于保存的文件对象
			File saveFile = new File(savePath, newName);

			try {
				multiFile.transferTo(saveFile);
				return newName;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			
		}
		return null;
	}

	@RequestMapping("/jsp/checkUserCode")
	@ResponseBody
	public Object checkUserCode(String userCode) {
		 User user = userService.selectUserCodeExist(userCode);
		 Map<String,Object> map = new HashMap<String,Object>();
		 if(user != null) { //用户已存在
			 map.put("userCode", "exist");
//			 String jsonStr = "{userCode:'exist'}";
//			 return jsonStr;
		 }else {
			 map.put("userCode","notexist");
//			 String jsonStr = "{userCode:'notexist'}";
//			 return jsonStr;
		 }
		 
		return  JSON.toJSONString(map);
	}
	
	//produces 用于设置ajax响应编码
	@RequestMapping(value="/jsp/viewUser"
			,method=RequestMethod.POST
			)
	@ResponseBody
	public Object viewUser(String userid) {
		User user = userService.getUserById(userid);
		
		//如果使用了FastJSON提供的消息转换器，则直接返回对象
		//无需转换
		return user;
		//return JSON.toJSONString(user);
		//在转换时处理日期类型
		//return JSON.toJSONStringWithDateFormat(user,"yyyy-MM-dd");
	}
	@RequestMapping(value="/jsp/getrolelist",method=RequestMethod.GET)
	@ResponseBody
	public Object getRoleList() {
		return roleService.getRoleList();
	}
	@RequestMapping(value="/jsp/checkoldpwd",method=RequestMethod.GET)
	@ResponseBody
	public Object checkoldpwd(String oldpassword,HttpServletRequest request) {
		User user = (User)request.getSession().getAttribute(Constants.USER_SESSION);
		Map<String,String> map = new HashMap<String,String>();
		if(user == null) {//未登录或session过期无法获取当前用户信息
			map.put("result", "sessionerror");
		}else	if(oldpassword==null ||"".equals(oldpassword)) { //密码未输入
			map.put("result","error");
		}else	if(user.getUserPassword().equals(oldpassword)) {
			map.put("result","true"); //密码一致
		}else {
			map.put("result", "false"); //密码不一致
		}
		return map;
	}
	 
	
	
	
}
