package A33Project;

import io.javalin.Javalin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Main {
    //TODO:参数中加入验证码之类的来防止恶意注册
    public static void main(String[] args) throws NoSuchAlgorithmException {
        double startTime = System.nanoTime();
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        System.out.println("初始化数据库中...");
        try{
            if(args[0].equals("-DBPath") && args[1] != null) {
                SQLiteJDBC SQLiteJDBCObj = new SQLiteJDBC("jdbc:sqlite:" + args[1]);//指定数据库路径
                Javalin PostHandler = Javalin.create(/*config*/)
                    .post("/register", ctx -> {
                        if((Objects.equals(ctx.formParam("type"), "register"))) {
                            byte[] inputByteArray = Objects.requireNonNull(ctx.formParam("password")).getBytes();
                            md5.update(inputByteArray);
                            byte[] resultByteArray = md5.digest();//对密码进行MD5加密，增强安全性
                            //System.out.println("密码加密成功");
                            SQLiteJDBCObj.register(ctx.formParam("username"),byteArrayToHex(resultByteArray));
                        }
                        ctx.status(201);//创建成功
                    })
                    .post("/login", ctx -> {
                        if(Objects.equals(ctx.formParam("type"),"login")){
                            byte[] inputByteArray = Objects.requireNonNull(ctx.formParam("password")).getBytes();
                            md5.update(inputByteArray);
                            byte[] resultByteArray = md5.digest();
                            SQLiteJDBCObj.login(ctx.formParam("username"), byteArrayToHex(resultByteArray));
                        }
                        ctx.status(201);
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
    public static String byteArrayToHex(byte[] byteArray) {
        char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'A','B','C','D','E','F' };
        char[] resultCharArray =new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b>>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b& 0xf];
        }
        return new String(resultCharArray);//返回一个长度为32的字符串
    }
}
