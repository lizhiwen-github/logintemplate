package com.lzw.web.servlet;
import com.alibaba.fastjson.JSON;
import com.lzw.entity.User;
import com.lzw.service.UserService;
import com.lzw.utils.CheckCodeUtil;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Classname UserServlet
 * @Description TODO
 * @Version 1.0.0
 * @Created by 李志文
 * @Date 2022/1/27 17:37
 */
@WebServlet("/user/*")
public class UserServlet  extends BaseServlet{
    private String codeString;
    /**
     * @param request 请求对象
     * @param response 响应对象
     * @return void
     * @author 李志文
     * @date 2022/1/28 10:45
     * @description: TODO 检查用户名个密码是否正确或者用户是否存在
     */
    public void selectUsernameAndPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        String jsonUser = request.getParameter("user");
        String checkValue = request.getParameter("checkValue");//单选框的value值
        User user = JSON.parseObject(jsonUser, User.class);
        String loginName=user.getUsername();//用户输入的用户名
        User user1 = UserService.selectUsernameAndPassword(user);
        String username=user1.getUsername();
        String password=user1.getPassword();
        User user2 = UserService.selectByName(loginName);
        if(user2==null){
            response.getWriter().write("用户不存在");
        }else if(user1!=null){
            //将用户名和密码存放在session中
            HttpSession session=request.getSession();
            session.setAttribute("user",user1);
            Cookie cookieUsername = null;
            Cookie cookiePwd=null;
            Cookie cookieCheckCode=new Cookie("checkValue",checkValue);
            cookieCheckCode.setMaxAge(60*60*24*30);
            response.addCookie(cookieCheckCode);
            if("true".equals(checkValue)){
                //用户点击了，记住密码，将用户名密码保存在cookie中
               cookieUsername=new Cookie("username",username);
                cookiePwd=new Cookie("password",password);
                cookieUsername.setMaxAge(60*60*24*30);
                cookiePwd.setMaxAge(60*60*24*30);
                response.addCookie(cookieUsername);
                response.addCookie(cookiePwd);
            }
            //跳转到登录页面
            response.getWriter().write("登录成功");
        }else{
            response.getWriter().write("登录失败");
        }
    }
    /**
     * @param request 请求对象
     * @param response 响应对象
     * @return void
     * @author 李志文
     * @date 2022/3/2 22:49
     * @description: TODO 判断是否记住密码
     */
    public void isRememberPwd(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        Cookie[] cookies = request.getCookies();
        Map<String, String> map=new HashMap<>();
        for(Cookie cookie:cookies){
            if("checkValue".equals(cookie.getName())){
               map.put("checkValue",cookie.getValue());
            }else if("username".equals(cookie.getName())){
                map.put("username",cookie.getValue());
            }else if("password".equals(cookie.getName())){
                map.put("password",cookie.getValue());
            }
        }
        String jsonString = JSON.toJSONString(map);
        response.getWriter().write(jsonString);

    }
    /**
     * @param request 请求对象
     * @param response 响应对象
     * @return void
     * @author 李志文
     * @date 2022/1/28 10:44
     * @description: TODO  注册时检查用户名是否已存在
     */
    public void selectByUsernameForRegister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
       request.setCharacterEncoding("utf-8");
       response.setContentType("text/html;charset=utf-8");
        String name = request.getParameter("username");
//        System.out.println("name="+name);
        User user = UserService.selectByName(name);
        if(user!=null){
            response.getWriter().write("该用户名已被注册");
        }
    }
    /**
     * @param request 请求对象
     * @param response 响应对象
     * @return void
     * @author 李志文
     * @date 2022/1/28 11:47
     * @description: TODO 通过检查用户名，来判断用户是否存在
     */
    public void selectByUsernameForForgetPwd(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        String name = request.getParameter("name");
        User user = UserService.selectByName(name);
        if(user==null) {
            response.getWriter().write("fail");
        }
    }
    /**
     * @param request 请求对象
     * @param response 响应对象
     * @return void
     * @author 李志文
     * @date 2022/1/29 10:50
     * @description: TODO 忘记密码之后更新密码
     */
    public void updatePassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        String jsonString = request.getReader().readLine();
        User user = JSON.parseObject(jsonString,User.class);
        System.out.println("修改："+user);
        UserService.updateUsername(user);
        response.getWriter().write("success");

    }
    /**
     * @param request 请求对象
     * @param response 响应对象
     * @return void
     * @author 李志文
     * @date 2022/1/29 10:51
     * @description: TODO 获取验证码
     */
    public void checkImageCode(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        //获取字节输出流，用于输出验证码
        //获取验证码，显示到注册页面上去
        ServletOutputStream outputStream = response.getOutputStream();
        //获取验证字节输出流
        codeString = CheckCodeUtil.outputVerifyImage(100, 50, outputStream, 4);
        //将该参数存入到session中
//        HttpSession session=request.getSession();
//        session.setAttribute("codeString",codeString);
//        System.out.println("99998888");

    }
    /**
     * @param request 请求对象
     * @param response 响应对象
     * @return void
     * @author 李志文
     * @date 2022/1/29 10:51
     * @description: TODO 用户注册
     */
    public void registerUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        String username = request.getParameter("username");
        String password=request.getParameter("password");
        User user=new User();
        user.setUsername(username);
        user.setPassword(password);
        System.out.println(user);
        String code = request.getParameter("verificationCode");//用户输入的验证码
//        HttpSession session=request.getSession();
//        String codeString = session.getAttribute("codeString").toString();
//        System.out.println(codeString);
//        System.out.println("112");
        //验证码不区分大小写
        if(codeString.equals(code.toUpperCase())||codeString.equals(code)){
            UserService.registerUser(user);
            response.getWriter().write("注册成功");
        }else{
            response.getWriter().write("验证码错误");
        }
    }
}

