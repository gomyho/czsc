package vip.qianbai.czsc.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import okhttp3.Call;
import okhttp3.Request;
import vip.qianbai.czsc.Constants;
import vip.qianbai.czsc.bean.TechStock;

/**
 * @author xiaofeng
 * @date 2017年9月12日
 */
public class BlogHtmlParser {
  static okhttp3.OkHttpClient httc = new okhttp3.OkHttpClient();
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
  
  
  public static String parseTechDoc(String html){
    org.jsoup.nodes.Document doc = Jsoup.parse(html);
    String docHtml = doc.getElementById("sina_keyword_ad_area2").outerHtml();
    return docHtml;
  }
  
  
  public static String removeHrefDiv(String fileName, String html){
    org.jsoup.nodes.Document doc = Jsoup.parse(html);
    List<org.jsoup.nodes.Node> childNodes = doc.getElementById("sina_keyword_ad_area2").childNodes();
    boolean hasImg = false;
    for (org.jsoup.nodes.Node node : childNodes) {
      if(node instanceof Element){
        Element n = (Element)node;
        hasImg = replaceImg(n);
        removeAhref(n);
      }
    }
    if(hasImg){
      System.out.println(fileName);
    }
    return doc.outerHtml();
  }

  private static boolean replaceImg(Element n) {
    Elements imgtag = n.getElementsByTag("img");
    boolean hasImg = false;
    if(imgtag != null && imgtag.size() >0){
      for (Element e : imgtag) {
        String realsrc = e.attr("real_src");
        if(realsrc != null && realsrc.length()>0){
          String fileName = storeImg(realsrc);
          if(fileName != null){
            e.attr("src", "./"+Constants.TECH_CUT_IMG+"/"+fileName);            
          }
          hasImg = true;
        }
      }
    }
    return hasImg;
  }
  private static String storeImg(String realsrc) {
    Request request = new Request.Builder().url(realsrc).get().build();
    Call call = httc.newCall(request );
    try {
      String name = StringUtils.substringAfterLast(realsrc, "/")+".gif";
      File file = new File(Constants.TECH_CUT_IMG_DIR,name);
      if(!file.exists()){
        InputStream byteStream = call.execute().body().byteStream();
        IOUtils.copy(byteStream, new FileOutputStream(file));        
        return name;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void saveTechBody(String dir,String fileName,String techBody){
    File file = new File(dir,fileName);
    try {
      file.createNewFile();
      IOUtils.write(techBody, new FileOutputStream(file),Charset.forName("utf-8"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void saveTechBody(String title,String techBody){
    saveTechBody(Constants.Tech_Dir,title+".html",techBody);
  }
  private static void removeAhref(Element n) {
    Elements atag = n.getElementsByTag("a");
    if(atag != null && atag.size() >0){
      atag.remove();
    }
  }
}
