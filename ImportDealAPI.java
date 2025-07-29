package br.com.sankhya.ctba.integracaoapi;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImportDealAPI {
	static final Logger logger = Logger.getLogger(ImportDealAPI.class.getName());
	static String token = "63851621c5e07d0016e05752";

	public static void main(String[] args) throws Exception {
		deals(token);
	}

	public static void deals(String token) throws Exception {
		logger.log(Level.INFO, "[deals] INICIO");
		boolean hasMore = true;
		int p = 1;

		while (hasMore) {
			OkHttpClient client = new OkHttpClient();
			OkHttpClient.Builder builder = new OkHttpClient.Builder();
			client = builder.build();

			//logger.log(Level.INFO, "[hasMore] ANTES GETSON");
			Request request = getJson(p, token);
			logger.log(Level.INFO, "[hasMore] request: "+request.toString());
			//logger.log(Level.INFO, "[hasMore] DEPOIS GETSON. P = "+p);

			try (Response response = client.newCall(request).execute()) {
				if (!response.isSuccessful()) {
					logger.log(Level.INFO, "[hasMore] RESPONSE SEM SUCESSO");
					throw new Exception("[deals] Falha no JSON: " + response.message());
				} else {
					//logger.log(Level.INFO, "[hasMore] ENTROU NO request");
					//logger.log(Level.INFO, "[hasMore] RESPOSTA:  "+response.message());

					String res = response.body().string(); 
					JsonParser parser = new JsonParser();
					JsonObject jsobj = parser.parse(res).getAsJsonObject();
					logger.log(Level.INFO, "[hasMore] hasMore 2: "+jsobj.get("has_more").getAsString());

					hasMore = jsobj.get("has_more").getAsString().equals("true");

					logger.log(Level.INFO, "[hasMore] "+hasMore+"/"+"página: "+p);

					getOportunidades(res, request);

					logger.log(Level.INFO, "[hasMore] res: "+res.toString());
					if (res == null || res.length() < 10) {
						hasMore = false;
					}
				}
			} catch (Exception ex) {
				hasMore = false;
				if (ex.getMessage() != null) {
					UtilIntegracao.salvaLogIntegracao("[ERROR]: " + ex.getMessage(), "deals");
				}
				throw new Exception("Falha no JSON."+ex.getMessage() );
			}

			p++;

			logger.log(Level.INFO, "[hasMore] hasMore 3: "+hasMore);
		}
		logger.log(Level.INFO, "[deals] FIM");

	}

	public static Request getJson(int p, String token) throws Exception {
		logger.log(Level.INFO, "[getJson]  ----------------------- INICIO ----------------------- page: " + p);
		Request request = (new Request.Builder())
				.url("https://crm.rdstation.com/api/v1/deals?token=" + token + "&page=" + p)
				.get()
				.addHeader("accept", "application/json")
				.addHeader("content-type", "application/json")
				.build();

		logger.log(Level.INFO, "[getJson]  url: "+request.url().toString());
		logger.log(Level.INFO, "[getJson]  ----------------------- FIM ----------------------- ");
		return request;
	}

	public static Timestamp convertStringToTimestamp(String strDate) throws Exception {
		logger.log(Level.INFO, "[convertStringToTimestamp]  ----------------------- INICIO ----------------------- ");
		logger.log(Level.INFO, "strDate: " + strDate);
		//OffsetDateTime odt = OffsetDateTime.parse(strDate);
		//LocalDate date = odt.toLocalDate();

		LocalDate date = LocalDate.parse(strDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		logger.log(Level.INFO, "LocalDate: " + date);
		//long timestamp = odt.toInstant().toEpochMilli();
		//Timestamp ts = new Timestamp(timestamp);

		Timestamp retorno  = Timestamp.valueOf(date.atStartOfDay());
		logger.log(Level.INFO, "Timestamp: " + retorno);

		logger.log(Level.INFO, "[convertStringToTimestamp]  ----------------------- FIM ----------------------- ");
		return retorno;
	}

	private static void getOportunidades(String deals, Request request) throws Exception {
		logger.log(Level.INFO, "[getOportunidades]  ----------------------- INICIO ----------------------- ");
		JsonParser parser = new JsonParser();
		JsonObject jsobj = parser.parse(deals).getAsJsonObject();
		JsonArray posts = jsobj.getAsJsonArray("deals");

		for (JsonElement post : posts) {
			JsonObject postObject = post.getAsJsonObject();
			List<Contact> contact = new ArrayList<>();
			Deal deal = new Deal();
			Organization org = new Organization();
			Vendedor vendedor = new Vendedor();
			Product prod = new Product();
			try {
				JsonElement dealStageElement = post.getAsJsonObject().getAsJsonObject("deal_stage").get("nickname");
				JsonElement updatedAtElement = post.getAsJsonObject().getAsJsonObject("deal_stage").get("updated_at");
				if (dealStageElement != null && !dealStageElement.isJsonNull())
					deal.setDeal_stage(dealStageElement.getAsString()); 
				if (updatedAtElement != null && !updatedAtElement.isJsonNull())
					deal.setUpdated_atDeal_stageStr(updatedAtElement.getAsString()); 
				JsonElement idElement = post.getAsJsonObject().get("id");
				JsonElement nameElement = post.getAsJsonObject().get("name");
				JsonElement vlrTotDealElement = post.getAsJsonObject().get("amount_total");
				JsonElement vlrUniDealElement = post.getAsJsonObject().get("amount_unique");
				JsonElement vlrMesDealElement = post.getAsJsonObject().get("amount_monthly");
				JsonElement updatedAtElement1 = post.getAsJsonObject().get("updated_at");
				JsonElement ratingElement = post.getAsJsonObject().get("rating");

				logger.log(Level.INFO, "[getOportunidades] PASSO 1 - deal  ");

				if (idElement != null && !idElement.isJsonNull()) {
					deal.setIdDeal(idElement.getAsString()); 
					logger.log(Level.INFO, "[getOportunidades] deal.idElement:  "+idElement.toString());
				}
				if (nameElement != null && !nameElement.isJsonNull()) {
					deal.setNameDeal(nameElement.getAsString()); 
					logger.log(Level.INFO, "[getOportunidades] deal.nameElement:  "+nameElement.toString());
				}
				if (vlrTotDealElement != null && !vlrTotDealElement.isJsonNull()) {
					deal.setVlrTotDeal(vlrTotDealElement.getAsString()); 
					logger.log(Level.INFO, "[getOportunidades] deal.vlrTotDealElement:  "+vlrTotDealElement.toString());
				}
				if (vlrUniDealElement != null && !vlrUniDealElement.isJsonNull()) {
					deal.setVlrUniDeal(vlrUniDealElement.getAsString()); 
					logger.log(Level.INFO, "[getOportunidades] deal.vlrUniDealElement:  "+vlrUniDealElement.toString());
				}
				if (vlrMesDealElement != null && !vlrMesDealElement.isJsonNull()) {
					deal.setVlrMesDeal(vlrMesDealElement.getAsString()); 
					logger.log(Level.INFO, "[getOportunidades] deal.vlrMesDealElement:  "+vlrMesDealElement.toString());
				}
				if (updatedAtElement1 != null && !updatedAtElement1.isJsonNull()) {
					deal.setUpdated_atStr(updatedAtElement1.getAsString()); 
					logger.log(Level.INFO, "[getOportunidades] deal.updatedAtElement1:  "+updatedAtElement1.toString());
				}
				if (ratingElement != null && !ratingElement.isJsonNull()) {
					deal.setRatingDeal(ratingElement.getAsBigDecimal()); 
					logger.log(Level.INFO, "[getOportunidades] deal.ratingElement:  "+ratingElement.toString());
				}

				deal.setUpdated_at(convertStringToTimestamp(deal.getUpdated_atStr()));
				logger.log(Level.INFO, "[getOportunidades] getUpdated_at:  "+deal.getUpdated_at().toString()); 


				logger.log(Level.INFO, "[getOportunidades] PASSO 2 - organization  ");

				if (postObject.has("organization")) {
					JsonObject organizationObj = post.getAsJsonObject().getAsJsonObject("organization");
					JsonElement idClientElement = organizationObj.get("id");
					JsonElement nameClientElement = organizationObj.get("name");
					JsonElement idUserElement = organizationObj.getAsJsonObject("user").get("id");
					JsonElement nameUserElement = organizationObj.getAsJsonObject("user").get("name");
					JsonElement emailUserElement = organizationObj.getAsJsonObject("user").get("email");

					if (idClientElement != null && !idClientElement.isJsonNull()) {
						org.setIdClient(idClientElement.getAsString()); 
						logger.log(Level.INFO, "[getOportunidades] org.idClientElement:  "+idClientElement.toString());
					}

					if (nameClientElement != null && !nameClientElement.isJsonNull()) {
						org.setNameClient(nameClientElement.getAsString().trim()); 
						logger.log(Level.INFO, "[getOportunidades] org.nameClientElement:  "+nameClientElement.toString());
					}

					if (idUserElement != null && !idUserElement.isJsonNull()) {
						org.setIdUser(idUserElement.getAsString());
						logger.log(Level.INFO, "[getOportunidades] org.idUserElement:  "+idUserElement.toString());
					}

					if (nameUserElement != null && !nameUserElement.isJsonNull()) {
						org.setNameUser(nameUserElement.getAsString()); 
						logger.log(Level.INFO, "[getOportunidades] org.nameUserElement:  "+nameUserElement.toString());
					}

					if (emailUserElement != null && !emailUserElement.isJsonNull()) {
						org.setEmailUser(emailUserElement.getAsString()); 
						logger.log(Level.INFO, "[getOportunidades] org.emailUserElement:  "+emailUserElement.toString());
					}
				} else {
					continue;
				}

				logger.log(Level.INFO, "[getOportunidades] PASSO 3 - user ");
				if (postObject.has("user")) {
					JsonObject userObj = post.getAsJsonObject().getAsJsonObject("user");
					JsonElement idVendElement = userObj.get("id");
					JsonElement nameVendElement = userObj.get("name");
					JsonElement emailVendElement = userObj.get("email");
					JsonElement nNameVendElement = userObj.get("nickname");

					if (idVendElement != null && !idVendElement.isJsonNull()) {
						vendedor.setIdVend(idVendElement.getAsString()); 
						logger.log(Level.INFO, "[getOportunidades] vendedor.idVendElement:  "+idVendElement.toString());
					}

					if (nameVendElement != null && !nameVendElement.isJsonNull()) {
						vendedor.setNameVend(nameVendElement.getAsString()); 
						logger.log(Level.INFO, "[getOportunidades] vendedor.nameVendElement:  "+nameVendElement.toString());
					}

					if (emailVendElement != null && !emailVendElement.isJsonNull()) {
						vendedor.setEmailVend(emailVendElement.getAsString());
						logger.log(Level.INFO, "[getOportunidades] vendedor.emailVendElement:  "+emailVendElement.toString());
					}

					if (nNameVendElement != null && !nNameVendElement.isJsonNull()) {
						vendedor.setnNameVend(nNameVendElement.getAsString());
						logger.log(Level.INFO, "[getOportunidades] vendedor.nNameVendElement:  "+nNameVendElement.toString());
					}
				} 

				logger.log(Level.INFO, "[getOportunidades] PASSO 4 - contacts ");
				JsonArray contactsJson = post.getAsJsonObject().getAsJsonArray("contacts");
				for (JsonElement contactElement : contactsJson) {
					String title = null;
					String name = null;
					String emaill = null;
					if (contactElement.getAsJsonObject().has("name") && !contactElement.getAsJsonObject().get("name").isJsonNull())
						name = contactElement.getAsJsonObject().get("name").getAsString(); 
					if (contactElement.getAsJsonObject().has("title") && !contactElement.getAsJsonObject().get("title").isJsonNull())
						title = contactElement.getAsJsonObject().get("title").getAsString(); 
					if (contactElement.getAsJsonObject().has("emails") && !contactElement.getAsJsonObject().getAsJsonArray("emails").isJsonNull()) {
						JsonArray emails = contactElement.getAsJsonObject().getAsJsonArray("emails");
						for (JsonElement email : emails) {
							if (!email.getAsJsonObject().get("email").isJsonNull())
								emaill = email.getAsJsonObject().get("email").getAsString(); 
						} 
					} 
					List<Phone> ph = new ArrayList<>();
					if (contactElement.getAsJsonObject().has("phones") && !contactElement.getAsJsonObject().getAsJsonArray("phones").isJsonNull()) {
						JsonArray phones = contactElement.getAsJsonObject().getAsJsonArray("phones");
						for (JsonElement phone : phones) {
							String phonee = null;
							String type = null;
							if (!phone.getAsJsonObject().get("phone").isJsonNull())
								phonee = phone.getAsJsonObject().get("phone").getAsString(); 
							if (!phone.getAsJsonObject().get("type").isJsonNull())
								type = phone.getAsJsonObject().get("type").getAsString(); 
							ph.add(new Phone(phonee, type));

							logger.log(Level.INFO, "[getOportunidades - phones] phonee:  "+phonee.toString() 
							+"type" + type );
						} 
					} 
					contact.add(new Contact(name, title, emaill, ph));
				} 

				logger.log(Level.INFO, "[getOportunidades] PASSO 5 - deal_products ");
				JsonArray produtos = post.getAsJsonObject().getAsJsonArray("deal_products");
				if (produtos != null)
					for (JsonElement produto : produtos) {
						if (produto != null && !produto.isJsonNull() && produto.getAsJsonObject().has("id")) {
							if (produto.getAsJsonObject().has("id"))
								prod.setIdProd(produto.getAsJsonObject().get("id").getAsString()); 

							if (produto.getAsJsonObject().has("name"))
								prod.setNameProd(produto.getAsJsonObject().get("name").getAsString()); 

							if (produto.getAsJsonObject().has("amount"))
								prod.setQtdNeg(produto.getAsJsonObject().get("amount").getAsString()); 

							if (produto.getAsJsonObject().has("discount_type"))
								prod.setTipoDesc(produto.getAsJsonObject().get("discount_type").getAsString()); 

							if (produto.getAsJsonObject().has("price"))
								prod.setVlrProd(produto.getAsJsonObject().get("price").getAsString()); 

							if (produto.getAsJsonObject().has("discount"))
								prod.setVlrDesc(produto.getAsJsonObject().get("discount").getAsString()); 

							if (!produto.getAsJsonObject().get("total").isJsonNull())
								prod.setVlrTot(produto.getAsJsonObject().get("total").getAsBigDecimal()); 

							if (produto.getAsJsonObject().get("updated_at") != null) {
								prod.setUpdated_at(produto.getAsJsonObject().get("updated_at").getAsString());
								prod.setUpdated_atProduct(convertStringToTimestamp(prod.getUpdated_at()));
							} 

							logger.log(Level.INFO, "[getOportunidades - deal_products] id: "+prod.getIdProd() 
							+"name" + prod.getNameProd()
							+"amount" + prod.getQtdNeg()
							+"price" + prod.getVlrProd()
									);
						} 
					}  
			} catch (Exception e) {
				logger.log(Level.INFO, "***********************  ERRO ****************************");
				throw new Exception(e.getMessage());
			} 

			logger.log(Level.INFO, "***************************************************");
			logger.log(Level.INFO, "");

			Timestamp ultimaExecucao = NativeSql.getTimestamp("DTULTIMAEXECUCAO", "AD_CTRLEXECUCAOCRM", "IDDTEXECCRM = 1");
			logger.log(Level.INFO, "[getOportunidades]  ultimaExceucao   : " + ultimaExecucao + 
					"\n                          updated_atDeal   : " + deal.getUpdated_at() + 
					"\n                          updated_atProduct: " + prod.getUpdated_atProduct());

			int ultimoDeal = deal.getUpdated_at().compareTo(ultimaExecucao);
			int ultimoProduct = -1;

			BigDecimal numOSOld = buscarOrdemServico(deal.getIdDeal());
			if (numOSOld == null) {
				ultimoDeal = 1;	
			}

			if (prod.getUpdated_atProduct() != null)
				ultimoProduct = prod.getUpdated_atProduct().compareTo(ultimaExecucao); 

			logger.log(Level.INFO, "[getOportunidades] ultimoDeal  " + ultimoDeal + " ultimoProduct  " + ultimoProduct);
			logger.log(Level.INFO, "[getOportunidades] numOSOld  " + numOSOld);
			logger.log(Level.INFO, "[getOportunidades] getDeal_stage  " + deal.getDeal_stage().toString());


			//if (ultimoDeal >= 0 || ultimoProduct >= 0) {
			//BigDecimal numOSOld = buscarOrdemServico(deal.getIdDeal());
			if(numOSOld == null) {
				numOSOld = BigDecimal.ZERO;
			}

			//"Negociação", "nickname": "N"
			//"Definição de escopo e budget", "nickname": "DDEEB"
			//"Concluídos", "nickname": "C"
			//"Lead", "nickname": "L"

			if(numOSOld != null && !deal.getDeal_stage().equals("L")) {
				UtilIntegracao.ProcessarDeal(deal, vendedor, prod, numOSOld, token, contact, org);
			}
			//} 
		} 
		logger.log(Level.INFO, "[getOportunidades]  ----------------------- FIM ----------------------- ");
	}

	@SuppressWarnings("unchecked")
	private static BigDecimal buscarOrdemServico(String idDeal) throws Exception {
		logger.log(Level.INFO, "[buscarOrdemServico]  ----------------------- Inicio ----------------------- ");
		String nomeInstancia = "OrdemServico";
		EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
		Collection<DynamicVO> notasList = dwfFacade.findByDynamicFinderAsVO(
				new FinderWrapper(nomeInstancia, "this.AD_IDDEALCRM = ? ", new Object[] { idDeal }));

		if (notasList.isEmpty()) {
			logger.log(Level.INFO, "[buscarOrdemServico]  NAO ENCONTRADA para o ID: " + idDeal);
			return null;

		} else {
			DynamicVO funVO = notasList.iterator().next();
			if (funVO.asString("SITUACAO") == null || funVO.asString("SITUACAO").equals("P")) {
				logger.log(Level.INFO, "[buscarOrdemServico]  OS: " + funVO.asBigDecimal("NUMOS") + " Situacao: " + funVO.asString("SITUACAO"));
				return funVO.asBigDecimal("NUMOS");
			} 			
		}

		logger.log(Level.INFO, "[buscarOrdemServico]  ----------------------- Fim ----------------------- ");
		return null;
	}
}
