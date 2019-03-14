## 敏感信息屏蔽组件--InfoMask

### what's this
适用于spring boot的使用过滤器实现的敏感信息屏蔽组件，通过过滤器拦截返回对象的json，使用`*`对json中配置的字段按照自定义屏蔽规则进行屏蔽  

### Features
1.自定义屏蔽接口路径、屏蔽的字段与屏蔽规则  
2.支持动态更新配置文件

### QucikStart
* 使用maven生成jar包并在需要的项目中引入jar包  
* 配置扫描路径  
将路径```com.q276240802.hanlin```配置在SpringBoot启动类的注解上```@SpringBootApplication(scanBasePackages = {"com.q276240802.hanlin"})```
* InfoMask配置文件  
在工程的`resources`路径下新建`MaskInfo.yml`配置文件  
可配置需要屏蔽的接口路径`url`和屏蔽的字段与规则`fields`两部分内容  
example
```yaml
fields:
  name: (2,1)
  phone: (4,4)
  nameList: (1,1)(3,1)

url:
  - /api/contract/vehicle/proposal/*
```
url地址中支持通配符`*`  
字段屏蔽规则：括号中第一位数字表示从第几个字符起，第二位数字表示屏蔽几位字符  
如：`(2,1)`对应`李小明`的屏蔽结果为`李*明`  
`(4,4)`对应`13822334455`屏蔽结果为`138****4455`  
* 更新配置文件  
程序运行期间，可通过调用```http://localhost:8888/infoMask/updateConfig```接口动态更新配置文件

* 效果示例
```json
{
  "nameList": [
    "*佳",
    "*美",
    "*二*"
  ],
  "name": "佳*",
  "phone": "173****6446"
}
```
