package vip.qianbai.czsc.parser;

import java.util.List;
import java.util.stream.Collectors;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import vip.qianbai.czsc.bean.TechStock;

/**
 * @author xiaofeng
 * @date 2017年9月12日
 */
public class BlogHtmlParser {

  public static List<String> getSaxUrl(String indexpage) throws Exception{
    SAXReader saxReader=new SAXReader();
    Document doc = saxReader.read(indexpage);
    List<Node> nodes = doc.selectNodes("#module_928");
    System.out.println(nodes);
    return null;
  }
  public static List<TechStock> getJsonpUrl(String indexpage,String keywords) throws Exception{
    org.jsoup.nodes.Document doc = Jsoup.parse(indexpage);
    Elements els = doc.getElementById("module_928").getElementsByClass("atc_title");
    List<TechStock> result = els.stream().map(e->{
      Elements tag = e.getElementsByTag("a");
      String href = tag.attr("href");
      String title = tag.text();
      if(title.contains(keywords))
        return new TechStock(title,href);
      return null;
    }).filter(e->e!=null).collect(Collectors.toList());
    return result;
  }
  
  
}
