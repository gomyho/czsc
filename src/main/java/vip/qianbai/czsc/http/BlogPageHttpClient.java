package vip.qianbai.czsc.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import okhttp3.Call;
import okhttp3.Request;
import vip.qianbai.czsc.Constants;
import vip.qianbai.czsc.bean.TechStock;
import vip.qianbai.czsc.parser.BlogHtmlParser;

/**
 * @author xiaofeng
 * @date 2017年9月12日
 */
public class BlogPageHttpClient {
  
  BlogSz client = null;
  static okhttp3.OkHttpClient httc = new okhttp3.OkHttpClient();
  
  public BlogPageHttpClient() {
    super();
    client = Feign.builder().logger(new Slf4jLogger()).client(new OkHttpClient(httc)).target(BlogSz.class,Constants.host);
  }


  public List<TechStock> index(String keywords,String indexUrl) throws Exception{
    
    List<TechStock> result = new ArrayList<>(90);
    for(int i = 0 ; i < 12;i++){
      String index = client.grabIndex(i+1);
      CollectionUtils.addAll(result , BlogHtmlParser.getJsonpUrl(index,keywords));
    }
    return result.stream().sorted(Comparator.comparing(TechStock::getTitle)).collect(Collectors.toList());
  }
 
  public List<TechStock> deserialize(){
    ObjectMapper mapper = new ObjectMapper();
    try {
      List<TechStock> techUrls = mapper.readValue(new FileInputStream(new File(Constants.JSON_FILE)), new TypeReference<List<TechStock>>() {});
      return techUrls;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  
  public String grabTechHtml(TechStock ts) throws IOException{
    Request request = new Request.Builder().url(ts.getUrl()).get().build();
    Call response = httc.newCall(request );
    return response.execute().body().string();
  }
  
  interface BlogSz{
    
    @RequestLine("GET /s/articlelist_1215172700_10_{page}.html")
    String grabIndex(@Param("page") int page);
    
  }
}
