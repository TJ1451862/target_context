package com.edu.whu.irlab.target_context.model;

import java.util.ArrayList;

//参考文献类，内有参考文献id，参考文献对应的相关信息，例如标题等
//id用来匹配文中的上下文内容
public class Reference
{
	private String _ID="";
	private String _label = "";
	private ArrayList<String> _author = new java.util.ArrayList<String>(); //作者链表，每一个作者用surname，givenName的形式保存
	private String _year=""; //发表年份
	private String _title=""; //标题
	private String _source=""; //来源
	private String _volume=""; //卷号
	private String _issue; //期号
	private String _fpage=""; //起始页码
	private String _lpage=""; //终止页码
	private String _doi=""; //DOI

	public Reference()
	{
		_author.clear();
	}
	//从文件中读取信息
	public final void setID(String ID)
	{
		_ID = ID;
	}
	public final void setLabel(String Label)
	{
		_label = Label;
	}
	public final void setYear(String Year)
	{
		_year = Year;
	}
	public final void setTitle(String Title)
	{
		_title = Title;
	}
	public final void setSource(String Source)
	{
		_source = Source;
	}
	public final void setVolume(String Volume)
	{
		_volume = Volume;
	}
	public final void setIssue(String Issue)
	{
		_issue = Issue;
	}
	public final void setFPage(String FPage)
	{
		_fpage = FPage;
	}
	public final void setLPage(String LPage)
	{
		_lpage = LPage;
	}
	public final void setDoi(String Doi)
	{
		_doi = Doi;
	}

	public final void addAuthor(String SurName, String givenName)
	{
		_author.add(SurName + ", " + givenName+"., ");
	}

	public final void addEtAl()
	{
		_author.add("et al., ");
	}
	//读取内容
	public final String getID()
	{
		return _ID;
	}
	public final String getLabel()
	{
		return _label;
	}
	public final String getYear()
	{
		return _year;
	}
	public final String getTitle()
	{
		return _title;
	}
	public final String getSource()
	{
		return _source;
	}
	public final String getVolume()
	{
		return _volume;
	}
	public final String getIssue()
	{
		return _issue;
	}
	public final String getFPage()
	{
		return _fpage;
	}
	public final String getLPage()
	{
		return _lpage;
	}
	public final String getDoi()
	{
		return _doi;
	}

	public final String getAuthor()
	{
		String info = "";
		for (int i = 0; i < _author.size(); i++)
		{
			info = info + _author.get(i);
		}
		if (info.length() <= 0)
		{
			return "";
		}
		info = info.substring(0, info.length() - 1);
		return info;
	}

   //输出信息到文档
	public final String getInfo()
	{
		String info = getAuthor() + getTitle() + ". " + getSource() + "," +getYear().toString()+","+ getVolume();
		if (_issue != null)
		{
			info = info + "(" + getIssue() + ")";
		}
		info = info + ": " + getFPage() + "-" + getLPage()+". "+getDoi();
		return info;
	}
}