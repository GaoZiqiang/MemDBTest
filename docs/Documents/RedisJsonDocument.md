Redis数据库存取并解析Json数据程序使用文档
=========================

一、实现原理
----
见流程图
二、向Redis数据库写入并转化为Json数据
--

2.1基本原理
=======

2.1.1原理图

flow
st1=>start: c++
en=>end: Java
op1=>operation: Json
op2=>operation: -Redis-
op3=>operation: Json 

st1->op1->op2->op3->en
note left of st1: zu 

2.2从C++序列化到JSON,存入redis
=======================

2.2.1环境清单
 1. linux
 2. gcc
 3. redis + hiredis(lib)
 4. cmake、make
 5. codeblocks(或者其他IDE)
2.2.2开始工作
redisSite: http://redis.io/ "http://redis.io/"
redisTutorialSite: http://www.yiibai.com/redis/redis_quick_guide.html "http://www.yiibai.com/redis/redis_quick_guide.html"
codeblocksSite: http://www.codeblocks.org/ "http://www.codeblocks.org/"
rapidjsonSite: https://github.com/miloyip/rapidjson "https://github.com/miloyip/rapidjson"
JSONSite: http://json.org/ "http://json.org/"
redisClientSite: http://redis.io/clients "http://redis.io/clients"
SRC: https://github.com/Levhav/SimpleRedisClient "https://github.com/Levhav/SimpleRedisClient"
 1. redis环境搭建
 2. codeblocks(IDE)配置
 3. rapidjson(C++ToJSON)下载
 4. hiredis依赖配置 (为 5. 做准备)
 5. SimpleRedisClient(toRedis)下载
 6. 最终成果
2.2.2.1 进入[redis官网][redisSite]自行配置，或者移步[redis入门教程][redisTutorialSite]有详细说明
2.2.2.2 进入[codeblocks官网][codeblocksSite]下载，或者在终端安装，完成之后新建一个项目备用
> \$ sudo apt-get install codeblocks

2.2.3 序列化我们用到的是[rapidjson][rapidjsonSite]开源项目,当然也可以去[JSON官网][JSONSite]翻一翻其他开源项目来使用。下面的命令请在workspace里刚才项目的路径下键入

> \$ git clone https://github.com/miloyip/rapidjson.git
> \$ cd rapidjson
> \$ sudo cmake -DCMAKE_INSTALL_PREFIX=/usr/local/include
> \$ make
> \$ make install


这有一个例子，倘若可以跑了，说明配置好了。
demo.cpp:
```C++
// rapidjson/example/simpledom/simpledom.cpp`
#include <string>
#include <stdlib.h>
#include "rapidjson/document.h"
#include "rapidjson/writer.h"
#include "rapidjson/stringbuffer.h"
#include <iostream>

using namespace rapidjson;
		
int main() {
	// 1. Parse a JSON string into DOM.
	const char* json = "{\"project\":\"rapidjson\",\"stars\":10}";
	Document d;
	d.Parse(json);

	// 2. Modify it by DOM.
	Value& s = d["stars"];
	s.SetInt(s.GetInt() + 1);

	// 3. Stringify the DOM
	StringBuffer buffer;
	Writer<StringBuffer> writer(buffer);
	d.Accept(writer);

	// Output {"project":"rapidjson","stars":11}
	std::cout << buffer.GetString() << std::endl;
	return 0;
}
```
2.2.4 hiredis环境配置：直接在终端

> \$ sudo apt-get install libhiredis-dev

1.2.5 存到redis我们用到的是[SimpleRedisClient][SRC],同样你也可以去[redis客户端][redisClientSite]翻一翻其他开源项目来使用。下面的命令请在workspace里刚才项目的路径下键入

> \$ git clone https://github.com/Levhav/SimpleRedisClient.git
> \$ cd SimpleRedisClient

再进入到对应的redis版本的目录下，把SimpleRedisClient.h拷贝到 刚才demo.cpp同级的目录下。并且在codeblocks中把它导入到项目中。demo使命已经完成，移除demo.cpp（不然会有冲突）
2.2.6 最终成果
final.cpp:
```c++

