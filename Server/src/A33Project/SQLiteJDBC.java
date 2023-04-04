package A33Project;

import java.sql.*;
public class SQLiteJDBC {
    //负责注册与登录，访问数据库
    private Connection c = null;
    private ResultSet rs;
    private static int Last_ID;

    public SQLiteJDBC(String DBPath) {
        try {
            Statement stmt;
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(DBPath);
            stmt = c.createStatement();
            String sql = "SELECT COUNT(ID) FROM USER;";//SELECT LAST_INSERT_ROWID()返回的结果不对
            rs = stmt.executeQuery(sql);
            Last_ID = rs.getInt(1);
            //System.out.println(Last_ID);
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.out.println("请指定正确的数据库位置！用法：java -jar A33Server.jar -DBPath 数据库文件的路径");
            System.exit(0);
        }
        System.out.println("数据库初始化成功！");
    }

    public int register(String username,String password) {
                PreparedStatement preStmt;
                //预编译包含？的语句可防范sql注入
                String registerSql = "INSERT INTO USER (ID,USERNAME,PASSWORD,PERMISSION) " +
                        "values(?,?,?,?)";
                /*
                String checkDuplicatedNameSql = "SELECT * FROM USER WHERE USERNAME = ?";//添加列唯一约束,原来的不需要了
                preStmt = c.prepareStatement(checkDuplicatedNameSql);
                preStmt.setString(1,username);
                rs = preStmt.executeQuery();
                if(rs.next()){
                    //检测到重复用户名
                    preStmt.close();
                    rs.close();
                */
                try {
                    preStmt = c.prepareStatement(registerSql);
                    //System.out.println("SQL语句预编译成功");
                    preStmt.setInt(1, ++Last_ID);
                    preStmt.setString(2, username);
                    preStmt.setString(3, password);
                    preStmt.setInt(4,0);
                    preStmt.execute();
                    preStmt.close();
                    rs.close();
                    return 0;
            } catch (SQLException exc) {
                    if(exc.getErrorCode()==19){
                        System.out.println("重复的用户名！用户名为："+ username +"违反唯一约束");//SQLITE_CONSTRAINT error code为19
                        Last_ID--;//否则id会注册错误
                        return 19;
                    }else {
                        exc.printStackTrace();
                        System.out.println("数据库执行异常");
                        return 1;
                    }
                }
    }
    public void login(String username,String password) {
        try {
            PreparedStatement preStmt;
            String sql = "SELECT USERNAME,PASSWORD FROM USER WHERE USERNAME IS ? AND PASSWORD IS ?";
            String usernameInDB = null, passwordInDB = null;
            preStmt = c.prepareStatement(sql);
            preStmt.setString(1, username);
            preStmt.setString(2, password);
            rs = preStmt.executeQuery(sql);
            while (rs.next()) {
                usernameInDB = rs.getString("username");
                passwordInDB = rs.getString("password");
            }
            if (username.equals(usernameInDB) && password.equals(passwordInDB)) {
                //TODO:登陆成功后的操作
            }else{

            }
            rs.close();
            preStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库异常");
        }
    }
    public void changeUsername(String username,String newUsername,String password){
        //TODO:修改用户名
    }
    public void changePassword(String username,String password,String newPassword){
        //TODO:修改密码
    }
}
