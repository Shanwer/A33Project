package A33Project;

import java.sql.*;
public class UserUtil {
    //负责注册与登录，访问数据库
    private Connection c = null;
    private static int Last_ID;
    static String DBPath;

    public UserUtil(String DBPath) {
        try {
            Statement stmt;
            UserUtil.DBPath = DBPath;
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(DBPath);
            stmt = c.createStatement();
            String sql = "SELECT COUNT(ID) FROM USER;";//SELECT LAST_INSERT_ROWID()返回的结果不对
            ResultSet rs = stmt.executeQuery(sql);
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

    public int register(String username, String password, String ip) {
        PreparedStatement preStmt;
        //预编译包含？的语句可防范sql注入
        String registerSql = "INSERT INTO USER (ID,USERNAME,PASSWORD,PERMISSION,IP) " +
                "values(?,?,?,?,?)";
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
            preStmt.setInt(4, 0);
            preStmt.setString(5, ip);
            preStmt.execute();
            preStmt.close();
            return 0;
        } catch (SQLException exc) {
            if (exc.getErrorCode() == 19) {
                System.out.println("该用户已存在！用户名为：" + username + "违反唯一约束");//SQLITE_CONSTRAINT error code为19
                Last_ID--;//否则id会注册错误
                return 19;
            } else {
                exc.printStackTrace();
                System.out.println("数据库执行异常");
                return 2;//确保不会出现
            }
        }
    }

    public int login(String username, String password, String ip) {
        try {
            String sql = "SELECT ID FROM USER WHERE USERNAME IS ? AND PASSWORD IS ?";
            String insertIPSql = "UPDATE USER set IP = ? WHERE ID = ?";
            PreparedStatement preStmt = c.prepareStatement(sql);
            preStmt.setString(1, username);
            preStmt.setString(2, password);
            ResultSet rs = preStmt.executeQuery();
            if (rs.next()) {//查询出来就说明用户名与密码匹配
                //TODO:登录成功，做些什么
                PreparedStatement preInsertIPStmt = c.prepareStatement(insertIPSql);
                preInsertIPStmt.setString(1, ip);
                preInsertIPStmt.setInt(1, rs.getInt(1));
                preInsertIPStmt.execute();
                preInsertIPStmt.close();
                rs.close();
                preStmt.close();
                return 0;
            }else {
                rs.close();
                preStmt.close();
                return 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库执行异常");
            return 2;
        }
    }
        public void changeUsername (String username, String newUsername, String password){
            //TODO:修改用户名
        }
        public void changePassword (String username, String password, String newPassword){
            //TODO:修改密码
        }
    }
