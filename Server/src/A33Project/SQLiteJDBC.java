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
            System.exit(0);
        }
        System.out.println("数据库初始化成功！");
    }

    public void register(String username,String password) {
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
            } catch (SQLException exc) {
                    if(exc.getCause() instanceof SQLIntegrityConstraintViolationException){
                        System.out.println("重复的用户名！违反唯一约束");
                        //TODO:违反USERNAME字段唯一约束,do something
                    }else {
                        exc.printStackTrace();
                        System.out.println("数据库执行异常");
                    }
                }
    }
    public void login(String username,String password){
            try {
                PreparedStatement preStmt;
                String sql = "SELECT USERNAME,PASSWORD FROM USER WHERE USERNAME IS ? AND PASSWORD IS ?";
                String usernameInDB = null,passwordInDB = null;
                preStmt = c.prepareStatement(sql);
                preStmt.setString(1,username);
                preStmt.setString(2,password);
                rs = preStmt.executeQuery(sql);
                while(rs.next()){
                    usernameInDB = rs.getString("username");
                    passwordInDB = rs.getString("password");
                }
                if(username.equals(usernameInDB)&&password.equals(passwordInDB)){
                    //TODO:登陆成功后的操作
                }
                rs.close();
                preStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("数据库异常");
            }
    }
}
