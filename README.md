# spi-selector
spi-selector 接口选择器
项目中经常出现一个接口，多个实现，根据业务场景选择不同实现类，执行接口方法。
spi-selector 解决的就是怎么根据业务场景，优雅地选择对应的实现的问题。

## 使用方法
1、引入依赖。
2、启动类增加 @EnableSpiSelector({"com.xxx.yyy"}) 注解，com.xxx.yyy是需要自动选择的接口和实现类所在的包。
3、接口增加 SpiInterface注解。
4、实现类增加 @Component和 @SpiInterface(code = "BIZ_CODE") 注解，BIZ_CODE 为业务码。
5、需要自动选择实现类的地方，注解注入 对应的接口
6、调用接口方法钱增加一行代码：SpiApplicationContext.setSpiCode("BIZ_CODE");

## todo
基于注解和SpEL的自动选择方式目前正在测试中。