#include "stdlib.h"
#include <stdio.h>
#include "/home/morpheus/CodeBlocks_workspqce/final/SimpleRedisClient.h"//注意路径
#include <iostream>
#include <string>
#include "rapidjson/document.h"
#include "rapidjson/writer.h"
#include "rapidjson/stringbuffer.h"

using namespace std;
using namespace rapidjson;

int main(int argc, char *argv[])
{   
	//toJson
	StringBuffer s;
	Writer<StringBuffer> writer(s);

	writer.StartObject();                	    // Between StartObject()/EndObject()

	writer.Key("name");                	    // output a key,
	writer.String("gaoyisheng");                // follow by a value.
	writer.Key("age");
	writer.Int(123);                	    
	
	writer.EndObject();			    // Between StartObject()/EndObject(),

	std::cout << s.GetString() << std::endl;
	
	
	//toRedis
	SimpleRedisClient rc;
	
	rc.setHost("127.0.0.1");            //redis服务器端口
	rc.setPort(6379);
	rc.auth("");	                    //改成你的   链接redis服务器的用户
	rc.LogLevel(0);
	
	if(!rc)
	{
		printf("Соединение с redis не установлено\n");//没有安装连接redis
		return -1;
	}

	rc.getset("me",s.GetString());//set()方法同样适用，详见SimpleRedisClient代码

	rc.redis_close();
}

