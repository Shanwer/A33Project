#使用pip install -r requirements.txt安装依赖
from pywebio import start_server
from pywebio.input import *
from pywebio.output import *
import requests
import json

url = 'http://127.0.0.1:1111'  # 服务器地址

def register():
    put_markdown("# 注册")
    loginUI = input_group("注册", [
    input('账号',name = 'usernameUI', type=TEXT),    #将组中用于判断的账号密码设为xxxUI以便于区分
    input('密码',name = 'passwordUI', type=PASSWORD)
    ], validate=checklimit)
    username = loginUI['usernameUI']            #将组中值赋予组外用于数据库传递
    password = loginUI['passwordUI']
    data = {"type": "register", "username": username, "password": password}
    response = requests.post(url, data=json.dumps(data))
    if response.status_code == 200:
        toast("注册成功", color="success")
    else:
        toast("注册失败", color="error")

def login():
    put_markdown("# 登录")
    loginUI = input_group("登录", [
    input('账号',name = 'usernameUI', type=TEXT),    #将组中用于判断的账号密码设为xxxUI以便于区分
    input('密码',name = 'passwordUI', type=PASSWORD)
    ], validate=checklimit)
    username = loginUI['usernameUI']            #将组中值赋予组外用于数据库传递
    password = loginUI['passwordUI']
    data = {"type": "login", "username": username, "password": password}
    response = requests.post(url, data=json.dumps(data))
    if response.status_code == 200:
        toast("登录成功", color="success")
    else:
        toast("登录失败", color="error")

def checklimit(loginUI):#判断用户名和密码长度是否符合要求 
    if len(loginUI['usernameUI']) > 20 or len(loginUI['usernameUI']) == 0:
        return ('usernameUI','用户名长度应该在1~20字符之间，请重新输入')
    if len(loginUI['passwordUI']) > 50 or len(loginUI['passwordUI']) < 8:
        return ('passwordUI','密码长度应该在8~50字符之间，请重新输入')

def main():
    put_markdown("# 用户登录和注册")
    while True:
        choice = actions("请选择操作：", ['注册', '登录'])
        if choice == '注册':
            register()
        elif choice == '登录':
            login()

if __name__ == '__main__':
    start_server(main, debug=True,port=25446)
