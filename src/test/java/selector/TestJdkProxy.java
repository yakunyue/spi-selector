/**
 * 佛祖镇楼                  BUG辟易
 * 佛曰:
 * 写字楼里写字间，写字间里程序员；
 * 程序人员写程序，又拿程序换酒钱。
 * 酒醒只在网上坐，酒醉还来网下眠；
 * 酒醉酒醒日复日，网上网下年复年。
 * 但愿老死电脑间，不愿鞠躬老板前；
 * 奔驰宝马贵者趣，公交自行程序员。
 * 别人笑我忒疯癫，我笑自己命太贱；
 * 不见满街漂亮妹，哪个归得程序员？
 */
package selector;

import com.yyk.spi.selector.JdkSpiProxy;
import com.yyk.spi.selector.SpiApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class TestJdkProxy {


    public static void main(String... args) throws Exception {
        Map<String, IUserService> map = new HashMap<>();
        map.put("code", new IUserService() {
            @Override
            public String hello(String name, String age) {
                throw new RuntimeException("test");
//                return name + "=" + age;
            }

            @Override
            public void nihao(String name) {
                System.out.println("hello : " + name);
            }

            @Override
            public Map nihao() {
                Map map1 = new HashMap();
                map1.put("a","b");
                return map1;
            }
        });

        SpiApplicationContext.setSpiCode("code");
        IUserService userService = new JdkSpiProxy<>(IUserService.class,map).getSpiProxy();
        userService.nihao("yyk");
        System.out.println(userService.hello("a", "b"));
        System.out.println(userService.nihao());
    }
}
