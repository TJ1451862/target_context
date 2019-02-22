package com.edu.whu.irlab.target_context.service;

import com.edu.whu.irlab.target_context.model.ContextRef;
import com.edu.whu.irlab.target_context.model.Reference;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Form {
    public ArrayList<Reference> _listRef = new ArrayList<Reference>();
    public ArrayList<ContextRef> _listContext = new ArrayList<ContextRef>();

    private Document doc = new Document();
    private Element root;

    //start of 功能1
    //用于测试的文档Case paper.xml

    //读取文档内容(正常)
    public void readFile(String filePath){
        try{
            //将XML文档加载进来
            SAXBuilder builder=new SAXBuilder();
            doc=builder.build(filePath);
            root=doc.getRootElement();
            System.out.println("XML文档读取成功");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("XML文档读取失败");
        }
    }

    //处理单篇文档引文上下文(正常)
    public void solve(){
        //处理参考文献列表
        _listRef.clear();
        set_listRef(root);
        //处理正文中内容上下文
        _listContext.clear();
        set_listContext(root);
        //处理文档内容，确定上下文。发现一句有参考文献的，分别往前往后各找一句。
        System.out.println("处理引文上下文finish");
    }

    //确定参考文献链表（正常）
    public final void set_listRef(Element data){
        //找到参考文献列表，返回不是参考列表路径上的标签，简化计算
        if(data.getName().toString().equals("front"))return;
        if(data.getName().toString().equals("body"))return;
        if(data.getName().toString().equals("sec"))return;
        //处理参考文献列表上的内容
        if(data.getName().toString().equals("ref")){
            addRefList(data);
            return;
        }
        List<Element> list =data.getChildren();
        for (Element item:
             list) {
            set_listRef(item);
        }
    }
    //添加具体参考文献信息（正常）
    public void addRefList(Element data){
        Reference temRef=new Reference();
        String id=data.getAttribute("id").getValue();
        temRef.setID(id);
        String label=data.getChild("label").getValue();
        temRef.setLabel(label);
        data=data.getChild("mixed-citation");//C#代码：data = data.Element("mixed-citation");
        List<Element> list=data.getChildren();
        for (Element item:
             list) {
            if(item.getName().toString().equals("person-group")){
                //作者列表
                List<Element> nextItem=item.getChildren();
                for (Element nItem:
                     nextItem) {
                    if (nItem.getName().toString().equals("name")){
                        Element temp1=nItem.getChild("surname");
                        Element temp2=nItem.getChild("given-names");
                        String surname=temp1.getValue();
                        String givenName=temp2.getValue();
                        temRef.addAuthor(surname,givenName);
                    }else if (nItem.getName().toString().equals("etal")){
                        temRef.addEtAl();
                    }
                }
            }else {
                if (item.getName().toString().equals("year")){
                    String year=item.getValue();
                    temRef.setYear(year);
                }else if (item.getName().toString().equals("article-title")){
                    String title=item.getValue();
                    temRef.setTitle(title);
                }else if (item.getName().toString().equals("source")){
                    String source=item.getValue();
                    temRef.setSource(source);
                }else if (item.getName().toString().equals("volume")){
                    String volume=item.getValue();
                    temRef.setVolume(volume);
                }else if (item.getName().toString().equals("fpage")){
                    String fpage=item.getValue();
                    temRef.setFPage(fpage);
                }else if (item.getName().toString().equals("lpage")){
                    String lpage=item.getValue();
                    temRef.setLPage(lpage);
                }else if (item.getName().toString().equals("issue")){
                    String issue=item.getValue();
                    temRef.setIssue(issue);
                }else if (item.getName().toString().equals("ext-link")){
                    String doi=item.getValue();
                    temRef.setDoi(doi);
                }
            }
        }
        _listRef.add(temRef);
    }

    //确定上下文（正常）
    public void set_listContext(Element data){
        //找到正文 返回不是正文路径上的标签，简化计算
        if (data.getName().toString().equals("front")) return;
        if (data.getName().toString().equals("back")) return;
        //处理含有参考文献的段落
        if (data.getName().toString().equals("p")&&(data.getChildren().size()!=0)){
            addContext(data);
            return;
        }
        List<Element> list=data.getChildren();
        for (Element item:
             list) {
            set_listContext(item);
        }
    }
    //处理一段话（正常）
    public void addContext(Element data){
        //构造XML，此处对于句子划分有点小bug，根据英文句号划分时很难区分缩写标点和句号标点
        String tem=data.toString().replace(".","</span><span>").replace("?","</span><span>").replace("!","</span><span>").replace("<p>","<span>").replace("</p>","</span>");
        tem="<p>"+tem+"</p>";
        StringReader reader=new StringReader(tem);
        try {
            SAXBuilder builder=new SAXBuilder();
            Document temdoc=builder.build(reader);
            Element temRoot=temdoc.getRootElement();
            List<Element> list=temRoot.getChildren();
            for (Element item:
                 list) {
                if (item.getChildren().size()!=0){
                    List<Element> nList=item.getChildren();
                    //处理诸如[2-5]之类的
                    int temCnt=0;
                    int st=0;
                    int ed=0;
                    for (Element nextItem:
                         nList) {
                        if (nextItem.getName().toString().equals("xref")&&nextItem.getAttribute("ref-type").getValue().equals("bibr")){
                            ContextRef contextRef=new ContextRef();
                            String label=nextItem.getValue();
                            contextRef.setLabel(label);
                            if (temCnt==1) st=Integer.parseInt(label);
                            if (temCnt>1) ed=Integer.parseInt(label);
                            String ID=nextItem.getAttribute("rid").getValue();
                            contextRef.setID(ID);
                            if (temCnt>0){
                                Element pre=nList.get(temCnt-1);
                                if (pre!=null){
                                    contextRef.setPre(pre.toString());
                                }
                            }
                            contextRef.setCur(item.toString());
                            Element post=nList.get(temCnt+1);
                            if (post!=null){
                                contextRef.setPost(post.toString());
                            }
                            _listContext.add(contextRef);
                            //处理[3-5]之类的
                            if (st+1<=ed){
                                for (int i = st+1; i <ed ; i++) {
                                    ContextRef temContextRef=new ContextRef();
                                    temContextRef.copy(contextRef);
                                    String label1=String.valueOf(i);
                                    String id=_listRef.get(i-1).getID();
                                    temContextRef.setLabel(label1);
                                    temContextRef.setID(id);
                                    _listContext.add(temContextRef);
                                }
                                temCnt=0;
                                st=0;
                                ed=0;
                            }
                            //计数器+1
                            temCnt++;
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //将数据写入文档（正常）
    public void write(){
        //写参考文献列表
        writeRefList();
        //写参考文献上下文
        writeRefCon();
        System.out.println("参考文献列表和参考文献上下文写入成功");
    }
    //写参考文献列表（正常）
    public void writeRefList(){
        try{
            File writeName=new File("E:\\code\\target_context\\result\\RefList.txt");
            writeName.createNewFile();
            BufferedWriter out=new BufferedWriter(new FileWriter(writeName));
            if (_listRef.size()!=0){
                for (int i = 0; i <_listRef.size() ; i++) {
                    out.write(_listRef.get(i).getInfo()+"\r\n");
                }
            }else {
                System.out.println("参考文献列表为null,写入失败");
            }
            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //写参考文献上下文（正常）


    public void writeRefCon(){
        try{
            File writeName=new File("E:\\code\\target_context\\result\\RefCon.txt");
            writeName.createNewFile();
            BufferedWriter out=new BufferedWriter(new FileWriter(writeName));
            if (_listContext.size()!=0){
                for (int i = 0; i <_listContext.size() ; i++) {
                    out.write(_listContext.get(i).getInfo()+"\r\n");
                }
            }else {
                System.out.println("参考文献上下文为null，写入失败");
            }
            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    Document doc2=new Document();

    //start of 功能2
    //用于测试的文档Event Extraction in a Plot Advice Agent.xml
    //对于没有参考文献标签的XML的读取

    public void fileRead(String _paramFile){
        try{
            //加载XML文件
            SAXBuilder builder=new SAXBuilder();
            doc2=builder.build(new File(_paramFile));
            //显示加载成功
            System.out.println("XML文档读取成功");
        }catch (Exception e){
            System.out.println("XML文档读取失败,失败原因："+e.getMessage());
            e.printStackTrace();
        }
    }

    //读取参考文献列表信息
    //直接全部输出列表即可，不需要自己构造格式

    String RefList="";

    //找到参考文献所在段落（正常）
    public void refList(){
        Element temRoot=doc2.getRootElement();
        RefList=getTxtVal(temRoot);
        RefList=RefList.trim().replace("\t","").replace("\n\n","\n");
        if (RefList.equals("")){
            System.out.println("处理完毕，该文档找不到参考文献列表");
        }else {
            System.out.println("参考文献列表已成功读取");
        }
    }

    //找到参考文献所在段落，返回文本信息（正常）
    private String getTxtVal(Element data){
        if (data.getName().toString().equals("section")&&data.getAttribute("subtile_name").getValue().equals("References")){
            return data.getValue();
        }else {
            String info="";
            List<Element> list=data.getChildren();
            for (Element item:
                 list) {
                info=info+getTxtVal(item);
            }
            return info;
        }
    }
    //确定参考文献上下文内容
    public void context(){
        Element temRoot=doc2.getRootElement();
        _listContext.clear();
        getConTxt(temRoot);
        System.out.println("处理完毕，参考文献上下文已缓存，请及时保存");
    }
    //按章节确定上下文内容
    private void getConTxt(Element data){
        //不对参考文献列表章节进行处理
        if (data.getName().toString().equals("section")&&data.getAttribute("subtile_name").getValue().equals("References")){
            return;
        }
        //对其余section标签进行确定上下文处理
        else if (data.getName().toString().equals("section")){
            //提取纯文本
            String str=data.getValue().trim().replace("\t"," ").replace("\n"," ");
            str=str.replace("  "," ");
            //先分段，在分句，最后一句一句处理是否存在参考文献
            String[] strP=str.split("\n");
            for (int i = 0; i <strP.length ; i++) {
                //避免对et al.(2018)等类型的数据误处理
                String temS=strP[i].replace(".(","####".replace("<","###lt")).replace(">","###gt").replace("\"","quot").replace("'","apos").replace("&","###amp");
                //分句
                String strS=temS.replace(". ","</span><span>").replace("? ","</span><span>").replace("! ","</span><span>").trim();
                strS=strS.replace("####",".(");
                strS="<p><span>"+strS+"</span></p>";
                //添加标签后，按xml的格式处理
                getConRE(strS);
            }
        }else {
            //寻找下一个标签
            List<Element> list=data.getChildren();
            for (Element item:
                 list) {
                getConTxt(item);
            }
            return;
        }

    }
    //利用正则表达式，确定参考文献上下文内容
    private void getConRE(String data){
        try{
            StringReader reader=new StringReader(data);
            SAXBuilder builder=new SAXBuilder();
            Document temdoc=builder.build(reader);
            Element temRoot=temdoc.getRootElement();
            List<Element> list=temRoot.getChildren();
            //匹配参考文献
            //C#代码：string pattern = @"\((?:\w|\s|,|\.|;|-)*?\d{4}\)";//匹配参考文献
            //String pattern="\\((?:\\w|\\s|,|\\.|;|-)*?\\d{4}\\)";//匹配参考文献
            String patternStr="\\D";//判断是否只有年份
            Pattern pattern=Pattern.compile(patternStr);

            int listIndex=0;
            for (Element item:
                 list) {
                //正则判定当前句子中是否存在参考文献
                String temStr=item.getValue();
                for (String match: getMatchers(temStr)
                     ) {
                    String refStr=match.replace("(","").replace(")","");
                    //确定具体的参考文献信息
                    List<String> _listRefAns=new ArrayList<String>();
                    _listRefAns.clear();
                    String[] temStr1=refStr.split(";");//对括号内多个参考文献分列
                    //如果括号中的是第一个年份，说明即使后面都是年份，也需要往前找作者信息

                    Matcher matcher=pattern.matcher(temStr1[0]);
                    boolean isMatches=matcher.find();

                    if (!isMatches){
                        //作者信息在括号外，确定作者信息，往前找作者信息
                        //C#代码：string subStr = temStr.Substring(0, match.Index).Trim();
                        String subStr=temStr.substring(0,temStr.indexOf(match)).trim();
                        String[] temStr3=subStr.split(" ");

                        //测试
                        System.out.println("temStr3:"+temStr3.toString());

                        int len =temStr3.length;

                        //测试
                        System.out.println("temStr3.length:"+len);

                        int cnt=1;
                        if (len>=3){
                            if (temStr3[len - 2].equals("and") ||((temStr3[len - 1].equals("al.") || temStr3[len - 1].equals("al") || temStr3[len - 1].equals("al.,")) && temStr3[len - 2].equals("et"))){
                                cnt=3;
                            }
                        }

                        String tempStr=",";
                        for (int i = 1; i <=cnt ; i++) {
                            tempStr=" "+temStr3[len-1]+tempStr;
                        }
                        tempStr=tempStr.trim();
                        for (int i = 0; i <temStr1.length ; i++) {
                            _listRefAns.add(temStr1[i]);
                        }
                    }
                    //否则就不需要从括号外面寻找作者信息，不管里面有多少
                    //190118测试到这里啦
                    else {
                        for (int i = 0; i <temStr1.length ; i++) {
                            _listRefAns.add(temStr1[i]);
                        }
                    }

                    for (int i = 0; i <_listRefAns.size() ; i++) {
                        ContextRef contextRef=new ContextRef();
                        contextRef.setLabel(_listRefAns.get(i));

                        String temp;

                        if (listIndex>0){
                            Element pre=list.get(listIndex-1);//上一个节点
                            if (pre!=null){
                                temp="<span>"+pre.getContent().toString()+"</span>";
                                contextRef.setPre(temp.replace("###lt","<").replace("###gt",">").replace("###quot","\"").replace("###apos","'").replace("###amp","&"));
                            }
                        }

                        temp="<span>"+item.getContent().toString()+"</span>";
                        contextRef.setCur(temp.replace("###lt","<").replace("###gt",">").replace("###quot","\"").replace("###apos","'").replace("###amp","&"));//当前节点

                        if (listIndex<list.size()-1){
                            Element post=list.get(listIndex+1);
                            if (post!=null){
                                temp="<span>"+post.getContent().toString()+"</span>";
                                contextRef.setPost(temp.replace("###lt","<").replace("###gt",">").replace("###quot","\"").replace("###apos","'").replace("###amp","&"));
                            }
                        }//下一个节点
                        _listContext.add(contextRef);
                    }
                }
                listIndex++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //写入文档
    public void fileWrite(){
        writeRefList1();
        writeRefCon();
        System.out.println("参考文献列表与上下文内容已经成功写入文档");
    }

    public void writeRefList1(){
        try{
            File writeName=new File("E:\\code\\target_context\\result\\RefList.txt");
            writeName.createNewFile();
            BufferedWriter out=new BufferedWriter(new FileWriter(writeName));
            out.write(RefList);
            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

        //在输入字符串中搜索正则表达式的所有匹配项并返回所有匹配
    public List<String> getMatchers(String source){
        String regex="\\((?:\\w|\\s|,|\\.|;|-)*?\\d{4}\\)";//匹配参考文献
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;

    }

}
