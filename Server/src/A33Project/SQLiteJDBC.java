package A33Project;

import java.sql.*;
public class SQLiteJDBC {
    //负责注册与登录，访问数据库
    //TODO:判断账号和密码长度
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
        if(username!=null && password!=null) {
            try {
                PreparedStatement preStmt;
                //TODO:日后需要注意防范SQL注入
                String registerSql = "INSERT INTO USER (ID,USERNAME,PASSWORD) " +
                        "values(?,?,?)";
                String checkDuplicatedNameSql = "SELECT * FROM USER WHERE USERNAME = ?";
                preStmt = c.prepareStatement(checkDuplicatedNameSql);
                preStmt.setString(1,username);
                rs = preStmt.executeQuery();//只会用ResultSet的笨办法查重了
                if(rs.next()){
                    preStmt.close();
                    rs.close();
                    //TODO:重复用户名，做点啥
                }else{
                    preStmt = c.prepareStatement(registerSql);
                    //System.out.println("SQL语句预编译成功");
                    preStmt.setInt(1,++Last_ID);
                    preStmt.setString(2,username);
                    preStmt.setString(3,password);
                    preStmt.execute();
                    preStmt.close();
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("数据库执行异常");
            }
        }
    }
    public void login(String username,String password){
        if(username!=null && password!=null){
            try {
                Statement stmt;
                //日后需要防范SQL注入
                String sql = "SELECT USERNAME,PASSWORD FROM USER";
                String usernameInDB = null,passwordInDB = null;
                stmt = c.prepareStatement(sql);
                rs = stmt.executeQuery(sql);
                while(rs.next()){
                    usernameInDB = rs.getString("username");
                    passwordInDB = rs.getString("password");
                }//TODO:这里换成数据库内查询操作效率会更高
                if(username.equals(usernameInDB)&&password.equals(passwordInDB)){
                    //TODO:登陆成功后的操作
                }
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("数据库异常");
            }
        }

    }
}
