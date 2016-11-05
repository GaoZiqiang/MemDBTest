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

			// 输出json对象jsonObject
			String jsonObject = client.get("gao");
			System.out.println("读取并输出Redis内Json数据:\n" + jsonObject);
			// 调用readValue()方法，解析该json数据转换为Person()的对象，并输出
			Person person = JsonAnalysis.readValue(jsonObject, Person.class);
			System.out.println("解析并输出person对象:\n" + person);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
