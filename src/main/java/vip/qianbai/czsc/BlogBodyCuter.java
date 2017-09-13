package vip.qianbai.czsc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import vip.qianbai.czsc.parser.BlogHtmlParser;

/**
 * @author xiaofeng
 * @date 2017年9月13日
 */
public class BlogBodyCuter {

  public void cut(){
    File f = new File(Constants.Tech_Dir);
    File[] files = f.listFiles();
    for (File file : files) {
      cutFile(file);
    }
//    cutFile(new File(Constants.Tech_Dir,"教你炒股票1：不会赢钱的经济人，….html"));
  }

  private void cutFile(File file) {
    String html;
    try {
      html = IOUtils.toString(new FileInputStream(file), Charset.forName("UTF-8"));
      html = BlogHtmlParser.removeHrefDiv(file.getName(),html);
      BlogHtmlParser.saveTechBody(Constants.Tech_cut_Dir,file.getName(), html);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args) {
    BlogBodyCuter c = new BlogBodyCuter();
    c.cut();
  }
}
