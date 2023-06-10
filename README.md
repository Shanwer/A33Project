# A33Project
---
# 2022服务外包创新大赛A33题目，由于时间不足之类的因素而弃坑  
---
### 默认管理员用户  
用户名:admin  
密码:shanwer666 （明文）  
权限等级1  
### 默认新注册用户  
权限等级0  
### 后端API  
/register 注册  _post请求体: {"type":"register","username": "xxx", "password": "xxx"}_  
/login 登录  _post请求体: {"type":"login","username": "xxx", "password": "xxx"}_  
/logout 登出 get请求，销毁session
