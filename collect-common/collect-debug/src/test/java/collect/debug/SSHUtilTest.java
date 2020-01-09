package collect.debug;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.debug.ssh.SSHUtil;
import com.common.collect.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by hznijianfeng on 2018/11/12.
 */

@Slf4j
public class SSHUtilTest {

    private static String remote_host;
    private static Integer debug_port;
    private static String classPath = SSHUtilTest.class.getResource("/").getPath();

    static {

        String app = "app";
        String machineIp = app + ".env.ip";
        String port = app + ".debug_port";

        Properties properties = new Properties();
        try {
            classPath = classPath + "app_machine.properties";
            properties.load(new FileInputStream(classPath));
        } catch (IOException e) {
            throw UnifiedException.gen("属性文件获取失败", e);
        }
        remote_host = properties.getProperty(machineIp);
        debug_port = Integer.valueOf(properties.getProperty(port));

        log.info("classPath:{}", classPath);
        log.info("machineIp:{}", machineIp);
        log.info("app:{}", app);
        log.info("remote_host:{}", remote_host);
        log.info("debug_port:{}", debug_port);

    }

    public static void main(String[] args) throws Exception {

        forward();

        System.exit(0);

    }

    private static void forward() throws Exception {
        SSHUtil.SSHInfo sshInfo = new SSHUtil.SSHInfo(remote_host, "hznijianfeng");
        sshInfo.setPort(1046);
        sshInfo.setLocalForward(true);
        sshInfo.setLocalPort(11111);
        sshInfo.setRemotePort(debug_port);
        sshInfo.getSession();

        boolean needHold = true;
        while (needHold) {
            ExceptionUtil.eatException(() -> Thread.sleep(1000 * 60 * 50), false);
            needHold = false;
        }
    }

}
