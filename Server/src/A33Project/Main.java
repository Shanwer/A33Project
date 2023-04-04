package A33Project;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Main {
    //TODO:参数中加入验证码之类的来防止恶意注册
    public static void main(String[] args) throws NoSuchAlgorithmException {
        double startTime = System.nanoTime();
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        System.out.println("初始化数据库中...");
        UserUtil UserUtilObj = null;

        try{
            if(args.length>=1 && args[0].equals("-DBPath") && args[1] != null) {
                UserUtilObj = new UserUtil("jdbc:sqlite:" + args[1]);//指定数据库路径
            }else {
                UserUtilObj = new UserUtil("jdbc:sqlite:C:\\Users\\Shanwer\\Documents\\Work\\GitHub\\A33Project\\A33Project.sqlite");//默认数据库地址
                System.out.println("未指定数据库地址，使用默认地址");
            }
        }catch(ArrayIndexOutOfBoundsException exc){
            exc.printStackTrace();
            System.out.println("请输入参数！用法：java -jar A33Server.jar -DBPath 数据库文件的路径");
            System.exit(0);
        }
        Javalin app = Javalin.create(config -> {
            config.jetty.sessionHandler(SessionUtil::sqlSessionHandler);
        }).start(8080);

        UserUtil finalUserUtilObj = UserUtilObj;

        app.post("/register", ctx -> {
            if((Objects.equals(ctx.formParam("type"), "register"))) {
                boolean lengthWithinRange = Objects.requireNonNull(ctx.formParam("username")).length() < 20 &&
                        Objects.requireNonNull(ctx.formParam("password")).length() < 50 &&
                        Objects.requireNonNull(ctx.formParam("password")).length() > 8;
                if (lengthWithinRange){
                    byte[] inputByteArray = Objects.requireNonNull(ctx.formParam("password")).getBytes();
                    md5.update(inputByteArray);
                    byte[] resultByteArray = md5.digest();//对密码进行MD5加密，增强安全性
                    //System.out.println("密码加密成功");
                    if(Objects.requireNonNull(finalUserUtilObj).register(ctx.formParam("username"), byteArrayToHex(resultByteArray), ctx.ip()) == 0){
                        ctx.status(201);//创建成功
                        ctx.redirect("/login", HttpStatus.forStatus(302));
                    }else {
                        ctx.status(403);
                        ctx.header("Reason","Occupied Username!");
                    }
                } else {
                    ctx.status(403);//用户名或密码长度不符合规范 //在前端也检查长度，理论上除了黑客伪造请求外不会触发
                    System.out.println("试图伪造请求，IP为：" + ctx.ip());
                }
            }
        });

        app.post("/login", ctx -> {
            if(Objects.equals(ctx.formParam("type"),"login")) {
                String username = ctx.formParam("username");
                boolean lengthWithinRange = Objects.requireNonNull(username).length() < 20 &&
                        Objects.requireNonNull(ctx.formParam("password")).length() < 50 &&
                        Objects.requireNonNull(ctx.formParam("password")).length() > 8;
                if (lengthWithinRange) {
                    byte[] inputByteArray = Objects.requireNonNull(ctx.formParam("password")).getBytes();
                    md5.update(inputByteArray);
                    byte[] resultByteArray = md5.digest();
                    if(Objects.requireNonNull(finalUserUtilObj).login(username, byteArrayToHex(resultByteArray), ctx.ip()) == 0){
                    ctx.status(200);
                    if(ctx.req().getSession().getAttribute("user")!=null){
                        ctx.req().changeSessionId();//抵御session固定攻击
                    }else
                        ctx.sessionAttribute("user", username);
                    }else{
                        ctx.status(403);
                        ctx.header("Reason","Incorrect username or password!");
                    }
                } else {
                    ctx.status(403);//用户名或密码长度不符合规范 //在前端也检查长度，理论上除了黑客伪造请求外不会触发
                    System.out.println("试图伪造请求，IP为：" + ctx.ip());
                }
            }
        });

        app.get("/logout", ctx ->{
            ctx.req().getSession().invalidate();
            ctx.status(200);
        });
        double EndTime = System.nanoTime();
        System.out.println("开启服务端后所用时间:" + (EndTime - startTime) / 1000000000 + "秒");
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
