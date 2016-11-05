#include "stdlib.h"
#include <stdio.h>
#include "/home/morpheus/CodeBlocks_workspqce/final/SimpleRedisClient.h"
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

    writer.StartObject();               // Between StartObject()/EndObject(),
    writer.Key("name");                 // output a key,
    writer.String("gaoyisheng");        // follow by a value.
    writer.Key("age");
    writer.Int(123);

    writer.EndObject();				    // Between StartObject()/EndObject(),

    std::cout << s.GetString() << std::endl;


    //toRedis
    SimpleRedisClient rc;

    rc.setHost("192.168.1.101");
    rc.setPort(6379);                   //redis服务器端口
    rc.auth("");                        //改成你的   链接redis服务器的用户
    rc.LogLevel(0);

    if(!rc)
    {
        printf("没有安装连接redis\n");
        return -1;
    }

    rc.getset("me",s.GetString());      //.set(const char *key, const char *val)方法同样适用，详见SimpleRedisClient代码

    rc.redis_close();
}
