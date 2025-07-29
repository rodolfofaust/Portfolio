package br.com.sankhya.ctba.integracaoapi;


import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/* @author Rodolfo
 * Integracao/API Sankhya - CRM Rd Station
 */

/*

https://api.rd.services/auth/token
https://crm.rdstation.com/api/v1/users?token=MY_TOKEN
TOKEN -> 63851621c5e07d0016e05752
https://www.rdstation.com/contato/?code=6028b1750d4ef43db80994994ee6468f
https://api.rd.services/auth/dialog?client_id=797bae9b-9c00-43ca-8d99-800b571ae345&redirect_uri=e83a005091e44922a05278330e6f3497&state=state

 "client_id": "797bae9b-9c00-43ca-8d99-800b571ae345",
 "client_secret": "e83a005091e44922a05278330e6f3497",
 "code": "6028b1750d4ef43db80994994ee6468f"
 */

public class ImportClientesAPI {
	static final Logger logger = Logger.getLogger(ImportClientesAPI.class.getName());

	//static String token = "63851621c5e07d0016e05752";

	//public static String organizations(String token, String nameClient) throws Exception {
	public static String organizations(String token, String idClient) throws Exception {
		logger.log(Level.INFO, "[organizations]  ----------------------- INICIO ----------------------- ");
		String  cnpj = null;

		OkHttpClient client = new OkHttpClient();
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		client = builder.build();

		Request request = getJson(token, idClient); 
		logger.log(Level.INFO, "[request] "+request);

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				logger.log(Level.INFO, "[organizations] response = not Successful.");
				return cnpj;
				//throw new Exception("[organizations] Falha no JSON: " + response.message());
			} else {
				//resposta = response.message();
				String res = response.body().string(); 

				logger.log(Level.INFO, "[res] "+res);

				cnpj = getCliente(res);
			}
			//response.body().close();
		} catch (Exception ex) {
			logger.log(Level.INFO, "[organizations] Falha na execução - getJson."+ex.getMessage());
			UtilIntegracao.salvaLogIntegracao("[ERROR]: "+ex.getMessage(), "Users");
			//throw new Exception("[organizations] Falha no JSON: " + resposta);
			return cnpj;
		}

		logger.log(Level.INFO, "[organizations] ----------------------- FIM ----------------------- ");
		return cnpj;
	}

	public static Request getJson(String token, String idClient) throws Exception {
		logger.log(Level.INFO, "[getJson] INICIO - idClient: "+idClient);
		Request request = new Request.Builder()
				//.url("https://crm.rdstation.com/api/v1/organizations?token="+token+"&q="+nameClient)
				.url("https://crm.rdstation.com/api/v1/organizations/"+idClient+"?token="+token)
				.get()
				.addHeader("accept", "application/json")
				.addHeader("content-type", "application/json") 
				.build();

		//https://crm.rdstation.com/api/v1/organizations?token=63851621c5e07d0016e05752&q=Mondelez
		// exemplo -> https://crm.rdstation.com/api/v1/organizations/64c816c3b6a7da0019a03f20?token=63851621c5e07d0016e05752
		logger.log(Level.INFO, "[getJson] FIM");
		return request;
	}

	private static String getCliente(String clients) throws Exception {
		logger.log(Level.INFO, "[getClientes] ----------------------- INICIO ----------------------- ");
		String cnpj = null; 

		JsonParser parser = new JsonParser();
		JsonObject jsobj = parser.parse(clients).getAsJsonObject();
		//JsonArray posts = jsobj.getAsJsonArray("organizations");
		
		if (!jsobj.isJsonNull()) {
			JsonArray posts = jsobj.getAsJsonObject().getAsJsonArray("organization_custom_fields");
			for (JsonElement post : posts) {
				//CNPJ
				//JsonArray customFields = post.getAsJsonObject().getAsJsonArray("custom_fields");
				//for (JsonElement customField : customFields) {
					if (!post.getAsJsonObject().get("value").isJsonNull()) {
						cnpj = post.getAsJsonObject().get("value").getAsString();
					}
				
			}
		}
		if (cnpj != null) {
			cnpj = UtilIntegracao.ajustaNumero(cnpj); 
		}

		logger.log(Level.INFO, "[getCliente] CNPJ: " +cnpj);

		logger.log(Level.INFO, "[getClientes]   ----------------------- FIM ----------------------- ");
		return cnpj;
	}


}
