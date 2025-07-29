package br.com.sankhya.ctba.integracaoapi;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.parser.ParseException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.sankhya.jape.util.JapeSessionContext;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImportActivities {
	static final Logger logger = Logger.getLogger(ImportActivities.class.getName());

	static String token = "63851621c5e07d0016e05752";

	public static void main(String[] args) throws Exception {
		//String atividade = null;
		//String deal_id = "63ebe3f0b46277000cc13d37";
		//atividade = activities(token, deal_id );
		//System.out.println(atividade);
	}

	public static String activities(String token, String deal_id) throws Exception {
		logger.log(Level.INFO, "[activities] INICIO");
		String atividade = "";

		OkHttpClient client = new OkHttpClient();
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		client = builder.build();

		Request request = getJson(token, deal_id);

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				logger.log(Level.INFO, "[activities] response = not Successful.");
				return atividade;
			} else {
				String res = response.body().string();

				atividade = getActivities(res, request);
				logger.log(Level.INFO, "[ImportActivities - activities] atividade: "+atividade);

				if (atividade.length() > 4000)
					atividade = atividade.substring(0,4000);

				return atividade;
			}
		} catch (Exception ex) {
			/*if (ex.getMessage() != null) {
				UtilIntegracao.salvaLogIntegracao("[ERROR]: " + ex.getMessage(), "activities");
				throw new Exception("Falha no JSON.");
			}
			*/
			logger.log(Level.INFO, "[ImportActivities - activities] ERROR JSON: "+ex.getMessage());
			UtilIntegracao.salvaLogIntegracao("[ERROR]: "+ex.getMessage(), "activities");
		} 

		logger.log(Level.INFO, "[activities] FIM");
		return atividade;
	}

	public static Request getJson(String token, String deal_id) throws Exception {
		logger.log(Level.INFO, "[ImportActivities - getJson] deal_id: "+deal_id);
		Request request = new Request.Builder()
				.url("https://crm.rdstation.com/api/v1/activities?token=63851621c5e07d0016e05752&deal_id="+deal_id)
				.get()
				.addHeader("accept", "application/json")
				.addHeader("content-type", "application/json") 
				.build();



		//https://crm.rdstation.com/api/v1/activities?token=63851621c5e07d0016e05752&deal_id=63ebe3f0b46277000cc13d37
		//		logger.log(Level.INFO, "[getJson]  jg----------------------- FIM ----------------------- ");
		return request;
	}

	private static String getActivities(String activi, Request request) throws Exception {
		logger.log(Level.INFO, "[ImportActivities - getActivities]  ----------------------- INICIO ----------------------- ");

		String atividade = "";
		JsonParser parser = new JsonParser();
		JsonObject jsobj = parser.parse(activi).getAsJsonObject();
		JsonArray posts = jsobj.getAsJsonArray("activities");

		for (JsonElement post : posts) {
			String userId = post.getAsJsonObject().get("user_id").getAsString();
			String text 		= post.getAsJsonObject().get("text").getAsString();
			//String date		= post.getAsJsonObject().get("date").getAsString();
			Timestamp dhatual = (Timestamp) JapeSessionContext.getProperty("dh_atual");

			//Timestamp dataActivity = convertStringToTimestamp(date);
			String name = ImportUsers.Users(token, userId);

			atividade = atividade + text + " \nPor: " + name + " Data: "+dhatual + "\n";

			logger.log(Level.INFO, "[ImportActivities - getActivities] atividade: "+atividade);
			logger.log(Level.INFO, "[ImportActivities - getActivities] userId: "+userId +", name: "+name +", dataActivity: "+dhatual);
		}

		logger.log(Level.INFO, "[ImportActivities - getActivities]  ----------------------- FIM ----------------------- ");
		return atividade;

	}
	public static Timestamp convertStringToTimestamp(String strDate) throws ParseException {
		System.out.println("strDate: " + strDate);
		OffsetDateTime odt = OffsetDateTime.parse(strDate);
		long timestamp = odt.toInstant().toEpochMilli();
		Timestamp ts = new Timestamp(timestamp);
		return ts;
	}

}
