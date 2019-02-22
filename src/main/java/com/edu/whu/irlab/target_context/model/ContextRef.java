package com.edu.whu.irlab.target_context.model;

//参考文献的上下文内容
   public class ContextRef
{
	public String ID=""; //ID用以匹配参考文献类，以确定具体的信息
	public String _label="";
	public String _preText=""; //当前参考文献所在的上文
	public String _curText=""; //当前参考文献所在的内容
	public String _postText=""; //当前参考文献所在的下文

	//确定上下文内容
	public final void setID(String txt)
	{
		ID = txt;
	}
	public final void setLabel(String txt)
	{
		_label = txt;
	}
	public final void setPre(String txt)
	{
		_preText = txt;
	}
	public final void setCur(String txt)
	{
		_curText = txt;
	}
	public final void setPost(String txt)
	{
		_postText = txt;
	}

	//输出上下文内容
	public final String getID()
	{
		return ID;
	}
	public final String getLabel()
	{
		return _label;
	}
	public final String getPre()
	{
		return _preText;
	}
	public final String getCur()
	{
		return _curText;
	}
	public final String getPost()
	{
		return _postText;
	}
	//输出内容到文档
	public final String getInfo()
	{
		String info =getID()+" "+getLabel()+"\t"+ getPre() + "\t" + getCur() + "\t" + getPost();
		return info;
	}
	public final void copy(ContextRef tem)
	{
		setID(tem.getID());
		setLabel(tem.getLabel());
		setPre(tem.getPre());
		setCur(tem.getPre());
		setPost(tem.getPost());
	}
}