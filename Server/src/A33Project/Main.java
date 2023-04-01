package A33Project;

import io.javalin.Javalin;

import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        double startTime = System.nanoTime();
        System.out.println("初始化数据库中...");
        try{
            if(args[0].equals("-DBPath") && args[1] != null) {
                SQLiteJDBC SQLiteJDBCObj = new SQLiteJDBC("jdbc:sqlite:" + args[1]);//指定数据库路径
                Javalin PostHandler = Javalin.create(/*config*/)
                    .post("/register", ctx -> {
                        if((Objects.equals(ctx.formParam("type"), "register"))) {
                            SQLiteJDBCObj.register(ctx.formParam("username"), ctx.formParam("password"));
                        }
                        ctx.status(201);
                    })
                    .post("/login", ctx -> {
                        if(Objects.equals(ctx.formParam("type"),"login")){
                            SQLiteJDBCObj.login(ctx.formParam("username"), ctx.formParam("password"));
                        }
                    })
                    .start(8080);
                double jsonEndTime = System.nanoTime();
                System.out.println("开启服务端后所用时间:" + (jsonEndTime - startTime) / 1000000000 + "秒");
            }else
                System.out.println("请指定正确的数据库位置！用法：java -jar A33Server.jar -DBPath 数据库文件的路径");
        }catch(ArrayIndexOutOfBoundsException exc){
            exc.printStackTrace();
            System.out.println("请输入参数！用法：java -jar A33Server.jar -DBPath 数据库文件的路径");
        }

    }
}
