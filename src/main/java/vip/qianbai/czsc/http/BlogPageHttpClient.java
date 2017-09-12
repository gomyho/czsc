package vip.qianbai.czsc.http;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import vip.qianbai.czsc.JsonUtil;
import vip.qianbai.czsc.bean.TechStock;
import vip.qianbai.czsc.parser.BlogHtmlParser;

/**
 * @author xiaofeng
 * @date 2017年9月12日
 */
public class BlogPageHttpClient {
  static String host="http://blog.sina.com.cn/";
  static String keywords="教你炒股票";
  BlogSz client = null;
  
  
  public BlogPageHttpClient() {
    super();
    client = Feign.builder().logger(new Slf4jLogger()).client(new OkHttpClient()).target(BlogSz.class,host);
  }


  public List<TechStock> index(String keywords,String indexUrl) throws Exception{
    
    List<TechStock> result = new ArrayList<>(90);
    for(int i = 0 ; i < 12;i++){
      String index = client.grabIndex(i+1);
      CollectionUtils.addAll(result , BlogHtmlParser.getJsonpUrl(index,keywords));
    }
    return result.stream().sorted(Comparator.comparing(TechStock::getTitle)).collect(Collectors.toList());
  }
 
  public static void main(String[] args) {
    BlogPageHttpClient bc = new BlogPageHttpClient();
    try {
      List<TechStock> czscurls = bc.index(keywords, host);
      JsonUtil.toFile(czscurls, "F:/personal/czsc.txt");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  interface BlogSz{
    
    @RequestLine("GET /s/articlelist_1215172700_10_{page}.html")
    String grabIndex(@Param("page") int page);
  }
}