```

2.3 总结归纳
========

2.3.1 倘若在局域网内通过redis传递，需将redis配置改为 bind 0.0.0.0 ，在final.cpp中将 127.0.0.1 改为 服务器地址。
2.3.2 倘若是下载解压的redis，需自行打开服务器。./redis-server
2.3.3 在导入项目时，需注意路径问题，文件最好放在codeblocks的workspace下！


三、从Redis数据库读取并解析Json数据
--

3.1基本步骤说明
=========

3.1.1工作流程图
(MarkDown暂时无法上传图片)
Json数据解析工作流程文字解读：获取Json对象(Json Object)，解析为Java对象(Java Object)(比如存入Map,List,在这里我们是将Json对象解析为一个Class类)，然后读取该Java对象(Java Object)即可.
3.1.2导入jar包
需要导入几个符合版本要求的jar包.
分为以下几类:
a.Redis数据库的驱动Jedis
jedis-2.5.2.jar
b.解析JSon数据需要的jar包
json-lib-2.4-jdk15.jar、jackson-core-asl-1.9.13.jar、jackson-mapper-asl-1.9.13.jar
3.1.3编写实例类
a.编写Redis读取类
编写ClientRedis类，连接Redis数据库，读取数据库里的Json对象(Json Object)，调用JsonAnalysis类的readValue()方法，将Json对象(Json Object)解析为Java对象(Java Object)并转换为String类型的数据输出.
代码实现

    package cn.edu.sdut.json.analysis;
    
    /*@Author GaoZiqiang
     * date:2016-11-03
     * 作用:实现从Redis数据库读取Json数据并解析输出
     * */
    import redis.clients.jedis.Jedis;
    
    /*从数据库读取Json数据并解析
     * 涉及到Redis数据库连接，数据读取，Json数据解析
     * */
    public class ClientRedis {
        private static Jedis client;
    
        public static void main(String[] args) {
            new ClientRedis().setRelation();
            new ClientRedis().jsonAnalysis();
        }
    
        // 与Redis数据库建立连接
        public void setRelation() {
            try {
                String host = "127.0.0.1";
                int port = 6379;
                if (client == null) {
                    client = new Jedis(host, port);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    
        // 数据读取与解析操作
        public void jsonAnalysis() {
            try {
    
                // 输出json数据jsonData
                String jsonData = client.get("gao");
                System.out.println("读取并输出Redis内Json数据:\n" + jsonData);
                // 调用readValue()方法，解析该json数据为String数据stringData，并输出
                JsonConversion stringData = JsonAnalysis.readValue(jsonData, JsonConversion.class);
                System.out.println("解析并输出Json数据:\n" + stringData);
    
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    
    }

b.编写解析类
编写JsonAnalysis类调用jackson.map.ObjectMapper的readValue()方法，结合泛型方法，把Json对象(Json Object)转换为相应的Java对象(Java Object)，在这里我们选择将Json对象(Json Object)解析转换为特定类型的Json对象(Json Object)即Student[].class类型.
代码实现

    package cn.edu.sdut.json.analysis;
    
    /*@Author GaoZiqiang
     *date:2016-11-03
     * 作用:实现不同数据类型与json数据的相互转化
     * */
    import org.codehaus.jackson.map.ObjectMapper;
    import org.codehaus.jackson.map.PropertyNamingStrategy;
    
    public class JsonAnalysis {
    
        static ObjectMapper objectMapper;
    
        /**
         * 使用泛型方法，把json字符串转换为相应的JavaBean对象。
         * (1)转换为普通JavaBean：readValue(json,Student.class)
         * (2)转换为List:readValue(json,List
         * .class).但是如果我们想把json转换为特定类型的List，比如List<Student>，就不能直接进行转换了。
         * 因为readValue(json
         * ,List.class)返回的其实是List<Map>类型，你不能指定readValue()的第二个参数是List<
         * Student>.class，所以不能直接转换。
         * 我们可以把readValue()的第二个参数传递为Student[].class.然后使用Arrays
         * .asList();方法把得到的数组转换为特定类型的List。 
         * (3)转换为Map：readValue(json,Map.class)
         * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;我们使用泛型，得到的也是泛型
         *
         * @param content
         *            要转换的JavaBean类型
         * @param valueType
         *            原始json字符串数据
         * @return JavaBean对象
         */
        // 解析Json数据并转化为JavaBean对象
        public static <T> T readValue(String content, Class<T> valueType) {
            if (objectMapper == null) {
                objectMapper = new ObjectMapper();
            }
            try {
                return objectMapper.readValue(content, valueType);
    
            } catch (Exception e) {
                e.printStackTrace();
            }
    
            return null;
        }
    
        /**
         * 把JavaBean转换为json字符串 (1)普通对象转换：toJson(Student) (2)List转换：toJson(List)
         * (3)Map转换:toJson(Map) 我们发现不管什么类型，都可以直接传入这个方法
         *
         * JavaBean对象
         * 
         * @param object
         * @return json字符串
         */
        // 转化为json数据
        public static String toJSon(Object object) {
            if (objectMapper == null) {
                objectMapper = new ObjectMapper();
            }
            try {
                objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
                return objectMapper.writeValueAsString(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
    
            return null;
        }
    
    }

c.编写转化类
编写JsonConversion类，通过toString()方法实现将Json对象(Json Object)即Student[].class内的符合Java常规的String类型数据输出.
代码实现

    package cn.edu.sdut.json.analysis;
    
    /*@Author GaoZiqiang
     * date:2016-11-03
     * 作用:实现接受Json解析后的数据
     * */
    public class JsonConversion {
        // 设置属性
        public String name;
        public int age;
    
        // 构造器
        public JsonConversion() {
    
        }
    
        // getter和setter方法
        public String getName() {
            return name;
        }
    
        public void setName(String name) {
            this.name = name;
        }
    
        public int getAge() {
            return age;
        }
    
        public void setAge(int age) {
            this.age = age;
        }
    
        /*
         * 具体实现方法 转化为String字符串
         */
        @Override
        public String toString() {
            return "Person [name=" + name + ", age=" + age + "]";
        }
    
    }

3.2相关代码程序说明
===========

由ClientRedis()类从Redis数据库读取Json对象(Json Object)，再由JsonAnalysis()类实现将Json对象解析为Java对象(Java Object)，最后由JsonConversion()类将Java对象(Java Object)转换为符合Java常规的String类型的数据.

四、程序运行流程及结果
a.在Server端将数据(C++ Object)数据转换为Json数据(Json)插入Redis数据库;
b.在Client端将Json数据读出并解析为Java对象(Java Object)，并输出.
程序运行结构如下:

    读取并输出Redis内Json数据:
    {"name":"gaoziqiang","age":123}
    解析并输出Json数据:
    Person [name=gaoziqiang, age=123]